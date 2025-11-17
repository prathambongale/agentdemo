package com.cursor.ai.agentdemo.model;

import java.time.LocalDateTime;

/**
 * Represents a notification sent to an underwriter
 */
public record UnderwriterNotification(
        String notificationId,
        String claimId,
        String reason,
        String details,
        LocalDateTime notificationDate,
        boolean reviewed
) {
}

