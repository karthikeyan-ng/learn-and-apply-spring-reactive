package com.techstack.react.learn.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/mono")
    public Mono<String> getSubject() {
        return Mono.just("Spring Boot ")
                   .map(s -> s.concat("Reactive"));

    }

    @GetMapping("/mono1")
    public Mono<Integer> returnMono() {
        return Mono.just(1).log();
    }

    /**
     * For Flux of String type, browser is keep on populating data
     * for the given request from producer side.
     *
     * @return
     */
    @GetMapping("/flux")
    public Flux<String> getSubjects() {
        return Flux.just("Spring Boot ", "Spring Data ", "Spring Message ")
                   .map(s -> s.concat("Reactive <br>"))
                   .delayElements(Duration.ofSeconds(1))
                   .log();
    }

    /**
     * For Flux of Integer type, when you execute this request, you may not see
     * any difference between traditional blocking and not-blocking
     * reactive response. Because, Browser understood that this request produces
     * JSON response. Hence, until all the JSON elements loads then it would
     * display it.
     *
     * How to avoid this? How to get elements in Reactive nature?
     *
     * @return
     */
    @GetMapping("/flux1")
    public Flux<Integer> returnIntegerFlux() {
        return Flux
                .just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * Inorder to overcome the previous problem, we have to use the
     * {@code MediaType.APPLICATION_STREAM_JSON_VALUE}
     *
     * @return
     */
    @GetMapping(value = "/flux2", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnIntegerFluxStream() {
        return Flux
                .just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    /**
     * This endpoint emits infinite Flux of values with the duration of 1 sec
     * {@code MediaType.APPLICATION_STREAM_JSON_VALUE}
     *
     * Inorder to test this endpoint, open two browser tabs and hit the endpoint.
     * Each tab will start consume the values from this Flux from the starting (1...N).
     * This is called "Cold Publisher".
     *
     * If you stop the SpringBoot application, you will see the log contains two cancel()
     * event. Why? Because, publisher will pass the cancel signal to the subscriber that
     * no more data is coming from the publisher side.
     *
     * @return
     */
    @GetMapping(value = "/flux3", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnInfiniteFluxStream() {
        return Flux.interval(Duration.ofSeconds(1)).log();
    }
}
