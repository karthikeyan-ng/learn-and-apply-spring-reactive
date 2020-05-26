package com.techstack.react.app.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemConstants {

    public static final String ITEM_END_POINT_V1 = "/v1/items";
    public static final String ITEM_FUNCTIONAL_END_POINT_V1 = "/v1/fun/items";
}
