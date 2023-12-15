package com.untactstore.modules.notification;

import com.untactstore.modules.account.repository.AccountRepository;
import com.untactstore.modules.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final AccountRepository accountRepository;
    private final StoreRepository storeRepository;
    private final NotificationRepository notificationRepository;


    public void markAsRead(List<Notification> notifications) {
        notifications.forEach(n -> n.setChecked(true));
        notificationRepository.saveAll(notifications);
    }
}
