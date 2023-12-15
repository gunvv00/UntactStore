package com.untactstore.modules.store.event;

import com.untactstore.modules.store.Store;
import lombok.Getter;

@Getter
public class StoreCreateEvent {
    private Store store;

    public StoreCreateEvent(Store store) {
        this.store = store;
    }

}
