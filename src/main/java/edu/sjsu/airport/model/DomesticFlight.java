package edu.sjsu.airport.model;

import edu.sjsu.airport.exceptions.InvalidTravelDocumentException;

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

    // --- Business methods --- //
    public void validateGovernmentId(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (passenger.getGovernmentId() == null || passenger.getGovernmentId().isBlank()) {
            throw new InvalidTravelDocumentException("Passenger must have a valid government ID.");
        }
    }

    public double calculateDomesticTax(double baseFare) {
        if (baseFare < 0) {
            throw new IllegalArgumentException("Base fare cannot be negative.");
        }

        return baseFare * domesticTaxRate;
    }

    public double applyResidentDiscount(double fare) {
        if (fare < 0) {
            throw new IllegalArgumentException("Fare cannot be negative.");
        }

        if (!residentDiscountAvailable) {
            return fare;
        }

        // TODO: Apply group-approved resident discount.
        return fare;
    }

    public int determineBoardingGroup(int seatRow) {
        if (seatRow <= 0) {
            throw new IllegalArgumentException(
                    "Seat row must be greater than zero.");
        }

        if (seatRow >= 21) {
            return 1;
        } else if (seatRow >= 11) {
            return 2;
        } else {
            return 3;
        }
    }
}