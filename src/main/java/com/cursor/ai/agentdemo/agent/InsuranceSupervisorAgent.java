package com.cursor.ai.agentdemo.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Supervisor agent that orchestrates the insurance workflow:
 * - Quote generation
 * - Claim logging
 * - Claim approval
 * - Underwriter notification
 */
public interface InsuranceSupervisorAgent {

    @SystemMessage("""
            You are a supervisor agent for an insurance system. Your role is to coordinate and orchestrate
            the following sub-agents:
            
            1. QuoteAgent - Generates insurance quotes for vehicles
            2. ClaimLoggingAgent - Logs and validates insurance claims
            3. ClaimApprovalAgent - Reviews and approves/rejects claims (can approve up to $10,000)
            4. UnderwriterNotificationAgent - Notifies underwriters about claims requiring review
            
            You should intelligently decide which agents to invoke and in what order based on the user's request.
            For quote requests, use QuoteAgent.
            For claim processing, use ClaimLoggingAgent first, then ClaimApprovalAgent, and if needed, UnderwriterNotificationAgent.
            """)
    @UserMessage("""
            User Request: {{request}}
            
            Based on the user's request, determine the appropriate workflow:
            
            - If the request is about getting an insurance quote, use QuoteAgent with the vehicle information
            - If the request is about filing a claim, use ClaimLoggingAgent first, then ClaimApprovalAgent
            - If ClaimApprovalAgent indicates underwriter review is needed, use UnderwriterNotificationAgent
            
            Execute the appropriate workflow and return the final result.
            """)
    @Agent(description = "Supervisor agent that orchestrates insurance quote generation and claim processing workflows")
    String processRequest(@V("request") String request);
}

