package edu.sjsu.airport.model;

import edu.sjsu.airport.exception.InvalidTravelDocumentException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an international flight with international travel document,
 * tax, customs, visa, and arrival requirements.
 *
 * International passengers are required to have a valid passport and,
 * when required by the flight, an approved visa.
 */
public class InternationalFlight extends Flight {
    private static final int REQUIRED_ARRIVAL_MINUTES = 180;

    private String destinationCountry;
    private double internationalTaxRate;
    private List<String> customsRequirements;
    private boolean visaRequired;

    /**
     * Creates an international flight with its international operating
     * information.
     *
     * @param flightNumber unique flight number
     * @param origin flight origin
     * @param destination flight destination
     * @param departureTime scheduled departure time
     * @param arrivalTime scheduled arrival time
     * @param capacity maximum number of confirmed passengers
     * @param destinationCountry destination country of the flight
     * @param internationalTaxRate tax rate applied to international fares
     * @param visaRequired whether passengers require visa approval
     * @throws IllegalArgumentException if the destination country is null or
     *         blank, or the international tax rate is negative, NaN, or infinite
     */
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
                    "Destination country cannot be null or blank.");
        }

        if (!Double.isFinite(internationalTaxRate) || internationalTaxRate < 0) {
            throw new IllegalArgumentException(
                    "International tax rate must be a finite, non-negative value.");
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

    /**
     * Validates that a passenger has a passport.
     *
     * @param passenger passenger whose passport is validated
     * @throws IllegalArgumentException if the passenger is null
     * @throws InvalidTravelDocumentException if the passenger does not have
     *         a valid passport
     */
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

    /**
     * Validates a passenger's visa approval when a visa is required
     * for this flight.
     *
     * @param passenger passenger whose visa approval is validated
     * @throws IllegalArgumentException if the passenger is null
     * @throws InvalidTravelDocumentException if a visa is required and the
     *         passenger does not have visa approval
     */
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

    /**
     * Calculates the international tax for a fare.
     *
     * @param baseFare fare used to calculate international tax
     * @return the calculated international tax
     * @throws IllegalArgumentException if baseFare is negative, NaN, or infinite
     */
    public double calculateInternationalTax(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare must be a finite, non-negative value.");
        }

        return baseFare * internationalTaxRate;
    }

    /**
     * Adds a customs requirement to the flight.
     *
     * @param requirement customs requirement to add
     * @throws IllegalArgumentException if the requirement is null or blank,
     *         or if the requirement already exists
     */
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

    /**
     * Determines the international check-in deadline using the scheduled
     * departure time and required arrival time.
     *
     * @return the required check-in deadline
     */
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