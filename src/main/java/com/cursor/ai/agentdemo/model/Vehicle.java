package com.cursor.ai.agentdemo.model;

import java.time.Year;

/**
 * Represents a vehicle for insurance purposes
 */
public record Vehicle(
        String make,
        String model,
        Year year,
        String vin,
        Integer mileage,
        String color,
        String ownerName,
        Integer ownerAge,
        String drivingLicenseNumber,
        Integer yearsOfDrivingExperience
) {
}

