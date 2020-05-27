package com.techstack.react.learn.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Why we didn't use {@link @WebFluxTest} annotation?
 * Because, this annotation will only deduct @RestController and @Controller and other few annotations.
 * But it will not deduct @Component, @Service and @Repository.
 * Hence we used @SpringBootTest here.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
class SampleHandlerFunctionTest {

    /**
     * If you don't use {@link AutoConfigureWebTestClient} you will get below error.
     *
     * Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException:
     * No qualifying bean of type 'org.springframework.test.web.reactive.server.WebTestClient'
     * available: expected at least 1 bean which qualifies as autowire candidate.
     * Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
     */
    @Autowired WebTestClient webTestClient;

    @Test
    @DisplayName("/functional/flux endpoint test")
    void routerFunctionFluxTest() {

        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri("/functional/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    @DisplayName("/functional/mono endpoint to test")
    void mono1_endpoint() {

        Integer expectedValue = new Integer(1);

        webTestClient
                .get()
                .uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> assertEquals(expectedValue, response.getResponseBody()));
    }
}