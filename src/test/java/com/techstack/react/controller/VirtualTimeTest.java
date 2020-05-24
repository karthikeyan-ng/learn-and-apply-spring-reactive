package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class VirtualTimeTest {

    /**
     * The below Flux test trying to explain that to execute this test,
     * it will take 3 seconds to complete. Because we have configured
     * Duration.ofSeconds as 1. Each element will be processed one per second.
     *
     * Think about realtime scenario where you would want to simulate
     * N number of values of the Flux and it would take N seconds to complete.
     * Obviously it will impact your build process.
     *
     * How to solve this problem?
     * Junit Virtualization concept. Refer next Testcase.
     */
    @Test
    void testWithoutVirtualTime() {

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }
}
