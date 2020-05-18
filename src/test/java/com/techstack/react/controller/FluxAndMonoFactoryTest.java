package com.techstack.react.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("Karthi", "Sara", "Pascal", "Thomas", "Christof");

    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("Test to use Flux.fromIterable")
    void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);

        StepVerifier.create(namesFlux)
                    .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                    .verifyComplete();
    }

    @Test
    @DisplayName("Test to use Flux.fromArray")
    void fluxUsingArray() {
        String names[] = new String[]{"Karthi", "Sara", "Pascal", "Thomas", "Christof"};

        Flux<String> namesFlux = Flux.fromArray(names);

        StepVerifier.create(namesFlux)
                .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                .verifyComplete();
    }

    @Test
    @DisplayName("Test to use Flux.fromStream")
    void fluxUsingStream() {

        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("Karthi", "Sara", "Pascal", "Thomas", "Christof")
                .verifyComplete();
    }

    @Test
    @DisplayName("Test to use Flux.range")
    void fluxUsingRange() {

        Flux<Integer> integerFlux = Flux.range(1, 5).log();

        StepVerifier.create(integerFlux)
                    .expectNext(1, 2, 3, 4, 5)
                    .verifyComplete();

    }

    @Test
    @DisplayName("Test to use Mono.justOrEmpty")
    void monoUsingJustOrEmpty() {

        Mono<String> mono = Mono.justOrEmpty(null); //==> You will get Mono.Empty()

        StepVerifier.create(mono.log())
                    .verifyComplete();

    }

    @Test
    @DisplayName("Test to use Mono.fromSupplier")
    void monoUsingSupplier() {

        Supplier<String> stringSupplier = () -> "Karthi";

        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono.log())
                    .expectNext("Karthi")
                    .verifyComplete();

    }
}
