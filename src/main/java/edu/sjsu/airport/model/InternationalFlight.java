package edu.sjsu.airport.model;

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
        // TODO: implement international travel document validation
    }

    @Override
    public double calculateTravelFee(Passenger passenger) {
        // TODO: implement international travel fee calculation
        return 0.0;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        // TODO: implement international arrival requirement
        return 0;
    }
}