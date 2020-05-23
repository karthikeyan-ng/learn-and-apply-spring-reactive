package com.techstack.react.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    /**
     * What is Cold Publisher?
     * From the given Flux, you can subscribe multiple times.
     * Each time when you subscribe, Flux will start emitting value from the beginning.
     *
     * @throws InterruptedException
     */
    @Test
    void coldPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        // Emits the value from beginning
        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(2000);

        // Emits the value from beginning
        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        Thread.sleep(4000);

    }
}
