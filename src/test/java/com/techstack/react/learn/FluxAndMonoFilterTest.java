package com.techstack.react.learn;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("Karthi", "Sara", "Pascal", "Thomas", "Christof");

    @Test
    void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                 .filter(s -> s.charAt(1) == 'a')
                 .log();  // Karthi, Sara, Pascal

        StepVerifier.create(namesFlux)
                    .expectNext("Karthi", "Sara", "Pascal")
                    .verifyComplete();
    }

    @Test
    void filterLengthTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .log();  // Karthi, Pascal, Thomas, Christof

        StepVerifier.create(namesFlux)
                .expectNext("Karthi",  "Pascal", "Thomas", "Christof")
                .verifyComplete();
    }

}
