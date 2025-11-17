package com.cursor.ai.agentdemo.controller;

import com.cursor.ai.agentdemo.dto.ClaimRequest;
import com.cursor.ai.agentdemo.dto.QuoteRequest;
import com.cursor.ai.agentdemo.model.Claim;
import com.cursor.ai.agentdemo.model.InsuranceQuote;
import com.cursor.ai.agentdemo.model.Vehicle;
import com.cursor.ai.agentdemo.service.InsuranceAgentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InsuranceControllerTest {

    @Mock
    private InsuranceAgentService insuranceAgentService;

    private InsuranceController insuranceController;

    @BeforeEach
    void setUp() {
        insuranceController = new InsuranceController(insuranceAgentService);
    }

    @Test
    void testGenerateQuote() {
        Vehicle vehicle = new Vehicle(
                "Honda", "Civic", Year.of(2020), "1HGBH41JXMN109186",
                50000, "Blue", "John Doe", 30, "DL123456", 10
        );

        InsuranceQuote quote = new InsuranceQuote(
                UUID.randomUUID().toString(),
                vehicle,
                new BigDecimal("1500.00"),
                new BigDecimal("25000.00"),
                "Comprehensive",
                LocalDateTime.now(),
                "PENDING",
                "Standard quote"
        );

        when(insuranceAgentService.generateQuote(any())).thenReturn(quote);

        QuoteRequest request = new QuoteRequest(
                "Honda", "Civic", 2020, "1HGBH41JXMN109186",
                50000, "Blue", "John Doe", 30, "DL123456", 10
        );

        ResponseEntity<InsuranceQuote> response = insuranceController.generateQuote(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(quote, response.getBody());
    }

    @Test
    void testProcessClaim() {
        Claim claim = new Claim(
                UUID.randomUUID().toString(),
                "quote-123",
                "1HGBH41JXMN109186",
                new BigDecimal("5000.00"),
                "Minor fender bender",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                Claim.ClaimStatus.APPROVED,
                "Claim approved",
                false
        );

        when(insuranceAgentService.processClaim(any(), any(), any(), any(), any())).thenReturn(claim);

        ClaimRequest request = new ClaimRequest(
                "quote-123",
                "1HGBH41JXMN109186",
                new BigDecimal("5000.00"),
                "Minor fender bender",
                LocalDateTime.now().minusDays(1)
        );

        ResponseEntity<Claim> response = insuranceController.processClaim(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(claim, response.getBody());
    }

    @Test
    void testHealth() {
        ResponseEntity<String> response = insuranceController.health();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Insurance Agent Service is running", response.getBody());
    }
}

