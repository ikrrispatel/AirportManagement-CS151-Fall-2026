package edu.sjsu.airport.model;

import java.time.LocalDateTime;

public class DomesticFlight extends Flight {
    private String operatingState;
    private double domesticTaxRate;
    private boolean residentDiscountAvailable;

    public DomesticFlight(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            int capacity,
            String operatingState,
            double domesticTaxRate,
            boolean residentDiscountAvailable) {

        super(flightNumber, origin, destination, departureTime, arrivalTime, capacity);

        if (operatingState == null || operatingState.isBlank()) {
            throw new IllegalArgumentException("Operating State cannot be null or blank");
        }

        if (domesticTaxRate < 0) {
            throw new IllegalArgumentException("Domestic Tax Rate cannot be negative");
        }

        this.operatingState = operatingState;
        this.domesticTaxRate = domesticTaxRate;
        this.residentDiscountAvailable = residentDiscountAvailable;
    }

    @Override
    public void validateTravelDocuments(Passenger passenger) {
        // TODO: implement domestic travel document validation
    }

    @Override
    public double calculateTravelFee(Passenger passenger) {
        // TODO: implement domestic travel fee calculation
        return 0.0;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        // TODO: implement domestic arrival requirement
        return 0;
    }
}