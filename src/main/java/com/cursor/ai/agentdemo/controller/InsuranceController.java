package com.cursor.ai.agentdemo.controller;

import com.cursor.ai.agentdemo.dto.ClaimRequest;
import com.cursor.ai.agentdemo.dto.QuoteRequest;
import com.cursor.ai.agentdemo.model.Claim;
import com.cursor.ai.agentdemo.model.InsuranceQuote;
import com.cursor.ai.agentdemo.service.InsuranceAgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for insurance operations
 */
@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {

    private final InsuranceAgentService insuranceAgentService;

    public InsuranceController(InsuranceAgentService insuranceAgentService) {
        this.insuranceAgentService = insuranceAgentService;
    }

    @PostMapping("/quote")
    public ResponseEntity<InsuranceQuote> generateQuote(@RequestBody QuoteRequest request) {
        try {
            InsuranceQuote quote = insuranceAgentService.generateQuote(request.toVehicle());
            return ResponseEntity.ok(quote);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/claim")
    public ResponseEntity<Claim> processClaim(@RequestBody ClaimRequest request) {
        try {
            Claim claim = insuranceAgentService.processClaim(
                    request.quoteId(),
                    request.vehicleVin(),
                    request.claimAmount(),
                    request.claimDescription(),
                    request.incidentDate()
            );
            return ResponseEntity.ok(claim);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Insurance Agent Service is running");
    }
}

