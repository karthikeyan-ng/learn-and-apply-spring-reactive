package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("Karthi", "Sara", "Pascal", "Thomas", "Christof");

    @Test
    void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                                     .map(String::toUpperCase)
                                     .log();

        StepVerifier.create(namesFlux)
                    .expectNext("KARTHI", "SARA", "PASCAL", "THOMAS", "CHRISTOF")
                    .verifyComplete();
    }

    @Test
    void transformUsingMap_Length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                                      .map(String::length)
                                      .log();

        StepVerifier.create(namesFlux)
                .expectNext(6, 4, 6, 6, 8)
                .verifyComplete();
    }

    @Test
    void transformUsingMap_Length_repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                                      .map(String::length)
                                      .repeat(1)
                                      .log();

        StepVerifier.create(namesFlux)
                .expectNext(6, 4, 6, 6, 8)
                //Since we added "repeat(1), we have to repeat StepVerifier.Step one more time.
                .expectNext(6, 4, 6, 6, 8)
                .verifyComplete();
    }

    @Test
    void transformUsingMap_Filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                                     .filter(s -> s.length() > 5)
                                     .map(String::toUpperCase)
                                     .log();

        StepVerifier.create(namesFlux)
                    .expectNext("KARTHI", "PASCAL", "THOMAS", "CHRISTOF")
                    .verifyComplete();
    }
}
