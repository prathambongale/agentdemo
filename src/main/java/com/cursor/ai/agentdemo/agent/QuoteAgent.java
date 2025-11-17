package com.cursor.ai.agentdemo.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI Agent that generates insurance quotes for vehicles based on provided information
 */
public interface QuoteAgent {

    @SystemMessage("You are an expert insurance agent specializing in vehicle insurance.")
    @UserMessage("""
            Based on the vehicle information provided, calculate an appropriate insurance quote.
            
            Consider the following factors:
            - Vehicle make, model, and year (newer vehicles typically cost more to insure)
            - Owner's age and driving experience (younger drivers and less experience = higher risk)
            - Vehicle mileage (higher mileage may indicate more wear)
            - Standard coverage should be between $500-$5000 annually depending on vehicle value
            - Coverage amount should be based on vehicle value (typically 80-120% of estimated value)
            
            Vehicle Information:
            Make: {{make}}
            Model: {{model}}
            Year: {{year}}
            Mileage: {{mileage}} miles
            Owner Age: {{ownerAge}} years
            Years of Driving Experience: {{yearsOfDrivingExperience}} years
            
            Provide a detailed quote calculation with reasoning.
            Return the quote in the following format:
            ANNUAL_PREMIUM: [amount]
            COVERAGE_AMOUNT: [amount]
            COVERAGE_TYPE: [type]
            NOTES: [your reasoning]
            """)
    @Agent(outputKey = "quote", description = "Generates insurance quotes for vehicles based on make, model, year, mileage, owner age, and driving experience")
    String generateQuote(
            @V("make") String make,
            @V("model") String model,
            @V("year") String year,
            @V("mileage") String mileage,
            @V("ownerAge") String ownerAge,
            @V("yearsOfDrivingExperience") String yearsOfDrivingExperience
    );
}

