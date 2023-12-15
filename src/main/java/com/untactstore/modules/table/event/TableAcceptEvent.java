package com.untactstore.modules.table.event;

import com.untactstore.modules.table.Event;
import lombok.Getter;

@Getter
public class TableAcceptEvent {

    private Event event;

    public TableAcceptEvent(Event event) {
        this.event = event;
    }
}
