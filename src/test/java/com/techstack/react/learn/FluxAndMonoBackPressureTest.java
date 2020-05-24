package com.techstack.react.learn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
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

    /**
     * This testcase is an Actual logic w.r.t above Testcase backPressureTest()
     */
    @Test
    @DisplayName("How to do programmatic way of subscribe call")
    void backPressure() {

        Flux<Integer> integerFlux = Flux.range(1, 10) //<= This will produce a Flux of 10 items
                .log();

        integerFlux.subscribe(e -> System.out.println("Element is " + e),   //Actual element
                              e -> System.err.println("Exception is " + e), //Error handling
                              () -> System.out.println("Done"),             //Completion event
                              (subscription -> subscription.request(2)));//Actual subscription

    }

    /**
     * This testcase is an Actual logic w.r.t above Testcase
     */
    @Test
    @DisplayName("How to do programmatic way of subscribe call")
    void backPressure_Cancel() {

        Flux<Integer> integerFlux = Flux.range(1, 10) //<= This will produce a Flux of 10 items
                .log();

        integerFlux.subscribe(e -> System.out.println("Element is " + e),   //Actual element
                              e -> System.err.println("Exception is " + e), //Error handling
                              () -> System.out.println("Done"),             //Completion event
                              subscription -> subscription.cancel());       //Actual subscription

    }

    @Test
    void customized_BackPressure() {
        Flux<Integer> integerFlux = Flux.range(1, 10) //<= This will produce a Flux of 10 items
                                        .log();

        integerFlux.subscribe(new BaseSubscriber<>() {

            /**
             * If you want to do data level validation you can use this method
             * like: if value is 4, I have to cancel the subscription
             * @param value
             */
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is : " + value);
                if(value == 4) {
                    cancel();
                }
            }
        });
    }


}
