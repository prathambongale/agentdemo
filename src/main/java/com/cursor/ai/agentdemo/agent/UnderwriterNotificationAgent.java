package com.cursor.ai.agentdemo.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI Agent that notifies underwriters about claims requiring review
 */
public interface UnderwriterNotificationAgent {

    @SystemMessage("You are an underwriter notification agent.")
    @UserMessage("""
            Create a detailed notification for the underwriter about a claim that requires their review.
            
            Claim Information:
            Claim ID: {{claimId}}
            Claim Amount: ${{claimAmount}}
            Claim Description: {{claimDescription}}
            Approval Decision: {{approvalDecision}}
            
            Create a comprehensive notification that includes:
            1. The reason for escalation (high amount, suspicious activity, etc.)
            2. Key details about the claim
            3. Any red flags or concerns identified
            4. Recommended action
            
            Return the notification in the following format:
            NOTIFICATION_ID: [id]
            REASON: [reason for escalation]
            DETAILS: [comprehensive details]
            RECOMMENDATION: [your recommendation]
            """)
    @Agent(outputKey = "underwriterNotification", description = "Creates detailed notifications for underwriters about claims requiring review due to high amounts or suspicious patterns")
    String notifyUnderwriter(
            @V("claimId") String claimId,
            @V("claimAmount") String claimAmount,
            @V("claimDescription") String claimDescription,
            @V("approvalDecision") String approvalDecision
    );
}

