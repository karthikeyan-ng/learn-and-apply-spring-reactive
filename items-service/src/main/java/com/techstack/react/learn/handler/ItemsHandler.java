package com.techstack.react.learn.handler;

import com.techstack.react.app.document.Item;
import com.techstack.react.app.document.ItemCapped;
import com.techstack.react.app.repository.ItemReactiveCappedRepository;
import com.techstack.react.app.repository.ItemReactiveRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
@AllArgsConstructor
public class ItemsHandler {

    private final ItemReactiveRepository itemReactiveRepository;
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item)))
                        .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {

        Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);
        return itemToBeInserted.flatMap(item ->
                    ServerResponse
                        .created(null)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {

                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                        .flatMap(currentItem -> {
                            currentItem.setDescription(item.getDescription());
                            currentItem.setPrice(item.getPrice());
                            return itemReactiveRepository.save(currentItem);
                        });
                    return itemMono;
                });

        return updatedItem.flatMap(item -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(item))
                    .switchIfEmpty(notFound));
    }

    /**
     * This method will throw a RuntimeException.
     * Who is responsible for preparing the default response json content?
     * - DefaultErrorWebExceptionHandler.java
     * - AbstractErrorWebExceptionHandler.java
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
        throw new RuntimeException("Runtime Error Occurred");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
    }
}
