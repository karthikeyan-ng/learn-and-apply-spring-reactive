package com.techstack.react.learn;

import com.techstack.react.learn.controller.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))

                //How to handle an Error in a Reactive stream
                //This block will exeucte any Error
                .onErrorResume(e -> {
                    System.out.println("Exception is : " + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")

                //Scenario 1: Error Scenario
//                .expectError(RuntimeException.class)
//                .verify();

                //Scenario 2: Handled Error Scenario with default response
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    void fluxErrorHandling_OnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default"); //<= Fallback!


        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    @DisplayName("Catch the RuntimeException and Convert it to application Custom Exception")
    void fluxErrorHandling_OnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e));


        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    @DisplayName("Catch the RuntimeException and Convert it to application Custom Exception With Retry")
    void fluxErrorHandling_OnErrorMap_WithRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))

                .retry(2); //<= retry two times


        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")

                //--configured retry(2) hence, it expects the same sequence two times.
                //Comment below two lines to check the error message
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                //--

                .expectError(CustomException.class)
                .verify();
    }

    @Test
    @DisplayName("Catch the RuntimeException and Convert it to application Custom Exception With Retry and BackOff")
    void fluxErrorHandling_OnErrorMap_WithRetryBackOff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))

                .retryBackoff(2, Duration.ofSeconds(5)); //<= retry two times


        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")

                //--configured retry(2) hence, it expects the same sequence two times.
                //Comment below two lines to check the error message
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                //--

                //if you are using retryBackoff() with error, it would display reactor.core.Exceptions$RetryExhaustedException
                .expectError(IllegalStateException.class)
                .verify();
    }
}
