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
import reactor.core.publisher.Mono;
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
                .hasSize(4);
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
                .hasSize(4)
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
                .expectNextCount(4)
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

    @Test
    @DisplayName("Create an Item")
    void createItem() {
        Item item = new Item(null, "IPad Pro 12 inch", 1399.99);
        webTestClient
                //Given
                .post()
                .uri(ItemConstants.ITEM_END_POINT_V1)
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
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC123")
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
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABC123")
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

    @Test
    @DisplayName("Update an Item Price Value Using Invalid Id")
    void updateItem_InvalidId() {
        double newPrice = 400.0;
        Item item = new Item(null, "Apple MacBook Pro 16", newPrice);

        webTestClient
                //Given
                .put()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "XYZ123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)

                //When
                .exchange()

                //Then
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("To test the exception scenario")
    void runtimeException() {
        webTestClient
                .get()
                .uri(ItemConstants.ITEM_END_POINT_V1.concat("/runtimeException"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("RuntimeException Occurred");

    }
}