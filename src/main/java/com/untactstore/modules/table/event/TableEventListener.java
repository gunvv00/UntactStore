package com.untactstore.modules.table.event;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.notification.Notification;
import com.untactstore.modules.notification.NotificationRepository;
import com.untactstore.modules.notification.NotificationType;
import com.untactstore.modules.store.Store;
import com.untactstore.modules.store.repository.StoreRepository;
import com.untactstore.modules.table.Event;
import com.untactstore.modules.table.EventRepository;
import com.untactstore.modules.table.Tables;
import com.untactstore.modules.table.TablesRepository;
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
public class TableEventListener {
    private final TablesRepository tablesRepository;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final StoreRepository storeRepository;

    @EventListener
    public void handleTableRequestEvent(TableRequestEvent tableRequestEvent) {
        Event event = eventRepository.findById(tableRequestEvent.getEvent().getId()).orElseThrow();
        Account account = accountRepository.findById(event.getAccount().getId()).orElseThrow();
        Tables tables = tablesRepository.findById(event.getTables().getId()).orElseThrow();
        Store store = storeRepository.findById(tables.getStore().getId()).orElseThrow();

        //손님
        Notification notification1 = new Notification();
        notification1.setTitle(store.getName());
        notification1.setLink("/store/"+ URLEncoder.encode(store.getPath(), StandardCharsets.UTF_8) + "/tables/"+URLEncoder.encode(tables.getTablesPath(), StandardCharsets.UTF_8));
        notification1.setChecked(false);
        notification1.setCreatedLocalDateTime(LocalDateTime.now());
        notification1.setMessage(store.getName()+"의 "+tables.getTablesPath()+"번 테이블을 요청했습니다.");
        notification1.setAccount(account);
        notification1.setNotificationType(NotificationType.TABLE_REQUEST);
        notificationRepository.save(notification1);

        //사장
        Notification notification2 = new Notification();
        notification2.setTitle(store.getName());
        notification2.setLink("/store/"+ URLEncoder.encode(store.getPath(), StandardCharsets.UTF_8) + "/tables/"+URLEncoder.encode(tables.getTablesPath(), StandardCharsets.UTF_8));
        notification2.setChecked(false);
        notification2.setCreatedLocalDateTime(LocalDateTime.now());
        notification2.setMessage(account.getName()+"님이 "+tables.getTablesPath()+"번 테이블을 요청했습니다.");
        notification2.setAccount(store.getOwner());
        notification2.setNotificationType(NotificationType.TABLE_REQUEST);
        notificationRepository.save(notification2);
    }

    @EventListener
    public void handleTableAcceptEvent(TableAcceptEvent tableAcceptEvent) {
        Event event = eventRepository.findById(tableAcceptEvent.getEvent().getId()).orElseThrow();
        Account account = accountRepository.findById(event.getAccount().getId()).orElseThrow();
        Tables tables = tablesRepository.findById(event.getTables().getId()).orElseThrow();
        Store store = storeRepository.findById(tables.getStore().getId()).orElseThrow();

        Notification notification = new Notification();
        notification.setTitle(store.getName());
        notification.setLink("/store/"+ URLEncoder.encode(store.getPath(), StandardCharsets.UTF_8) + "/tables/"+URLEncoder.encode(tables.getTablesPath(), StandardCharsets.UTF_8));
        notification.setChecked(false);
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notification.setMessage(store.getName()+"의 "+tables.getTablesPath()+"번 테이블에 착석했습니다.");
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.TABLE_ACCEPT);
        notificationRepository.save(notification);
    }
}
