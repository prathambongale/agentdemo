package com.cursor.ai.agentdemo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for insurance claim
 */
public record ClaimRequest(
        String quoteId,
        String vehicleVin,
        BigDecimal claimAmount,
        String claimDescription,
        LocalDateTime incidentDate
) {
}

