package com.cursor.ai.agentdemo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an insurance claim
 */
public record Claim(
        String claimId,
        String quoteId,
        String vehicleVin,
        BigDecimal claimAmount,
        String claimDescription,
        LocalDateTime incidentDate,
        LocalDateTime claimDate,
        ClaimStatus status,
        String approvalNotes,
        boolean requiresUnderwriterReview
) {
    public enum ClaimStatus {
        PENDING,
        APPROVED,
        REJECTED,
        UNDERWRITER_REVIEW,
        SUSPICIOUS
    }
}

