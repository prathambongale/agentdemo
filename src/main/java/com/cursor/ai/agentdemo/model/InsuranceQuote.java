package com.cursor.ai.agentdemo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an insurance quote for a vehicle
 */
public record InsuranceQuote(
        String quoteId,
        Vehicle vehicle,
        BigDecimal annualPremium,
        BigDecimal coverageAmount,
        String coverageType,
        LocalDateTime quoteDate,
        String status,
        String notes
) {
}

