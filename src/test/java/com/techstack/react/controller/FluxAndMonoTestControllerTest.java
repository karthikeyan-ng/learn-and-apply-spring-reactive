package com.techstack.react.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test Mono and Flux
 */
class FluxAndMonoTestControllerTest {

    @Test
    @DisplayName("This is Simple Flex to subscribe series of String")
    void fluxTest() {

        //Step1: Create
        Flux<String> stringFlux = Flux.just("Spring Boot ", "Spring Data ", "Spring Message ");

        //Step2: Consume
        stringFlux.subscribe(System.out::println);
    }

    @Test
    @DisplayName("This is Simple Flex to subscribe series of String with Exception by applying log")
    void fluxTest1() {

        Flux<String> stringFlux =
                //Step1: Create
                Flux.just("Spring Boot ", "Spring Data ", "Spring Message ")
                    //Step2: Attach an Exception
                    .concatWith(Flux.error(new RuntimeException("Exception while processing")))
                    //Step6: Add log method to get detailed processing steps
                    .log();

        //Step3: Consume
        stringFlux.subscribe(
                System.out::println, //Step4: Data part
                (e) -> System.err.println("Exception is " + e)  //Step5: Error part
        );
    }

    @Test
    @DisplayName("This is Simple Flex Test to subscribe series of String with Exception by applying log" +
            " adding another concatWith to check after the onError event emitted")
    void fluxTest2() {

        Flux<String> stringFlux =
                //Step1: Create
                Flux.just("Spring Boot ", "Spring Data ", "Spring Message ")

                        //Step2: Attach an Exception
                        .concatWith(Flux.error(new RuntimeException("Exception while processing")))

                        //Step7: To check whether Flux returns any data after exception?
                        .concatWith(Flux.just("After Error"))

                        //Step6: Add log method to get detailed processing steps
                        .log();

        //Step3: Consume
        stringFlux.subscribe(
                System.out::println, //Step4: Data part

                (e) -> System.err.println("Exception is " + e), //Step5: Error part

                //Step8: This Block of information will be executed for onComplete() status.
                //This will not print if any error occurs
                () -> System.out.println("Completed!")
        );
    }

    @Test
    @DisplayName("This is a Flux test to verify the testcase")
    void fluxTestElements_WithoutError() {

        Flux<String> stringFlux = Flux.just("Spring Boot", "Spring Data", "Spring Message")
                .log();

        //Success: Inorder to verify the Flux elements, you can use StepVerifier
//        StepVerifier.create(stringFlux)
//                    .expectNext("Spring Boot")
//                    .expectNext("Spring Data")
//                    .expectNext("Spring Message")
//                    .verifyComplete();

        //Failure: Change the order in the "expectNext"
//        StepVerifier.create(stringFlux)
//                .expectNext("Spring Data")
//                .expectNext("Spring Boot")
//                .expectNext("Spring Message")
//                .verifyComplete();

        //Failure: What would happen if you not called "verifyComplete()"
        StepVerifier.create(stringFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Data")
                .expectNext("Spring Message");

        //If you not call this, element won't be subscribed!
        //Because, this would act as a subscribe() in Flux
//                .verifyComplete();
    }

    @Test
    @DisplayName("This is a Flux test to verify the testcase with Error")
    void fluxTestElements_WithError() {
        Flux<String> stringFlux = Flux.just("Spring Boot", "Spring Data", "Spring Message")
                .concatWith(Flux.error(new RuntimeException("Exception while processing")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring Boot")
                .expectNext("Spring Data")
                .expectNext("Spring Message")

                //This would just verify the Exception class
//                .expectError(RuntimeException.class)

                //If you want to verify the Exception message?
                .expectErrorMessage("Exception while processing" )

//                .verifyComplete(); //<==This is not applicable. Don't use it with Error. Instead use verify()
                .verify()
        ;
    }
}