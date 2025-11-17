package com.cursor.ai.agentdemo.service;

import com.cursor.ai.agentdemo.agent.*;
import com.cursor.ai.agentdemo.model.Claim;
import com.cursor.ai.agentdemo.model.InsuranceQuote;
import com.cursor.ai.agentdemo.model.UnderwriterNotification;
import com.cursor.ai.agentdemo.model.Vehicle;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that orchestrates insurance agents using langchain4j-agentic framework
 */
@Service
public class InsuranceAgentService {

    private final QuoteAgent quoteAgent;
    private final ClaimLoggingAgent claimLoggingAgent;
    private final ClaimApprovalAgent claimApprovalAgent;
    private final UnderwriterNotificationAgent underwriterNotificationAgent;

    @Autowired
    public InsuranceAgentService(
            ChatModel baseChatModel,
            ChatModel plannerChatModel
    ) {
        this(
                AgenticServices.agentBuilder(QuoteAgent.class)
                        .chatModel(baseChatModel)
                        .outputKey("quote")
                        .build(),
                AgenticServices.agentBuilder(ClaimLoggingAgent.class)
                        .chatModel(baseChatModel)
                        .outputKey("claimLog")
                        .build(),
                AgenticServices.agentBuilder(ClaimApprovalAgent.class)
                        .chatModel(plannerChatModel)
                        .outputKey("approvalDecision")
                        .build(),
                AgenticServices.agentBuilder(UnderwriterNotificationAgent.class)
                        .chatModel(baseChatModel)
                        .outputKey("underwriterNotification")
                        .build()
        );
    }

    InsuranceAgentService(
            QuoteAgent quoteAgent,
            ClaimLoggingAgent claimLoggingAgent,
            ClaimApprovalAgent claimApprovalAgent,
            UnderwriterNotificationAgent underwriterNotificationAgent
    ) {
        this.quoteAgent = quoteAgent;
        this.claimLoggingAgent = claimLoggingAgent;
        this.claimApprovalAgent = claimApprovalAgent;
        this.underwriterNotificationAgent = underwriterNotificationAgent;
    }

    /**
     * Generate an insurance quote for a vehicle
     */
    public InsuranceQuote generateQuote(Vehicle vehicle) {
        // Invoke the quote agent with vehicle information
        String quoteResult = quoteAgent.generateQuote(
                vehicle.make(),
                vehicle.model(),
                vehicle.year().toString(),
                vehicle.mileage().toString(),
                vehicle.ownerAge().toString(),
                vehicle.yearsOfDrivingExperience().toString()
        );

        return parseQuote(quoteResult, vehicle);
    }

    /**
     * Process a claim: log it, approve/reject it, and notify underwriter if needed
     */
    public Claim processClaim(String quoteId, String vehicleVin, BigDecimal claimAmount,
                             String claimDescription, LocalDateTime incidentDate) {
        String claimId = UUID.randomUUID().toString();
        
        String claimLog = claimLoggingAgent.logClaim(
                quoteId,
                vehicleVin,
                claimAmount.toPlainString(),
                claimDescription,
                incidentDate.toString()
        );

        String approvalDecision = claimApprovalAgent.approveClaim(
                claimId,
                claimAmount.toPlainString(),
                claimDescription,
                claimLog
        );

        // Parse the decision
        ClaimDecision decision = parseClaimDecision(approvalDecision);
        
        // Step 3: If underwriter review is needed, create notification
        if (decision.requiresUnderwriterReview()) {
            String notificationResult = underwriterNotificationAgent.notifyUnderwriter(
                    claimId,
                    claimAmount.toPlainString(),
                    claimDescription,
                    approvalDecision
            );
            // Parse and log notification (can be stored in database in future)
            UnderwriterNotification notification = parseNotification(notificationResult, claimId);
            // TODO: Store notification in database or send to underwriter system
        }

        return new Claim(
                claimId,
                quoteId,
                vehicleVin,
                claimAmount,
                claimDescription,
                incidentDate,
                LocalDateTime.now(),
                decision.status(),
                decision.notes(),
                decision.requiresUnderwriterReview()
        );
    }

    private InsuranceQuote parseQuote(String quoteResult, Vehicle vehicle) {
        Pattern premiumPattern = Pattern.compile("ANNUAL_PREMIUM:\\s*\\$?([\\d,]+(?:\\.\\d{2})?)");
        Pattern coveragePattern = Pattern.compile("COVERAGE_AMOUNT:\\s*\\$?([\\d,]+(?:\\.\\d{2})?)");
        Pattern typePattern = Pattern.compile("COVERAGE_TYPE:\\s*(.+)");
        Pattern notesPattern = Pattern.compile("NOTES:\\s*(.+)");

        BigDecimal annualPremium = extractBigDecimal(quoteResult, premiumPattern);
        BigDecimal coverageAmount = extractBigDecimal(quoteResult, coveragePattern);
        String coverageType = extractString(quoteResult, typePattern, "Comprehensive");
        String notes = extractString(quoteResult, notesPattern, "Standard vehicle insurance coverage");

        return new InsuranceQuote(
                UUID.randomUUID().toString(),
                vehicle,
                annualPremium,
                coverageAmount,
                coverageType,
                LocalDateTime.now(),
                "PENDING",
                notes
        );
    }

    private ClaimDecision parseClaimDecision(String decisionText) {
        Pattern decisionPattern = Pattern.compile("DECISION:\\s*(APPROVED|REJECTED|UNDERWRITER_REVIEW|SUSPICIOUS)");
        Pattern notesPattern = Pattern.compile("NOTES:\\s*(.+)");
        Pattern requiresReviewPattern = Pattern.compile("REQUIRES_UNDERWRITER:\\s*(true|false)");

        String decisionStr = extractString(decisionText, decisionPattern, "PENDING");
        String notes = extractString(decisionText, notesPattern, "");
        boolean requiresReview = extractBoolean(decisionText, requiresReviewPattern);

        Claim.ClaimStatus status = switch (decisionStr.toUpperCase()) {
            case "APPROVED" -> Claim.ClaimStatus.APPROVED;
            case "REJECTED" -> Claim.ClaimStatus.REJECTED;
            case "UNDERWRITER_REVIEW", "SUSPICIOUS" -> Claim.ClaimStatus.UNDERWRITER_REVIEW;
            default -> Claim.ClaimStatus.PENDING;
        };

        return new ClaimDecision(status, notes, requiresReview || status == Claim.ClaimStatus.UNDERWRITER_REVIEW);
    }

    private UnderwriterNotification parseNotification(String notificationText, String claimId) {
        Pattern reasonPattern = Pattern.compile("REASON:\\s*(.+)");
        Pattern detailsPattern = Pattern.compile("DETAILS:\\s*(.+)");
        Pattern recommendationPattern = Pattern.compile("RECOMMENDATION:\\s*(.+)");

        String reason = extractString(notificationText, reasonPattern, "Claim requires underwriter review");
        String details = extractString(notificationText, detailsPattern, "");
        String recommendation = extractString(notificationText, recommendationPattern, "Review claim details");

        return new UnderwriterNotification(
                UUID.randomUUID().toString(),
                claimId,
                reason,
                details + "\nRecommendation: " + recommendation,
                LocalDateTime.now(),
                false
        );
    }

    private BigDecimal extractBigDecimal(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                String value = matcher.group(1).replace(",", "");
                return new BigDecimal(value);
            } catch (Exception e) {
                // Return default if parsing fails
            }
        }
        return BigDecimal.ZERO;
    }

    private String extractString(String text, Pattern pattern, String defaultValue) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return defaultValue;
    }

    private boolean extractBoolean(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return false;
    }

    private record ClaimDecision(
            Claim.ClaimStatus status,
            String notes,
            boolean requiresUnderwriterReview
    ) {}
}

