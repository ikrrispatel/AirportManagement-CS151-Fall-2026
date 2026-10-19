package edu.sjsu.airport.model;

import edu.sjsu.airport.exception.InvalidTravelDocumentException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InternationalFlight extends Flight {
    private static final int REQUIRED_ARRIVAL_MINUTES = 180;

    private String destinationCountry;
    private double internationalTaxRate;
    private List<String> customsRequirements;
    private boolean visaRequired;

    public InternationalFlight(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            int capacity,
            String destinationCountry,
            double internationalTaxRate,
            boolean visaRequired) {

        super(flightNumber, origin, destination, departureTime, arrivalTime, capacity);

        if (destinationCountry == null || destinationCountry.isBlank()) {
            throw new IllegalArgumentException(
                    "Destination Country cannot be null or blank");
        }

        if (!Double.isFinite(internationalTaxRate) || internationalTaxRate < 0) {
            throw new IllegalArgumentException(
                    "International Tax Rate cannot be negative");
        }

        this.destinationCountry = destinationCountry;
        this.internationalTaxRate = internationalTaxRate;
        this.customsRequirements = new ArrayList<>();
        this.visaRequired = visaRequired;
    }

    @Override
    public void validateTravelDocuments(Passenger passenger) {
        validatePassport(passenger);
        validateVisaRequirement(passenger);
    }

    @Override
    public double calculateTravelFee(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare must be a finite, non-negative value.");
        }

        double internationalTax = calculateInternationalTax(baseFare);

        return baseFare + internationalTax;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        return REQUIRED_ARRIVAL_MINUTES;
    }

    @Override
    public boolean isInternational() {
        return true;
    }

    // --- Business Methods --- //
    public void validatePassport(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (passenger.getPassportNumber() == null || passenger.getPassportNumber().isBlank()) {
            throw new InvalidTravelDocumentException(
                    "Passenger must have a valid passport.");
        }
    }

    public void validateVisaRequirement(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (visaRequired && !passenger.isVisaApproved()) {
            throw new InvalidTravelDocumentException(
                    "Passenger must have an approved visa.");
        }
    }

    public double calculateInternationalTax(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare cannot be negative.");
        }

        return baseFare * internationalTaxRate;
    }

    public void addCustomsRequirement(String requirement) {
        if (requirement == null || requirement.isBlank()) {
            throw new IllegalArgumentException(
                    "Customs requirement cannot be null or blank.");
        }

        if (customsRequirements.contains(requirement)) {
            throw new IllegalArgumentException(
                    "Customs requirement already exists.");
        }

        customsRequirements.add(requirement);
    }

    public LocalDateTime determineInternationalCheckInDeadline() {
        return getDepartureTime().minusMinutes(getRequiredArrivalMinutes());
    }

    // --- Object Method Overrides --- //
    @Override
    public String toString() {
        return "InternationalFlight{"
                + "flight=" + super.toString()
                + ", destinationCountry='" + destinationCountry + '\''
                + ", internationalTaxRate=" + internationalTaxRate
                + ", customsRequirements=" + customsRequirements
                + ", visaRequired=" + visaRequired
                + '}';
    }
}