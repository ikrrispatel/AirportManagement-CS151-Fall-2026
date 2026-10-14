package edu.sjsu.airport.model;
import java.util.Objects;
import edu.sjsu.airport.enums.BookingStatus;

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
            double baseFare,
            double totalFare,
            BookingStatus status) {
        if (confirmationNumber == null
            || confirmationNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Confirmation number cannot be blank.");
        }

        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (flight == null) {
            throw new IllegalArgumentException(
                    "Flight cannot be null.");
        }

        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be blank.");
        }

        if (baseFare < 0) {
            throw new IllegalArgumentException(
                    "Base fare cannot be negative.");
        }

        this.confirmationNumber = confirmationNumber;
        this.passenger = passenger;
        this.flight = flight;
        this.seatNumber = seatNumber;
        this.baseFare = baseFare;
        this.totalFare = totalFare;
        this.status = status;
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

    public void cancelBooking() {
        if (status == BookingStatus.CANCELED) {
            throw new IllegalStateException(
                "Booking is already canceled.");
        }

        if (status == BookingStatus.BOARDED) {
            throw new IllegalStateException(
                "A boarded booking cannot be canceled.");
        }

        status = BookingStatus.CANCELED;
    }

    public void markCheckedIn() {
        if (status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException(
                "Only confirmed bookings can be checked in.");
        }

        status = BookingStatus.CHECKED_IN;
    }

    public void markBoarded() {
        if (status != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException(
                    "Passenger must check in before boarding.");
        }

        status = BookingStatus.BOARDED;
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
                + ", baseFare='" + baseFare + '\''
                + ", totalFare='" + totalFare + '\''
                + ", status=" + status +
                + '}';
    }
}