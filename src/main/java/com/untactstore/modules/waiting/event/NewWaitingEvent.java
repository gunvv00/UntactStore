package com.untactstore.modules.waiting.event;

import com.untactstore.modules.waiting.Waiting;
import lombok.Getter;

@Getter
public class NewWaitingEvent {

    private Waiting waiting;

    public NewWaitingEvent(Waiting waiting) {
        this.waiting = waiting;
    }
}
