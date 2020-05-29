package com.techstack.react.app.controller.v1;

import com.techstack.react.app.consts.ItemConstants;
import com.techstack.react.app.document.ItemCapped;
import com.techstack.react.app.repository.ItemReactiveCappedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles("test")
class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions
                        .empty()
                        .maxDocuments(20) //How many max documents this can store at a given point
                        .size(50000)  //What is the size of the whole capped collection
                        .capped());

        Flux<ItemCapped> itemCappedFlux = Flux
                .interval(Duration.ofSeconds(1))  //Also change it to .ofMilliSeconds(1)
                .map(value -> new ItemCapped(null, "Random Item "+ value, (100.0 + value)))
                .take(5); //<= limit the element to 5

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("Inserted item is {}", itemCapped));
    }

    @Test
    @DisplayName("Stream All Items")
    void testStreamAllItems() {
        Flux<ItemCapped> itemCappedFlux = webTestClient
                .get()
                .uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier
                .create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();

    }
}