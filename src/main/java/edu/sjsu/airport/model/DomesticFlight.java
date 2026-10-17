package edu.sjsu.airport.model;

import edu.sjsu.airport.exceptions.InvalidTravelDocumentException;

import java.time.LocalDateTime;

public class DomesticFlight extends Flight {
    private static final double RESIDENT_DISCOUNT_RATE = 0.10;
    private static final int REQUIRED_ARRIVAL_MINUTES = 120;

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
        validateGovernmentId(passenger);
    }

    @Override
    public double calculateTravelFee(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException("Base fare must be a finite, non-negative value.");
        }

        double discountedFare = applyResidentDiscount(baseFare);
        double domesticTax = calculateDomesticTax(discountedFare);

        return discountedFare + domesticTax;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        return REQUIRED_ARRIVAL_MINUTES;
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

    public double applyResidentDiscount(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException("Base fare must be a finite, non-negative value.");
        }

        if (residentDiscountAvailable) {
            return baseFare * (1.0 - RESIDENT_DISCOUNT_RATE);
        }

        return baseFare;
    }

    public int determineBoardingGroup(int seatRow) {
        if (seatRow <= 0) {
            throw new IllegalArgumentException("Seat row must be greater than zero.");
        }

        if (seatRow >= 21) {
            return 1;
        } else if (seatRow >= 11) {
            return 2;
        } else {
            return 3;
        }
    }

    // --- Overrides from Parent Class --- //
    @Override
    public String toString() {
        return "DomesticFlight{"
                + "flight=" + super.toString()
                + ", operatingState='" + operatingState + '\''
                + ", domesticTaxRate=" + domesticTaxRate
                + ", residentDiscountAvailable=" + residentDiscountAvailable
                + '}';
    }
}