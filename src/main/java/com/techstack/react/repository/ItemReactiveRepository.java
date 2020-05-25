package com.techstack.react.repository;

import com.techstack.react.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
}
