package com.techstack.reactive.controller;

import com.techstack.reactive.client.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    /**
     * Here retrieve() method will give access to the direct response content
     *
     * @return
     */
    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {

        return webClient
                .get()
                .uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Call using Retrieve");
    }

    /**
     * Here exchange() method will give access to the RAW ClientResponse content
     * @return
     */
    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {

        return webClient
                .get()
                .uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in Client Call using Exchange");
    }

    @GetMapping("/client/retrieve/singleItem")
    public Mono<Item> getOneItemsUsingRetrieve() {

        String id = "5ece6e9834cd6d162e32d147";

        return webClient
                .get()
                .uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Items in Client Call using Retrieve Single Item");
    }

    @GetMapping("/client/exchange/singleItem")
    public Mono<Item> getOneItemsUsingExchange() {

        String id = "5ece6e9834cd6d162e32d147";

        return webClient
                .get()
                .uri("/v1/items/{id}", id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Items in Client Call using Retrieve Single Item");
    }

    @PostMapping("/client/createItem")
    public Mono<Item> createItem(@RequestBody Item item) {

        Mono<Item> itemMono = Mono.just(item);

        return webClient
                .post()
                .uri("/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemMono, Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Created Item is : ");
    }

    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateItem(@PathVariable @NonNull final String id,
                                 @RequestBody @NonNull final Item item) {

        return webClient
                .put()
                .uri("/v1/items/{id}", id)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Update Item is : ");
    }

    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteItem(@PathVariable @NonNull final String id) {
        return webClient
                .delete()
                .uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Deleted Item");
    }

    /**
     * Error Handling scenario using retrieve() method
     */
    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve() {
        return webClient
                .get()
                .uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {

                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap(errorMessage -> {
                        log.error("The error Message is : " + errorMessage);
                        throw new RuntimeException(errorMessage);
                    });
                })
                .bodyToFlux(Item.class);

    }

    /**
     * Error Handling scenario using exchange() method
     */
    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange() {
        return webClient
                .get()
                .uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany(clientResponse -> {

                   if(clientResponse.statusCode().is5xxServerError()) {
                        return  clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("Error Message : {} ", errorMessage);
                                    throw new RuntimeException(errorMessage);
                                });
                    } else {
                       return clientResponse.bodyToFlux(Item.class);
                   }
                });
    }
}
