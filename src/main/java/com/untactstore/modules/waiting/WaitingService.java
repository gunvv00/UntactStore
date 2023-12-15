package com.untactstore.modules.waiting;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.waiting.event.AcceptWaitingEvent;
import com.untactstore.modules.waiting.event.NewWaitingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class WaitingService {
    private final WaitingRepository waitingRepository;
    private final StoreRepository storeRepository;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**/
    public void newWaiting(Account account, Store store, String personnel) {
        Waiting waiting = new Waiting();
        waiting.setAccount(account);
        waiting.setPersonnel(Integer.valueOf(personnel));
        waiting.setTurn(store.countWaitingList()+1);
        waiting.setWaitingAt(LocalDateTime.now());
        waiting.setAvailable(false);
        waiting.setAttended(false);
        store.addWaiting(waiting);
        waitingRepository.save(waiting);
        eventPublisher.publishEvent(new NewWaitingEvent(waiting));
    }

    public void exitWaiting(Store store, Waiting waiting) {
        store.getWaitingList().stream().forEach(w-> {
            if (w.getTurn() > waiting.getTurn()) {
                w.setTurn(w.getTurn()-1);
            }
        });
        store.removeWaiting(waiting);
        waitingRepository.delete(waiting);
    }

    public void acceptWaiting(Waiting waiting) {
        if (!waiting.isAvailable() && !waiting.isAttended()) {
            waiting.setAvailable(true);
            eventPublisher.publishEvent(new AcceptWaitingEvent(waiting));
        }
    }

    public void rejectWaiting(Waiting waiting) {
        if (waiting.isAvailable() && !waiting.isAttended()) {
            waiting.setAvailable(false);
        }
    }

    public void checkIn(Store store, Waiting waiting) {
        if (waiting.isAvailable() && !waiting.isAttended()) {
            waiting.setAttended(true);
            store.shiftTurn(waiting);
        }
    }

    public void cancelCheck(Store store, Waiting waiting) {
        if (waiting.isAvailable() && waiting.isAttended()) {
            waiting.setAttended(false);
            waiting.setTurn(store.countWaitingList()+1);
        }
    }
}
