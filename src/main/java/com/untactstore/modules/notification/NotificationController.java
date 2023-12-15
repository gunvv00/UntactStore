package com.untactstore.modules.notification;

import com.untactstore.modules.account.Account;
import com.untactstore.modules.account.authentication.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotifications(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        putCategorizedNotifications(model, notifications, numberOfChecked, notifications.size());
        model.addAttribute("isNew", true);
        notificationService.markAsRead(notifications);
        return "notification/list";
    }

    @GetMapping("/notifications/old")
    public String getOldNotifications(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        putCategorizedNotifications(model, notifications, notifications.size(), numberOfNotChecked);
        model.addAttribute("isNew", false);
        return "notification/list";
    }

    @PostMapping("/notifications")
    public String deleteNotifications(@CurrentAccount Account account) {
        System.out.println("들어오냐?");
        List<Notification> notificationList = notificationRepository.findByAccountAndChecked(account, true);
        notificationRepository.deleteAll(notificationList);
        return "redirect:/notifications";
    }

    private void putCategorizedNotifications(Model model, List<Notification> notifications,
                                             long numberOfChecked, long numberOfNotChecked) {
        List<Notification> newStoreNotifications = new ArrayList<>();
        List<Notification> waitingNotifications = new ArrayList<>();
        List<Notification> tableNotifications = new ArrayList<>();

        for (var notification : notifications) {
            switch (notification.getNotificationType()) {
                case STORE_CREATED: newStoreNotifications.add(notification); break;
                case WAITING_CREATED: case WAITING_DELETED: case WAITING_ACCEPT:
                    waitingNotifications.add(notification); break;
                case TABLE_REQUEST: case TABLE_ACCEPT:
                    tableNotifications.add(notification); break;
            }
        }

        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newStoreNotifications", newStoreNotifications);
        model.addAttribute("waitingNotifications", waitingNotifications);
        model.addAttribute("tableNotifications", tableNotifications);
    }
}
