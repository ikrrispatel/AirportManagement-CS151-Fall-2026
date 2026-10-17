package edu.sjsu.airport.model;

import edu.sjsu.airport.exceptions.*;

import java.time.*;
import java.util.*;

public class InternationalFlight extends Flight {
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
            throw new IllegalArgumentException("Destination Country cannot be null or blank");
        }

        if (internationalTaxRate < 0) {
            throw new IllegalArgumentException("International Tax Rate cannot be negative");
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
        // TODO: implement international travel fee calculation
        return 0.0;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        // TODO: implement international arrival requirement
        return 0;
    }

    // --- Business methods --- //
    public void validatePassport(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (passenger.getPassportNumber() == null || passenger.getPassportNumber().isBlank()) {
            throw new InvalidTravelDocumentException("Passenger must have a valid passport.");
        }
    }

    public void validateVisaRequirement(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (visaRequired && !passenger.isVisaApproved()) {
            throw new InvalidTravelDocumentException("Passenger must have an approved visa.");
        }
    }

    public double calculateInternationalTax(double baseFare) {
        if (baseFare < 0) {
            throw new IllegalArgumentException("Base fare cannot be negative.");
        }

        return baseFare * internationalTaxRate;
    }

    public void addCustomsRequirement(String requirement) {
        if (requirement == null || requirement.isBlank()) {
            throw new IllegalArgumentException("Custom requirement cannot be null or blank");
        }

        if (customsRequirements.contains(requirement)) {
            throw new IllegalArgumentException("Custom requirement already exists.");
        }

        this.customsRequirements.add(requirement);
    }

    public LocalDateTime determineInternationalCheckInDeadline() {
        return getDepartureTime().minusMinutes(getRequiredArrivalMinutes());
    }
}