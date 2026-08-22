package com.saga.project.service;

import com.saga.project.entity.Notification;
import com.saga.project.repository.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JpaNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendNotification(UUID userId, String title, String message, String type) {
        // 1. Save to DB for history and offline read
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();
        notification = notificationRepository.save(notification);

        // 2. Broadcast via WebSocket STOMP
        String destination = "/topic/user." + userId;
        messagingTemplate.convertAndSend(destination, notification);
        log.debug("Sent notification to {}: {}", destination, title);
    }

    @Transactional
    public void sendTeamNotification(UUID teamId, String title, String message, String type) {
        String destination = "/topic/team." + teamId;
        messagingTemplate.convertAndSend(destination, message);
        log.debug("Sent team notification to {}: {}", destination, title);
    }

    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        // Simple but might be slow for massive updates.
        // For production, a custom @Query UPDATE is better.
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .stream()
                .filter(n -> !n.isRead())
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }
}
