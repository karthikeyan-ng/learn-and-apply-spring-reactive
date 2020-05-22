package com.techstack.react.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

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

    /**
     * The whole process will be executed in main thread
     */
    @Test
    @DisplayName("Execute FlatMap in Sequential")
    void transformUsingFlatMap_Sequential() {
        Flux<String> alphabetsFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .flatMap(s -> Flux.fromIterable(convertToList(s))) //When to use flatMap? If you want to call a DB or external service that returns a Flux<T>
                .log();

        StepVerifier.create(alphabetsFlux)
                    .expectNextCount(12)
                    .verifyComplete();
    }

    /**
     * This will use the parallel-N thread to do your operation.
     * The final output is not garnette w.r.t input order
     * If you want to stick with your input order there are some other handy methods available.
     */
    @Test
    @DisplayName("Execute FlatMap in Parallel")
    void transformUsingFlatMap_Parallel() {
        Flux<String> alphabetsFlux =
                Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))

                        //Step1: Here window will wait to pass two elements. Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                        .window(2)

                        //When to use flatMap? If you want to call a DB or external service that returns a Flux<T>
                        .flatMap(s -> s.map(this::convertToList)
                                //Step2: subscribe elements in parallel()
                                .subscribeOn(parallel()))

                        //Step3: Again convert back to Flux<String> using FlatMap
                        .flatMap(s -> Flux.fromIterable(s))
                        .log();

        StepVerifier.create(alphabetsFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    /**
     * This will use the parallel-N thread to do your operation.
     * The final output is garnette w.r.t input order
     * If you want to stick with your input order there are some other handy methods available.
     * Let's explorer here
     * 1. concatMap() -> maintains order but you will feel like sequential
     * 2. flatMapSequential() -> must faster compare to concatMap()
     */
    @Test
    @DisplayName("Execute FlatMap in Parallel and Maintain Order")
    void transformUsingFlatMap_Parallel_And_Maintain_Order() {
        Flux<String> alphabetsFlux =
                Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))

                        .window(2)

                        //Option 1
                        //.concatMap(s -> s.map(this::convertToList).subscribeOn(parallel()))

                        //Option 2
                        .flatMapSequential(s -> s.map(this::convertToList).subscribeOn(parallel()))

                        .flatMap(s -> Flux.fromIterable(s))
                        .log();

        StepVerifier.create(alphabetsFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of(s, "newValue");
    }
}
