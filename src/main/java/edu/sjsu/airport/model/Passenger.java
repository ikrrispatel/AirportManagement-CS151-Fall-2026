package edu.sjsu.airport.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Passenger
{
/**
 * Represents a passenger registered with the airport management system
 * Passenger Identity is determined by the unique passenger Id;
 */

    private final String passengerId;
    private String fullName;
    private String governmentId;
    private String passportNumber;
    private boolean visaApproved;
    private final List<Booking> bookings;
    
    /**
     * 
     * @param passengerId unique passenger Identifier
     * @param fullName passenger's full name
     * @param governmentId government issued Identification
     * @param passportNumber passport number, or blank if unavailable
     * @param visaApproved whether the passenger has visa approval
     */
    public Passenger(
            String passengerId,
            String fullName,
            String governmentId,
            String passportNumber,
            boolean visaApproved) {

        if (passengerId == null || passengerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Passenger ID cannot be blank.");
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name cannot be blank.");
        }

        if (governmentId == null || governmentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Government ID cannot be blank.");
        }

        this.passengerId = passengerId;
        this.fullName = fullName;
        this.governmentId = governmentId;
        this.passportNumber = passportNumber;
        this.visaApproved = visaApproved;
        this.bookings = new ArrayList<>();
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGovernmentId()
    {
        return governmentId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public boolean isVisaApproved() {
        return visaApproved;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException(
                "Booking cannot be null.");
        }

        if (!this.equals(booking.getPassenger())) {
            throw new IllegalArgumentException(
                "Booking belongs to a different passenger.");
        }

        if (bookings.contains(booking)) {
        throw new IllegalArgumentException(
                "Passenger already has this booking.");
        }

        bookings.add(booking);
    }

    public void cancelBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException(
                "Booking cannot be null.");
        }

        if (!bookings.contains(booking)) {
            throw new IllegalArgumentException(
                "Booking does not belong to this passenger.");
        }

        booking.cancelBooking();
    }

    public void checkIn(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException(
                "Booking cannot be null.");
        }

        if (!bookings.contains(booking)) {
            throw new IllegalArgumentException(
                "Booking does not belong to this passenger.");
        }

        booking.markCheckedIn();
    }

    @Override 
    public boolean equals(Object other) {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof Passenger passenger))
        {
            return false;
        }
        return passengerId.equals(passenger.passengerId);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(passengerId);
    }

    @Override
    public String toString() {
        return "Passenger{"
                + "passengerId='" + passengerId + '\''
                + ", fullName='" + fullName + '\''
                + ", governmentId='" + governmentId + '\''
                + ", passportNumber='" + passportNumber + '\''
                + ", visaApproved=" + visaApproved
                + '}';
    }
}
