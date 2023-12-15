package com.untactstore.modules.waiting.event;

import com.untactstore.modules.waiting.Waiting;
import lombok.Getter;

@Getter
public class AcceptWaitingEvent {

    private Waiting waiting;

    public AcceptWaitingEvent(Waiting waiting) {
        this.waiting = waiting;
    }
}
