package com.untactstore.modules.table.event;

import com.untactstore.modules.table.Event;
import lombok.Getter;

@Getter
public class TableRequestEvent {

    private Event event;

    public TableRequestEvent(Event event) {
        this.event = event;
    }
}
