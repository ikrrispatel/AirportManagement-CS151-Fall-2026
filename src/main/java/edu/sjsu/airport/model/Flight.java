package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.FlightStatus;
import edu.sjsu.airport.exception.FlightFullException;
import edu.sjsu.airport.interfaces.Trackable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Represents a flight managed by the airport system.
 *
 * A flight maintains its schedule, passenger capacity, seat reservations,
 * standby queue, gate assignment, delay information, and current status.
 * Subclasses define flight-specific travel document requirements, fare
 * calculations, and required arrival times.
 *
 * Two flights are functionally equal when they have the same flight number
 * and scheduled departure time.
 */
public abstract class Flight implements Trackable {
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int capacity;
    private FlightStatus status;
    private List<Passenger> passengers;
    private Queue<Passenger> standbyQueue;
    private Set<String> reservedSeats;
    private Gate assignedGate;
    private int delayMinutes;

    /**
     * Creates a flight with its basic schedule and capacity information.
     * A newly created flight starts with SCHEDULED status, no delay,
     * no assigned gate, and empty passenger, standby, and seat collections.
     *
     * @param flightNumber unique flight number
     * @param origin flight origin
     * @param destination flight destination
     * @param departureTime scheduled departure time
     * @param arrivalTime scheduled arrival time
     * @param capacity maximum number of confirmed passengers
     * @throws IllegalArgumentException if required information is null or blank,
     *         the flight times are invalid, or capacity is not positive
     */
    public Flight(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            int capacity) {

        if (flightNumber == null || flightNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Flight number cannot be blank.");
        }

        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException(
                    "Origin cannot be blank.");
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException(
                    "Destination cannot be blank.");
        }

        if (origin.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException(
                    "Origin and destination cannot be the same.");
        }

        if (departureTime == null || arrivalTime == null) {
            throw new IllegalArgumentException(
                    "Flight times cannot be null.");
        }

        if (!departureTime.isBefore(arrivalTime)) {
            throw new IllegalArgumentException(
                    "Departure must be before arrival.");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Capacity must be greater than zero.");
        }

        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.capacity = capacity;

        this.status = FlightStatus.SCHEDULED;
        this.passengers = new ArrayList<>();
        this.standbyQueue = new LinkedList<>();
        this.reservedSeats = new HashSet<>();
        this.assignedGate = null;
        this.delayMinutes = 0;
    }

    // --- Business Methods --- //

    /**
     * Checks whether the flight has room for another confirmed passenger.
     *
     * @return true if the confirmed passenger count is below capacity
     */
    public boolean hasAvailableSeat() {
        return passengers.size() < capacity;
    }

    /**
     * Checks whether a passenger is confirmed on this flight.
     *
     * @param passenger passenger to check
     * @return true if the passenger is currently on the flight
     */
    public boolean hasPassenger(Passenger passenger) {
        return passenger != null && passengers.contains(passenger);
    }

    /**
     * Checks whether a passenger is currently on the standby queue.
     *
     * @param passenger passenger to check
     * @return true if the passenger is on standby
     */
    public boolean isOnStandby(Passenger passenger) {
        return passenger != null && standbyQueue.contains(passenger);
    }

    /**
     * Adds a passenger to the flight if capacity is available.
     *
     * @param passenger passenger to add
     * @throws IllegalArgumentException if the passenger is null or already
     *         confirmed on the flight
     * @throws FlightFullException if the flight has reached capacity
     */
    public void addPassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (passengers.contains(passenger)) {
            throw new IllegalArgumentException(
                    "Passenger is already on this flight.");
        }

        if (!hasAvailableSeat()) {
            throw new FlightFullException(
                    "Flight " + flightNumber + " is full.");
        }

        passengers.add(passenger);
    }

    /**
     * Removes a confirmed passenger from the flight.
     *
     * @param passenger passenger to remove
     * @throws IllegalArgumentException if the passenger is null or is not
     *         currently on the flight
     */
    public void removePassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (!passengers.contains(passenger)) {
            throw new IllegalArgumentException(
                    "Passenger is not on this flight.");
        }

        passengers.remove(passenger);
    }

    /**
     * Adds a passenger to the standby queue.
     *
     * @param passenger passenger to place on standby
     * @throws IllegalArgumentException if the passenger is null, already
     *         confirmed, or already on standby
     */
    public void addToStandby(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (passengers.contains(passenger) || standbyQueue.contains(passenger)) {
            throw new IllegalArgumentException(
                    "Passenger is already on this flight.");
        }

        standbyQueue.offer(passenger);
    }

    /**
     * Removes a passenger from the standby queue.
     *
     * @param passenger passenger to remove from standby
     * @throws IllegalArgumentException if the passenger is null or not
     *         currently on standby
     */
    public void removeFromStandby(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException(
                    "Passenger cannot be null.");
        }

        if (!standbyQueue.remove(passenger)) {
            throw new IllegalArgumentException(
                    "Passenger is not on this flight's standby queue.");
        }
    }

    /**
     * Promotes the first standby passenger when a confirmed seat is available.
     *
     * @return the promoted passenger, or null if no seat is available or the
     *         standby queue is empty
     */
    public Passenger promoteStandbyPassenger() {
        if (!hasAvailableSeat() || standbyQueue.isEmpty()) {
            return null;
        }

        Passenger passenger = standbyQueue.poll();
        passengers.add(passenger);

        return passenger;
    }

    /**
     * Reserves a seat on the flight.
     *
     * @param seatNumber seat number to reserve
     * @throws IllegalArgumentException if the seat number is blank or the seat
     *         is already reserved
     */
    public void reserveSeat(String seatNumber) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be blank.");
        }

        if (reservedSeats.contains(seatNumber)) {
            throw new IllegalArgumentException(
                    "Seat is already reserved.");
        }

        reservedSeats.add(seatNumber);
    }

    /**
     * Releases a previously reserved seat.
     *
     * @param seatNumber seat number to release
     * @throws IllegalArgumentException if the seat number is blank or the seat
     *         is not currently reserved
     */
    public void releaseSeat(String seatNumber) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Seat number cannot be blank.");
        }

        if (!reservedSeats.remove(seatNumber)) {
            throw new IllegalArgumentException(
                    "Seat is not currently reserved.");
        }
    }

    /**
     * Records a delay for a flight that has not departed, arrived, or been
     * canceled.
     *
     * @param minutes number of delay minutes
     * @throws IllegalArgumentException if minutes is not positive
     * @throws IllegalStateException if the flight can no longer be delayed
     */
    public void delayFlight(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException(
                    "Delay minutes must be greater than zero.");
        }

        switch (status) {
            case DEPARTED -> throw new IllegalStateException(
                    "Cannot delay a departed flight.");

            case ARRIVED -> throw new IllegalStateException(
                    "Cannot delay an arrived flight.");

            case CANCELED -> throw new IllegalStateException(
                    "Cannot delay a canceled flight.");

            case SCHEDULED -> {
                delayMinutes = minutes;
                status = FlightStatus.DELAYED;
            }

            default -> delayMinutes = minutes;
        }
    }

    /**
     * Cancels the flight if it has not departed or arrived.
     *
     * @throws IllegalStateException if the flight has already departed,
     *         arrived, or been canceled
     */
    public void cancelFlight() {
        if (status == FlightStatus.DEPARTED
                || status == FlightStatus.ARRIVED) {
            throw new IllegalStateException(
                    "A departed or arrived flight cannot be canceled.");
        }

        if (status == FlightStatus.CANCELED) {
            throw new IllegalStateException(
                    "Flight is already canceled.");
        }

        status = FlightStatus.CANCELED;
    }

    /**
     * Opens boarding for a scheduled or delayed flight.
     *
     * @throws IllegalStateException if boarding cannot be opened from the
     *         current flight status
     */
    public void openBoarding() {
        switch (status) {
            case SCHEDULED, DELAYED -> status = FlightStatus.BOARDING;

            case CANCELED -> throw new IllegalStateException(
                    "Cannot open boarding for a canceled flight.");

            case DEPARTED -> throw new IllegalStateException(
                    "Cannot open boarding for a departed flight.");

            case ARRIVED -> throw new IllegalStateException(
                    "Cannot re-open boarding for an arrived flight.");

            default -> throw new IllegalStateException(
                    "Cannot open boarding when flight status is " + status);
        }
    }

    /**
     * Closes boarding for a flight that is currently boarding.
     *
     * @throws IllegalStateException if the flight is not currently boarding
     */
    public void closeBoarding() {
        if (status == FlightStatus.BOARDING) {
            status = FlightStatus.CLOSED;
        } else {
            throw new IllegalStateException(
                    "Cannot close boarding when flight status is " + status);
        }
    }

    /**
     * Marks the flight as departed after boarding has closed.
     *
     * @throws IllegalStateException if the flight is not closed
     */
    public void markDeparted() {
        if (status == FlightStatus.CLOSED) {
            status = FlightStatus.DEPARTED;
        } else {
            throw new IllegalStateException(
                    "Cannot mark flight as departed when flight status is " + status);
        }
    }

    /**
     * Marks a departed flight as arrived.
     *
     * @throws IllegalStateException if the flight has not departed
     */
    public void markArrived() {
        if (status == FlightStatus.DEPARTED) {
            status = FlightStatus.ARRIVED;
        } else {
            throw new IllegalStateException(
                    "Cannot mark flight as arrived when flight status is " + status);
        }
    }

    // --- Interface Methods --- //

    @Override
    public String getCurrentStatus() {
        return status.toString();
    }

    @Override
    public void updateStatus(String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "Flight status cannot be blank.");
        }

        FlightStatus newFlightStatus;

        try {
            newFlightStatus = FlightStatus.valueOf(newStatus.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid flight status: " + newStatus);
        }

        switch (newFlightStatus) {
            case BOARDING -> openBoarding();
            case CLOSED -> closeBoarding();
            case DEPARTED -> markDeparted();
            case ARRIVED -> markArrived();

            default -> throw new IllegalStateException(
                    "Flight status cannot be manually changed to " + newFlightStatus);
        }
    }

    @Override
    public String getTrackingSummary() {
        return "Flight " + flightNumber
                + ": " + origin + " -> " + destination
                + ", Status: " + status
                + ", Departure: " + departureTime
                + ", Arrival: " + arrivalTime
                + ", Delay: " + delayMinutes + " minutes";
    }

    // --- Abstract Methods --- //

    /**
     * Validates the travel documents required for this type of flight.
     *
     * @param passenger passenger whose documents are validated
     */
    public abstract void validateTravelDocuments(Passenger passenger);

    /**
     * Calculates the final travel fare according to this flight type's
     * pricing rules.
     *
     * @param baseFare pre-tax base fare
     * @return calculated final travel fare
     * @throws IllegalArgumentException if baseFare is invalid
     */
    public abstract double calculateTravelFee(double baseFare);

    /**
     * Returns how many minutes before departure passengers should arrive.
     *
     * @return required arrival time in minutes before departure
     */
    public abstract int getRequiredArrivalMinutes();

    /**
     * Indicates whether this is an international flight.
     *
     * @return true for international flights, false for domestic flights
     */
    public abstract boolean isInternational();

    // --- Getters --- //

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public Gate getAssignedGate() {
        return assignedGate;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    // --- Gate Assignment --- //

    /**
     * Attaches a gate to this flight.
     *
     * @param gate the gate to assign to this flight
     * @throws NullPointerException if the gate is null
     * @throws IllegalStateException if the flight is already assigned
     *         to another gate
     */
    void attachGate(Gate gate) {
        Objects.requireNonNull(gate, "Gate cannot be null.");

        if (assignedGate != null && assignedGate != gate) {
            throw new IllegalStateException(
                    "Flight is already assigned to another gate.");
        }

        assignedGate = gate;
    }

    /**
     * Detaches the currently assigned gate from this flight.
     *
     * @param gate the gate to release from this flight
     * @throws IllegalStateException if the flight is not assigned
     *         to the specified gate
     */
    void detachGate(Gate gate) {
        if (assignedGate != gate) {
            throw new IllegalStateException(
                    "Flight is not assigned to this gate.");
        }

        assignedGate = null;
    }

    // --- Object Method Overrides --- //

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Flight flight)) {
            return false;
        }

        return flightNumber.equals(flight.flightNumber)
                && departureTime.equals(flight.departureTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber, departureTime);
    }

    @Override
    public String toString() {
        return "Flight{"
                + "flightNumber='" + flightNumber + '\''
                + ", origin='" + origin + '\''
                + ", destination='" + destination + '\''
                + ", departureTime=" + departureTime
                + ", arrivalTime=" + arrivalTime
                + ", capacity=" + capacity
                + ", status=" + status
                + ", delayMinutes=" + delayMinutes
                + '}';
    }
}