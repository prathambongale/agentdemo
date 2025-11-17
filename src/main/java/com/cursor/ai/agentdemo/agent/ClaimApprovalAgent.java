package com.cursor.ai.agentdemo.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI Agent that approves or rejects claims based on amount and other factors
 */
public interface ClaimApprovalAgent {

    @SystemMessage("You are a claims approval agent with authority to approve claims up to $10,000.")
    @UserMessage("""
            Review the following claim:
            
            Claim Information:
            Claim ID: {{claimId}}
            Claim Amount: ${{claimAmount}}
            Claim Description: {{claimDescription}}
            Previous Claim Log: {{claimLog}}
            
            Decision Rules:
            1. If claim amount is $10,000 or less and there are no suspicious indicators, APPROVE it
            2. If claim amount exceeds $10,000, mark for UNDERWRITER_REVIEW
            3. If you detect any suspicious patterns (e.g., frequent claims, unusual descriptions, 
               inconsistencies), mark as SUSPICIOUS and require UNDERWRITER_REVIEW
            4. If the claim seems legitimate and within your authority, APPROVE it
            
            Return your decision in the following format:
            DECISION: [APPROVED/REJECTED/UNDERWRITER_REVIEW/SUSPICIOUS]
            AMOUNT: ${{claimAmount}}
            NOTES: [your reasoning]
            REQUIRES_UNDERWRITER: [true/false]
            """)
    @Agent(outputKey = "approvalDecision", description = "Reviews and approves or rejects insurance claims. Can approve claims up to $10,000. Escalates to underwriter for high amounts or suspicious patterns")
    String approveClaim(
            @V("claimId") String claimId,
            @V("claimAmount") String claimAmount,
            @V("claimDescription") String claimDescription,
            @V("claimLog") String claimLog
    );
}

