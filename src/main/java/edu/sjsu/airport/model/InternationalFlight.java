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