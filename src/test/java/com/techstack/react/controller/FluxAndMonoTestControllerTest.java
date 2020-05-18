package com.techstack.react.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test Mono and Flux
 */
class FluxAndMonoTestControllerTest {

    @Test
    @DisplayName("This is Simple Flex to subscribe series of String")
    void fluxTest() {

        //Step1: Create
        Flux<String> stringFlux = Flux.just("Spring Boot ", "Spring Data ", "Spring Message ");

        //Step2: Consume
        stringFlux.subscribe(System.out::println);
    }

}