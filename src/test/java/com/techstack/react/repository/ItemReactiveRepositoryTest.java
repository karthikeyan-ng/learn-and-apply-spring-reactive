package com.techstack.react.repository;

import com.techstack.react.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ItemReactiveRepositoryTest {

    @Autowired ItemReactiveRepository itemReactiveRepository;

    List<Item> items = List.of(
            new Item(null, "Apple Ipad", 350.0),
            new Item(null, "Samsung Tab", 450.0),
            new Item(null, "LG TV", 850.0),
            new Item("ABC123", "MacBook Pro 16", 2500.0)
            );

    @BeforeEach
    public void setup() {
        itemReactiveRepository

                /**
                 * This below line will delete all the document from the collection.
                 * Hence, each test case will start with fresh document entries from
                 * {@code items}
                 */
                .deleteAll()

                /**
                 * This below line prepares a Iterable Flux for the given {@code items}
                 */
                .thenMany(Flux.fromIterable(items))

                /**
                 * This below line saves each Item document to the MongoDB Collection
                 */
                .flatMap(itemReactiveRepository::save)

                /**
                 * After inserted the document, printing the saved document
                 */
                .doOnNext(item -> System.out.println("Inserted Item is " + item))

                /**
                 * This below line will wait util all the items are save
                 * to Mongo DB inorder to execute the testcase.
                 * This is not recommended in actual production code.
                 * Only applicable for executing test cases.
                 */
                .blockLast()
        ;

    }

    @Test
    @DisplayName("Test to verify total Items count = 4")
    void verifyTotalNumberOfItemsCount() {
        StepVerifier
            .create(itemReactiveRepository.findAll())
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete()
        ;
    }

    @Test
    @DisplayName("Get Item by Id")
    void getItemsById() {
        StepVerifier
                .create(itemReactiveRepository.findById("ABC123"))
                .expectSubscription()

                /**
                 * expectNextMatches is a kind of verify operation (in JUnit) to
                 * check the "expected" and "actual" value.
                 * Here:
                 * expected: "MacBook Pro 16"
                 * actual: item.getDescription() value
                 */
                .expectNextMatches(item -> item.getDescription().equals("MacBook Pro 16"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Find Item(s) by given Description")
    void findItemByDescription() {
        StepVerifier
                .create(itemReactiveRepository
                        .findByDescription("Samsung Tab") //it's a exact match!
                        .log("findItemByDescription"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Save an Item")
    void saveItem() {
        Item item = new Item(null, "Motorola G8 Plus", 600.0);
        Mono<Item> savedItemMono = itemReactiveRepository.save(item);

        StepVerifier
                .create(savedItemMono.log("saved Item"))
                .expectSubscription()
                .expectNextMatches(item1 ->
                        Objects.nonNull(item1.getId()) &&
                        item1.getDescription().equals("Motorola G8 Plus"))
                .verifyComplete();
    }
}