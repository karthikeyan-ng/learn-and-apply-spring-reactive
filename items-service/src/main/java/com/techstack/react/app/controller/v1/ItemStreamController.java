package com.techstack.react.app.controller.v1;

import com.techstack.react.app.document.ItemCapped;
import com.techstack.react.app.repository.ItemReactiveCappedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.techstack.react.app.consts.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RequiredArgsConstructor
@RestController
public class ItemStreamController {

    private final ItemReactiveCappedRepository itemReactiveCappedRepository;

    /**
     * TIP: This endpoint produces continues data based on the data available in the database.
     * However, if you refresh your browser it would display only limited records and then proceed to
     * get each record from DB. Because we have configured the "maxDocuments(20)" in the ItemDataInitializer.
     * You can adjust this based on your requirement.
     */
    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream() {
        return itemReactiveCappedRepository.findItemsBy();
    }

}
