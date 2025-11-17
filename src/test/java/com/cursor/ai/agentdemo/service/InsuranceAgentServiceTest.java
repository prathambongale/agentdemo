package com.cursor.ai.agentdemo.service;

import com.cursor.ai.agentdemo.agent.ClaimApprovalAgent;
import com.cursor.ai.agentdemo.agent.ClaimLoggingAgent;
import com.cursor.ai.agentdemo.agent.QuoteAgent;
import com.cursor.ai.agentdemo.agent.UnderwriterNotificationAgent;
import com.cursor.ai.agentdemo.model.Claim;
import com.cursor.ai.agentdemo.model.InsuranceQuote;
import com.cursor.ai.agentdemo.model.Vehicle;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceAgentServiceTest {

    private InsuranceAgentService createService(
            QuoteAgent quoteAgent,
            ClaimLoggingAgent claimLoggingAgent,
            ClaimApprovalAgent claimApprovalAgent,
            UnderwriterNotificationAgent underwriterNotificationAgent
    ) {
        return new InsuranceAgentService(
                quoteAgent,
                claimLoggingAgent,
                claimApprovalAgent,
                underwriterNotificationAgent
        );
    }

    @Test
    void testGenerateQuote() {
        var insuranceAgentService = createService(
                (make, model, year, mileage, ownerAge, yearsOfDrivingExperience) -> """
                        ANNUAL_PREMIUM: $1500.00
                        COVERAGE_AMOUNT: $25000.00
                        COVERAGE_TYPE: Comprehensive
                        NOTES: Standard quote for a 2020 Honda Civic with a 30-year-old driver with 10 years of experience.
                        """,
                (quoteId, vehicleVin, claimAmount, claimDescription, incidentDate) -> "",
                (claimId, claimAmount, claimDescription, claimLog) -> "",
                (claimId, claimAmount, claimDescription, approvalDecision) -> ""
        );

        Vehicle vehicle = new Vehicle(
                "Honda",
                "Civic",
                Year.of(2020),
                "1HGBH41JXMN109186",
                50000,
                "Blue",
                "John Doe",
                30,
                "DL123456",
                10
        );

        InsuranceQuote quote = insuranceAgentService.generateQuote(vehicle);

        assertNotNull(quote);
        assertNotNull(quote.quoteId());
        assertEquals(vehicle, quote.vehicle());
        assertTrue(quote.annualPremium().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(quote.coverageAmount().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(quote.coverageType());
        assertNotNull(quote.notes());
    }

    @Test
    void testProcessClaim() {
        var insuranceAgentService = createService(
                (make, model, year, mileage, ownerAge, yearsOfDrivingExperience) -> "",
                (quoteId, vehicleVin, claimAmount, claimDescription, incidentDate) ->
                        "CLAIM_LOGGED: claim-123 | STATUS: PENDING | NOTES: Claim logged successfully",
                (claimId, claimAmount, claimDescription, claimLog) -> """
                        DECISION: APPROVED
                        AMOUNT: $5000.00
                        NOTES: Claim amount is within approval authority. No suspicious indicators.
                        REQUIRES_UNDERWRITER: false
                        """,
                (claimId, claimAmount, claimDescription, approvalDecision) -> ""
        );

        Claim claim = insuranceAgentService.processClaim(
                "quote-123",
                "1HGBH41JXMN109186",
                new BigDecimal("5000.00"),
                "Minor fender bender in parking lot",
                LocalDateTime.now().minusDays(1)
        );

        assertNotNull(claim);
        assertNotNull(claim.claimId());
        assertEquals("quote-123", claim.quoteId());
        assertEquals(new BigDecimal("5000.00"), claim.claimAmount());
        assertFalse(claim.requiresUnderwriterReview());
    }

    @Test
    void testProcessHighValueClaim() {
        var insuranceAgentService = createService(
                (make, model, year, mileage, ownerAge, yearsOfDrivingExperience) -> "",
                (quoteId, vehicleVin, claimAmount, claimDescription, incidentDate) ->
                        "CLAIM_LOGGED: claim-456 | STATUS: PENDING | NOTES: High-value claim logged",
                (claimId, claimAmount, claimDescription, claimLog) -> """
                        DECISION: UNDERWRITER_REVIEW
                        AMOUNT: $15000.00
                        NOTES: Claim amount exceeds approval authority of $10,000. Requires underwriter review.
                        REQUIRES_UNDERWRITER: true
                        """,
                (claimId, claimAmount, claimDescription, approvalDecision) -> """
                        NOTIFICATION_ID: notif-789
                        REASON: High-value claim exceeding approval authority
                        DETAILS: Claim amount of $15,000 exceeds the $10,000 approval limit.
                        RECOMMENDATION: Review claim details and vehicle damage assessment.
                        """
        );

        Claim claim = insuranceAgentService.processClaim(
                "quote-123",
                "1HGBH41JXMN109186",
                new BigDecimal("15000.00"),
                "Major collision resulting in significant vehicle damage",
                LocalDateTime.now().minusDays(2)
        );

        assertNotNull(claim);
        assertTrue(claim.requiresUnderwriterReview());
        assertEquals(Claim.ClaimStatus.UNDERWRITER_REVIEW, claim.status());
    }
}

