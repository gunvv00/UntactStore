package com.untactstore.modules.store.event;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.AccountPredicates;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.notification.Notification;
import com.untactstore.modules.notification.NotificationRepository;
import com.untactstore.modules.notification.NotificationType;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StoreEventListener {

    private final StoreRepository storeRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStoreCreatedEvent(StoreCreateEvent storeCreateEvent) {
        Store store = storeRepository.findStoreWithKeywordById(storeCreateEvent.getStore().getId());

        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByKeywords(store.getKeywords()));
        accounts.forEach(account -> {
            if (account.isStoreCreatedByWeb()) {
                Notification notification = new Notification();
                notification.setTitle(store.getName());
                notification.setLink("/store/"+ URLEncoder.encode(store.getPath(), StandardCharsets.UTF_8));
                notification.setChecked(false);
                notification.setCreatedLocalDateTime(LocalDateTime.now());
                notification.setMessage(store.getShortDescription());
                notification.setAccount(account);
                notification.setNotificationType(NotificationType.STORE_CREATED);
                notificationRepository.save(notification);
            }
        });
    }
}
