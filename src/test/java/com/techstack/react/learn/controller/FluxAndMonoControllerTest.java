package com.techstack.react.learn.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    @DisplayName("/flux1 endpoint test approach2: verify th size")
    void flux1_endpoint_test_approach2() {

        webTestClient
            .get()
            .uri("/flux1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(Integer.class)
            .hasSize(4); //<== verify that response body list contains 4 elements

    }

    @Test
    @DisplayName("/flux1 endpoint test approach3: using returnResult")
    void flux1_endpoint_test_approach3() {

       List<Integer> expectedIntegerList = List.of(1, 2, 3, 4);

       EntityExchangeResult<List<Integer>> entityExchangeResult  = webTestClient
                .get()
                .uri("/flux1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

       assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
    }

    @Test
    @DisplayName("/flux1 endpoint test approach4: using consumeWith()")
    void flux1_endpoint_test_approach4() {

        List<Integer> expectedIntegerList = List.of(1, 2, 3, 4);

        webTestClient
                .get()
                .uri("/flux1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response ->
                    assertEquals(expectedIntegerList, response.getResponseBody()));
    }

    @Test
    @DisplayName("/flux3 endpoint to test infinite Flux values using Stream")
    void flux3_endpoint_infinite_stream() {
        Flux<Long> longStreamFlux = webTestClient
                .get()
                .uri("/flux3")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier
                .create(longStreamFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L) //<= Infinite stream emits values continuously. Hence, limits to some value and apply cancel() event
                .thenCancel()
                .verify();

    }

    @Test
    @DisplayName("/mono1 endpoint to test integer Mono")
    void mono1_endpoint() {

        Integer expectedValue = new Integer(1);

        webTestClient
                .get()
                .uri("/mono1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> assertEquals(expectedValue, response.getResponseBody()));
    }
}