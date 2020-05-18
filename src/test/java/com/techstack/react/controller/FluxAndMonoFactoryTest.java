package com.techstack.react.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("Karthi", "Sara", "Pascal", "Thomas", "Christof");

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("Test to use Flux.fromIterable")
    void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);

        StepVerifier.create(namesFlux)
                    .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                    .verifyComplete();
    }

    @Test
    @DisplayName("Test to use Flux.fromArray")
    void fluxUsingArray() {
        String names[] = new String[]{"Karthi", "Sara", "Pascal", "Thomas", "Christof"};

        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                .verifyComplete();
    }

    @Test
    @DisplayName("Test to use Flux.fromStream")
    void fluxUsingStream() {

        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                .verifyComplete();
    }

}
