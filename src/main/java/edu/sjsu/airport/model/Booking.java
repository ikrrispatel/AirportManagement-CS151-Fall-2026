package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BookingStatus;
import edu.sjsu.airport.exception.InvalidBookingStateException;
import edu.sjsu.airport.exception.PassengerNotCheckedInException;

import java.util.Objects;
import java.time.LocalDateTime;

public class Booking {
/**
 * Represents a passenger's reservation of a flight
 * 
 * Booking identity is determined by its unique confirmation number
 */
    private final String confirmationNumber;
    private final Passenger passenger;
    private Flight flight;
    private String seatNumber;
    private final double baseFare;
    private double totalFare;
    private BookingStatus status;
    private final LocalDateTime createdAt;
    
    /**
     * @param confirmationNumber unique booking confirmation number
     * @param passenger passenger making the reservation
     * @param flight flight that is being reserved
     * @param seatNumber requested seat
     * @param baseFare the base price of the booking
     */
    public Booking(
            String confirmationNumber,
            Passenger passenger,
            Flight flight,
            String seatNumber,
            double baseFare) {
        
        this.confirmationNumber = requireText(confirmationNumber, "Confirmation Number"); 
        this.passenger = Objects.requireNonNull(passenger, "Passenger cannot be null.");
        this.flight = Objects.requireNonNull(flight, "Flight cannot be null.");
        this.seatNumber = requireText(seatNumber, "Seat number");

        if (baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare cannot be negative.");
        }

        this.baseFare = baseFare;
        this.totalFare = baseFare;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }

        return value.trim();
    }
    
    public void confirmBooking() {
        if (status != BookingStatus.PENDING
                && status != BookingStatus.STANDBY) {
            throw new InvalidBookingStateException(
                    "Only pending or standby bookings can be confirmed.");
        }

        if (!flight.hasAvailableSeat()) {
            throw new IllegalStateException(
                    "Flight has no available seats.");
        }

        flight.validateTravelDocuments(passenger);

        flight.reserveSeat(seatNumber);
        flight.addPassenger(passenger);

        status = BookingStatus.CONFIRMED;
        calculateTotalFare();
    }

    public void cancelBooking() {
        if (status == BookingStatus.CANCELED) {
            throw new InvalidBookingStateException(
                    "Booking is already canceled.");
        }

        if (status == BookingStatus.BOARDED) {
            throw new InvalidBookingStateException(
                    "A boarded booking cannot be canceled.");
        }

        if (status == BookingStatus.CONFIRMED
                || status == BookingStatus.CHECKED_IN) {
            flight.releaseSeat(seatNumber);
            flight.removePassenger(passenger);
        }

        status = BookingStatus.CANCELED;
    }

    public void changeSeat(String newSeatNumber) {
        if (status != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException(
                    "Only confirmed bookings can change seats.");
        }

        String validatedSeat =
                requireText(newSeatNumber, "Seat number");

        if (validatedSeat.equalsIgnoreCase(seatNumber)) {
            throw new IllegalArgumentException(
                    "New seat must be different from the current seat.");
        }

        flight.reserveSeat(validatedSeat);
        flight.releaseSeat(seatNumber);
        seatNumber = validatedSeat;
    }

    public double calculateTotalFare() {
        totalFare =
                baseFare + flight.calculateTravelFee(baseFare);
        return totalFare;
    }

    public void moveToStandby() {
        if (status != BookingStatus.PENDING) {
            throw new InvalidBookingStateException(
                    "Only pending bookings can move to standby");
        }

        flight.addToStandby(passenger);
        status = BookingStatus.STANDBY;
    }

    public void markCheckedIn() {
        if (status != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException(
                "Only confirmed bookings can be checked in.");
        }

        flight.validateTravelDocuments(passenger);
        status = BookingStatus.CHECKED_IN;
    }

    public void markBoarded() {
        if (status != BookingStatus.CHECKED_IN) {
            throw new PassengerNotCheckedInException(
                    "Passenger must check in before boarding.");
        }

        status = BookingStatus.BOARDED;
    }

    public void markRebooked(Flight replacementFlight) {
        Objects.requireNonNull(replacementFlight, "Replacement Flight cannot be null");
        if (status == BookingStatus.CANCELED
                || status == BookingStatus.BOARDED) {
                    throw new InvalidBookingStateException(
                            "This booking cannot be rebooked.");
        }

        replacementFlight.validateTravelDocuments(passenger);

        if (!replacementFlight.hasAvailableSeat()) {
            throw new IllegalStateException(
                    "Replacement flight has no available seats.");
        }

        if (status == BookingStatus.CONFIRMED
                || status == BookingStatus.CHECKED_IN) {
            flight.releaseSeat(seatNumber);
            flight.removePassenger(passenger);
        }
        
        flight = replacementFlight;
        status = BookingStatus.REBOOKED;
    }
    
    @Override 
    public boolean equals(Object other) {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof Booking booking))
        {
            return false;
        }
        return confirmationNumber.equals(
                booking.confirmationNumber);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(confirmationNumber);
    }

    @Override
    public String toString() {
        return "Booking{"
                + "confirmationNumber='" + confirmationNumber + '\''
                + ", passenger='" + passenger.getPassengerId() + '\''
                + ", flight='" + flight + '\''
                + ", seatNumber='" + seatNumber + '\''
                + ", baseFare='" + baseFare
                + ", totalFare='" + totalFare
                + ", status=" + status
                + ", createdAt=" + createdAt
                + '}';
    }
}