package com.techstack.react.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FluxAndMonoTestController {

    @GetMapping("/mono")
    public Mono<String> getSubject() {
        return Mono.just("Spring Boot ")
                   .map(s -> s.concat("Reactive"));

    }

    @GetMapping("/flux")
    public Flux<String> getSubjects() {
        return Flux.just("Spring Boot ", "Spring Data ", "Spring Message ")
                   .map(s -> s.concat("Reactive"));
    }


}
