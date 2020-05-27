package com.techstack.reactive.controller;

import com.techstack.reactive.client.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
}
