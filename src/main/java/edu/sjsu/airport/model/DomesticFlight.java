package edu.sjsu.airport.model;

import edu.sjsu.airport.exception.InvalidTravelDocumentException;

import java.time.LocalDateTime;

/**
 * Represents a domestic flight with domestic travel document,
 * tax, resident discount, boarding group, and arrival requirements.
 *
 * Domestic passengers are validated using a government-issued ID
 * and are not required to have a passport.
 */
public class DomesticFlight extends Flight {
    private static final double RESIDENT_DISCOUNT_RATE = 0.10;
    private static final int REQUIRED_ARRIVAL_MINUTES = 120;

    private String operatingState;
    private double domesticTaxRate;
    private boolean residentDiscountAvailable;

    /**
     * Creates a domestic flight with its domestic operating information.
     *
     * @param flightNumber unique flight number
     * @param origin flight origin
     * @param destination flight destination
     * @param departureTime scheduled departure time
     * @param arrivalTime scheduled arrival time
     * @param capacity maximum number of confirmed passengers
     * @param operatingState state in which the domestic flight operates
     * @param domesticTaxRate tax rate applied to domestic fares
     * @param residentDiscountAvailable whether the resident discount is available
     * @throws IllegalArgumentException if the operating state is null or blank,
     *         or the domestic tax rate is negative, NaN, or infinite
     */
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
            throw new IllegalArgumentException(
                    "Operating state cannot be null or blank.");
        }

        if (!Double.isFinite(domesticTaxRate) || domesticTaxRate < 0) {
            throw new IllegalArgumentException(
                    "Domestic tax rate must be a finite, non-negative value.");
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
            throw new IllegalArgumentException(
                    "Base fare must be a finite, non-negative value.");
        }

        double discountedFare = applyResidentDiscount(baseFare);
        double domesticTax = calculateDomesticTax(discountedFare);

        return discountedFare + domesticTax;
    }

    @Override
    public int getRequiredArrivalMinutes() {
        return REQUIRED_ARRIVAL_MINUTES;
    }

    @Override
    public boolean isInternational() {
        return false;
    }

    // --- Business Methods --- //

    /**
     * Validates that a passenger has a government-issued ID.
     *
     * @param passenger passenger whose identification is validated
     * @throws IllegalArgumentException if the passenger is null
     * @throws InvalidTravelDocumentException if the passenger does not have
     *         a valid government-issued ID
     */
    public void validateGovernmentId(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (passenger.getGovernmentId() == null || passenger.getGovernmentId().isBlank()) {
            throw new InvalidTravelDocumentException(
                    "Passenger must have a valid government ID.");
        }
    }

    /**
     * Calculates the domestic tax for a fare.
     *
     * @param baseFare fare used to calculate domestic tax
     * @return the calculated domestic tax
     * @throws IllegalArgumentException if baseFare is negative, NaN, or infinite
     */
    public double calculateDomesticTax(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare must be a finite, non-negative value.");
        }

        return baseFare * domesticTaxRate;
    }

    /**
     * Applies the resident discount to the pre-tax base fare when the
     * resident discount is available.
     *
     * @param baseFare pre-tax base fare
     * @return the discounted base fare when the resident discount is available,
     *         otherwise the unchanged base fare
     * @throws IllegalArgumentException if baseFare is negative, NaN, or infinite
     */
    public double applyResidentDiscount(double baseFare) {
        if (!Double.isFinite(baseFare) || baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare must be a finite, non-negative value.");
        }

        if (residentDiscountAvailable) {
            return baseFare * (1.0 - RESIDENT_DISCOUNT_RATE);
        }

        return baseFare;
    }

    /**
     * Determines the boarding group using a back-to-front boarding rule.
     * Rows 21 and above are Group 1, rows 11-20 are Group 2,
     * and rows 1-10 are Group 3.
     *
     * @param seatRow passenger's assigned seat row
     * @return boarding group number
     * @throws IllegalArgumentException if seatRow is less than or equal to zero
     */
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

    // --- Object Method Overrides --- //

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