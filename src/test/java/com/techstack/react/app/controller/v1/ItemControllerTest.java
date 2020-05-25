package com.techstack.react.app.controller.v1;

import com.techstack.react.app.consts.ItemConstants;
import com.techstack.react.app.document.Item;
import com.techstack.react.app.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles("test")
//@DirtiesContext
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> data() {
        return List.of(
                new Item(null, "Apple Ipad", 350.0),
                new Item(null, "Samsung Tab", 450.0),
                new Item(null, "LG TV", 850.0),
                new Item("ABC123", "Apple MacBook Pro 16", 2400.0)
        );
    }

    @BeforeEach
    void setup() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> log.info("From Test: Inserted item is {}", item))
                .blockLast();
    }

    /**
     * TIP:
     * When you run this test case, you can observe the testcase logs that it would also
     * executes the ItemDataInitializer (CommandLineRunner) data load method to store data to
     * the MongoDB database.
     *
     * Why both CommandLineRunner and this setup() loading?
     * Because we have used @SpringBootTest which would create a full application bean context both
     * source and test types.
     *
     * How to fix this issue?
     * Step1: create a profile called "test" in application.yml
     * Step2: add @ActiveProfiles("test") in this test class
     * Step3: ItemDataInitializer class @Profile("!test")
     *
     */
    @Test
    @DisplayName("Get all Items - approach 1")
    void getAllItems_Approach1() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3);
    }

    @Test
    @DisplayName("Get all Items - approach 2")
    void getAllItems_Approach2() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> assertTrue(Objects.nonNull(item.getId())));
                });
    }

    @Test
    @DisplayName("Get all Items - approach 3")
    void getAllItems_Approach3() {
        Flux<Item> itemsFlux = webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        //This would call over the Network and get the count
        StepVerifier
                .create(itemsFlux.log("Value from Network : "))
                .expectNextCount(3)
                .verifyComplete();
    }

    /**
     * Here in this testcase, using jsonPath, you can assert the value
     * comparision using "$.price" expression. Here $ is a root element.
     */
    @Test
    @DisplayName("get One Item by using Id (Valid)")
    void getOneItem() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 2400.0);
    }

    @Test
    @DisplayName("get One Item by using Id (In Valid)")
    void getOneItem_NotFound() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF123")
                .exchange()
                .expectStatus().isNotFound();
    }
}