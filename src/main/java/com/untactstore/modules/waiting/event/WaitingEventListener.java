package com.untactstore.modules.waiting.event;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.notification.Notification;
import com.untactstore.modules.notification.NotificationRepository;
import com.untactstore.modules.notification.NotificationType;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.waiting.Waiting;
import com.untactstore.modules.waiting.WaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class WaitingEventListener {
    private final WaitingRepository waitingRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final StoreRepository storeRepository;

    @EventListener
    public void handleNewWaitingEvent(NewWaitingEvent newWaitingEvent) {
        Waiting waiting = waitingRepository.findById(newWaitingEvent.getWaiting().getId()).orElseThrow();
        Account account = accountRepository.findById(waiting.getAccount().getId()).orElseThrow();
        Store store = storeRepository.findById(waiting.getStore().getId()).orElseThrow();

        Notification notification = new Notification();
        notification.setTitle(store.getName());
        notification.setLink("/my-waiting");
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(store.getName()+"의 대기번호를 발급 받았습니다.");
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.WAITING_CREATED);
        notificationRepository.save(notification);
    }

    @EventListener
    public void handleAcceptWaitingEvent(AcceptWaitingEvent acceptWaitingEvent) {
        Waiting waiting = waitingRepository.findById(acceptWaitingEvent.getWaiting().getId()).orElseThrow();
        Account account = accountRepository.findById(waiting.getAccount().getId()).orElseThrow();
        Store store = storeRepository.findById(waiting.getStore().getId()).orElseThrow();

        Notification notification = new Notification();
        notification.setTitle(store.getName());
        notification.setLink("/my-waiting");
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(store.getName()+"의 대기가 끝났습니다. 지금 바로 입장해 주세요.");
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.WAITING_ACCEPT);
        notificationRepository.save(notification);
    }
}
