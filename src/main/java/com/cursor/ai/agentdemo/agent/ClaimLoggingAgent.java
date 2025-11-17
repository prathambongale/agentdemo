package com.cursor.ai.agentdemo.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI Agent that logs insurance claims
 */
public interface ClaimLoggingAgent {

    @SystemMessage("You are a claims processing agent.")
    @UserMessage("""
            Log the claim information provided by the user.
            
            Claim Information:
            Quote ID: {{quoteId}}
            Vehicle VIN: {{vehicleVin}}
            Claim Amount: ${{claimAmount}}
            Claim Description: {{claimDescription}}
            Incident Date: {{incidentDate}}
            
            Validate the claim information and log it.
            Return a confirmation message with the claim details.
            Format: CLAIM_LOGGED: [claimId] | STATUS: [status] | NOTES: [any notes]
            """)
    @Agent(outputKey = "claimLog", description = "Logs and validates insurance claims with quote ID, vehicle VIN, claim amount, description, and incident date")
    String logClaim(
            @V("quoteId") String quoteId,
            @V("vehicleVin") String vehicleVin,
            @V("claimAmount") String claimAmount,
            @V("claimDescription") String claimDescription,
            @V("incidentDate") String incidentDate
    );
}

