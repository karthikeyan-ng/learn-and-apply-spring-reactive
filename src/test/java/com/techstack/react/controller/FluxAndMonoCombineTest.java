package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

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

    @Test
    void combineUsingMerge_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        /**
         * In real use case, if you want to do two DB / External API calls and
         * combine them into another Flux and send it back to the caller
         * you can use Flux.merge()
         *
         * Important to note:
         * using merge() method, flux2 will start emitting values parallel with flux1
         * and order will NOT be maintained
         */

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        /**
         * In real use case, if you want to do two DB / External API calls and
         * combine them into another Flux and send it back to the caller
         * you can use Flux.concat()
         */

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void combineUsingConcat_withDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        /**
         * In real use case, if you want to do two DB / External API calls and
         * combine them into another Flux and send it back to the caller
         * you can use Flux.concat()
         *
         * Important to note:
         * using concat() method, flux2 will not start emitting values until flux1 is completed
         * and order will be maintained
         */

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
}
