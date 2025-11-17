package com.cursor.ai.agentdemo.dto;

import com.cursor.ai.agentdemo.model.Vehicle;
import java.time.Year;

/**
 * Request DTO for insurance quote
 */
public record QuoteRequest(
        String make,
        String model,
        Integer year,
        String vin,
        Integer mileage,
        String color,
        String ownerName,
        Integer ownerAge,
        String drivingLicenseNumber,
        Integer yearsOfDrivingExperience
) {
    public Vehicle toVehicle() {
        return new Vehicle(
                make,
                model,
                Year.of(year),
                vin,
                mileage,
                color,
                ownerName,
                ownerAge,
                drivingLicenseNumber,
                yearsOfDrivingExperience
        );
    }
}

