package edu.sjsu.airport.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.sjsu.airport.enums.BookingStatus;

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
    private final List<Baggage> baggageItems;
    
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

        this.passengerId = requireText(passengerId, "Passenger ID");
        this.fullName = requireText(fullName, "Full name");
        this.governmentId = requireText(governmentId, "Government ID");
        this.passportNumber = normalizeOptionalText(passportNumber);
        this.visaApproved = visaApproved;
        this.bookings = new ArrayList<>();
        this.baggageItems = new ArrayList<>();
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

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    public List<Baggage> getBaggageItems() {
        return List.copyOf(baggageItems);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }

        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        
        return value.trim();
    }
    
    public void requestBooking(Flight flight) {
        Objects.requireNonNull(flight, "Flight cannot be null");

        for (Booking booking : bookings) {
            if (booking.getFlight().equals(flight)
                    && booking.getStatus() != BookingStatus.CANCELED) {
                throw new IllegalStateException(
                        "Passenger already has an active booking for this flight.");
            }
        }

        flight.validateTravelDocuments(this);
    }

    public void addBooking(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");

        if (!this.equals(booking.getPassenger())) {
            throw new IllegalArgumentException(
                    "Booking belongs to a different passenger.");
        }

        if (bookings.contains(booking)) {
            throw new IllegalArgumentException(
                    "Passenger already has this booking.");
        }

        for (Booking existing : bookings) {
            if (existing.getFlight().equals(booking.getFlight())
                    && existing.getStatus() != BookingStatus.CANCELED) {
                throw new IllegalStateException(
                        "Passenger already has an active booking for this flight.");
            }
        }

        bookings.add(booking);
    }

    public void cancelBooking(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");

        if (!bookings.contains(booking)) {
            throw new IllegalArgumentException(
                "Booking does not belong to this passenger.");
        }

        booking.cancelBooking();
    }

    public void checkIn(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");

        if (!bookings.contains(booking)) {
            throw new IllegalArgumentException(
                "Booking does not belong to this passenger.");
        }

        booking.markCheckedIn();
    }

    public void addBaggage(Baggage baggage) {
        Objects.requireNonNull(baggage, "Baggage cannot be null");

        if(!this.equals(baggage.getOwner())) {
            throw new IllegalArgumentException(
                "Baggage belongs to a different passenger.");        
        }

        if (baggageItems.contains(baggage)) {
            throw new IllegalArgumentException(
                "Baggage is already assigned to this passenger");
        }

        baggageItems.add(baggage);
    }

    public void removeBaggage(Baggage baggage) {
        Objects.requireNonNull(baggage, "Baggage cannot be null");
        
        if(!baggageItems.remove(baggage)) {
            throw new IllegalArgumentException(
                "Baggage does not belong to this passenger");
        }
    }

    public String viewItinerary() {
        if (bookings.isEmpty()) {
            return "No booking found for passenger";
        }

        StringBuilder itinerary = new StringBuilder();
        itinerary.append("Itinerary for ")
                .append(fullName)
                .append(" (")
                .append(passengerId)
                .append("):");
        
        for (Booking booking : bookings) {
            itinerary.append(System.lineSeparator())
                    .append(booking);
        }

        return itinerary.toString();
    }

    public boolean checkConnectionRisk() {
        return bookings.size() > 1;
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
