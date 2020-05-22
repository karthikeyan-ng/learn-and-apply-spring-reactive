package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    void infiniteSequence() throws InterruptedException {

        /**
         * The below code will start emitting values from 0 to N (infinite) long values.
         * Each value will be emitting each 100 milli seconds
         */
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log();

        infiniteFlux.subscribe(e -> System.out.println("Value is " + e));

        //This is very important!!!
        //If you comment this line, nothing will be printed on console output.
        //Values will be start emitting till 3000 milli seconds
        Thread.sleep(3000);
    }

    @Test
    void finiteSequenceTest() {

        /**
         * The below code will start emitting values from 0 to N using take(N) long values.
         * Each value will be emitting each 100 milli seconds
         */
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                                    .take(3)
                                    .log();

        StepVerifier.create(finiteFlux)
                    .expectSubscription()
                    .expectNext(0L, 1L, 2L)
                    .verifyComplete();
    }

    @Test
    void finiteSequenceMapTest() {

        /**
         * The below code will start emitting values from 0 to N using take(N) long values.
         * Each value will be emitting each 100 milli seconds
         */
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .map(l -> l.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

    /**
     * The below Flux will start emitting each value in the interval of 100 milli seconds.
     * However, if you want to further delay on each element you can use delayElements()
     */
    @Test
    void finiteSequenceMap_WithDelay() {

        /**
         * The below code will start emitting values from 0 to N using take(N) long values.
         * Each value will be emitting each 100 milli seconds
         */
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                                       .delayElements(Duration.ofSeconds(1))
                                       .map(l -> l.intValue())
                                       .take(3)
                                       .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
