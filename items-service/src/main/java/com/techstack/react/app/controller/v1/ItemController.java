package com.techstack.react.app.controller.v1;

import com.techstack.react.app.document.Item;
import com.techstack.react.app.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.techstack.react.app.consts.ItemConstants.ITEM_END_POINT_V1;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ItemController {

    private final ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable final String id) {
        return itemReactiveRepository
                .findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody final Item item) {
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<Void> deleteItem(@PathVariable final String id) {
        return itemReactiveRepository.deleteById(id);
    }

    /**
     * 1. Receives "id" (PathVariable) and "item" (RequestBody) to be updated in the request
     * 2. Using the "id" get the Item from database
     * 3. Updated the item retrieved with the value from the request body
     * 4. Save the Item
     * 5. Return the saved Item
     */
    @PutMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable final String id,
            @RequestBody final Item item) {
        return itemReactiveRepository
                .findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

    /**
     * The Expected response for this call is
     * {
     *  "timestamp":"2020-05-27T14:42:10.495+00:00",
     *  "path":"/v1/items/runtimeException",
     *  "status":500,
     *  "error":"Internal Server Error",
     *  "message":"",
     *  "requestId":"f834bd99-4"
     * }
     *
     * Approach to override the default behaviour:
     * However, if I don't want to get all the above details, How to suppress or
     * create your own custom Exception response for your REST endpoint?
     * Using @ExceptionHandler method {@code ItemController#handleRuntimeException}
     *
     */
    @GetMapping(ITEM_END_POINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository
                .findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred")));

    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
//        log.error("Exception caught in handleRuntimeException: {}", ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
//    }
}
