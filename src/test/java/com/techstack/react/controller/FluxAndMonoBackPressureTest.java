package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    /**
     * Here in this testcase create a Flex range(1,10)
     * Using StepVerifier we control the data flow 1 at a time using thenRequest(1).
     * After consumed two elements, cancel() event called and
     * verify() event checks the data validation
     */
    @Test
    void backPressureTest() {

        Flux<Integer> integerFlux = Flux.range(1, 10) //<= This will produce a Flux of 10 items
                                        .log();

        StepVerifier.create(integerFlux)
                    .expectSubscription()
                    .thenRequest(1)
                    .expectNext(1)
                    .thenRequest(1)
                    .expectNext(2)
                    .thenCancel()
                    .verify();
    }
}
