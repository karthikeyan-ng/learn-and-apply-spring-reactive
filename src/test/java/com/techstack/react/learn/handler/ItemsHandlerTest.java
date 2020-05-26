package com.techstack.react.learn.handler;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@Slf4j
@ActiveProfiles("test")
class ItemsHandlerTest {

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

    @Test
    @DisplayName("Get all Items")
    void getAllItems() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    @DisplayName("get One Item by using Id (Valid) Success")
    void getOneItem_Success() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 2400.0);
    }

    @Test
    @DisplayName("get One Item by using Invalid Id")
    void getOneItem_NotFound() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF123")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Create an Item")
    void createItem() {
        Item item = new Item(null, "IPad Pro 12 inch", 1399.99);
        webTestClient
                //Given
                .post()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)

                //When
                .exchange()

                //Then
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("IPad Pro 12 inch")
                .jsonPath("$.price").isEqualTo(1399.99);

    }

    @Test
    @DisplayName("Delete an Item")
    void deleteItem() {
        webTestClient
                //Given
                .delete()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC123")
                .accept(MediaType.APPLICATION_JSON)

                //When
                .exchange()

                //Then
                .expectStatus().isOk()
                .expectBody(Void.class);

    }

    @Test
    @DisplayName("Update an Item Price Value")
    void updateItem_PriceValue() {
        double newPrice = 400.0;
        Item item = new Item(null, "Apple MacBook Pro 16", newPrice);

        webTestClient
                //Given
                .put()
                .uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)

                //When
                .exchange()

                //Then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", newPrice);
    }
}