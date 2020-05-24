package com.techstack.react.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest //<== Step2
@ExtendWith(SpringExtension.class)  //<== Step1
class FluxAndMonoControllerTest {

    //Step3
    //The @WebFluxTest is responsible for creating instance
    //for this WebTestClient
    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName("/flux1 endpoint test approach1")
    void flux1_endpoint_test_approach1() {

        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri("/flux1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

}