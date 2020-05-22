package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoCombineTest {

    @Test
    void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        /**
         * In real use case, if you want to do two DB / External API calls and
         * combine them into another Flux and send it back to the caller
         */

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                    .expectSubscription()
                    .expectNext("A", "B", "C", "D", "E", "F")
                    .verifyComplete();
    }
}
