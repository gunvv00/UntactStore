package com.untactstore.modules.notification;

import com.untactstore.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly=true)
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(Account account, boolean checked);

    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findByAccountAndChecked(Account account, boolean b);
}
