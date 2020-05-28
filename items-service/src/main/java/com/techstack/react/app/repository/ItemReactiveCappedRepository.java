package com.techstack.react.app.repository;

import com.techstack.react.app.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

    /**
     * How we create a Tailable Cursor?
     * Tailable Cursor is a one which is going to be used in order to facilidate
     * the stream kind of output to the endpoint
     *
     */
    @Tailable
    Flux<ItemCapped> findItemsBy();

}
