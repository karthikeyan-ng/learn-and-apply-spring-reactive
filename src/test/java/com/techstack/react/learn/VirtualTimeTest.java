package com.techstack.react.learn;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

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

    @Test
    void testWithVirtualTime() {

        //Step1: Enable Virtual Time
        VirtualTimeScheduler.getOrSet();

        //Step2: Create a Flux
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        //Step3: use StepVerifier.withVirtualTime()
        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();


    }

    @Test
    void combineUsingConcat_withDelay() {

        //Step1: Enable Virtual Time
        VirtualTimeScheduler.getOrSet();

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
         *
         * if you remove step 1,2 and 3 it will take 6 seconds to complete your testcase.
         */

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        //Step2: withVirtualTime
        StepVerifier.withVirtualTime(() -> mergedFlux.log())
                .expectSubscription()
                //Step3: enable thenAwaite
                .thenAwait(Duration.ofSeconds(6))
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
}
