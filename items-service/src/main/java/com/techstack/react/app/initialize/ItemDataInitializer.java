package com.techstack.react.app.initialize;

import com.techstack.react.app.document.Item;
import com.techstack.react.app.document.ItemCapped;
import com.techstack.react.app.repository.ItemReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Profile("!test")
@Slf4j
@RequiredArgsConstructor
@Component
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository itemReactiveRepository;
    private final MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {

        initialDataSetup();
        createCappedCollection();
    }

    private void createCappedCollection() {
        //Every time application starts, this would drop the ItemCapped collection
        //TIP: Don't use capped collection for permanent storage. Its used in temporary storage
        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions
                        .empty()
                        .maxDocuments(20) //How many max documents this can store at a given point
                        .size(50000)  //What is the size of the whole capped collection
                        .capped());
    }

    private void initialDataSetup() {
        itemReactiveRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> log.info("Item inserted from CommandLineRunner : {}", item));
    }

    private List<Item> data() {
        return List.of(
                new Item(null, "Apple Ipad", 350.0),
                new Item(null, "Samsung Tab", 450.0),
                new Item(null, "LG TV", 850.0)
                );
    }
}
