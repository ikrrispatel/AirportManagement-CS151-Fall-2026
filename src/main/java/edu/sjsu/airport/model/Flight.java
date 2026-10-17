package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.FlightStatus;
import edu.sjsu.airport.interfaces.Trackable;
import edu.sjsu.airport.exception.FlightFullException;

import java.time.LocalDateTime;
import java.util.*;


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

    public Flight(
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            int capacity) {

        if (flightNumber == null || flightNumber.isBlank()) {
            throw new IllegalArgumentException("Flight number cannot be blank.");
        }

        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin cannot be blank.");
        }

        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination cannot be blank.");
        }

        if (origin.equalsIgnoreCase(destination)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same.");
        }

        if (departureTime == null || arrivalTime == null) {
            throw new IllegalArgumentException("Flight times cannot be null.");
        }

        if (!departureTime.isBefore(arrivalTime)) {
            throw new IllegalArgumentException("Departure must be before arrival.");
        }

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
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

    // --- Businesses methods --- //
    public boolean hasAvailableSeat() {
        return passengers.size() < capacity;
    }

    public boolean hasPassenger(Passenger passenger) {
        return passenger != null && passengers.contains(passenger);
    }

    public boolean isOnStandby(Passenger passenger) {
        return passenger != null && standbyQueue.contains(passenger);
    }

    public void removeFromStandby(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (!standbyQueue.remove(passenger)) {
            throw new IllegalArgumentException(
                    "Passenger is not on this flight's standby queue.");
        }
    }

    public void addPassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (passengers.contains(passenger)) {
            throw new IllegalArgumentException("Passenger is already on this flight.");
        }

        if (!hasAvailableSeat()) {
            throw new FlightFullException("Flight " + flightNumber + " is full.");
        }

        passengers.add(passenger);
    }

    public void removePassenger(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (!passengers.contains(passenger)) {
            throw new IllegalArgumentException("Passenger is not on this flight.");
        }

        passengers.remove(passenger);
    }

    public void addToStandby(Passenger passenger) {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }

        if (passengers.contains(passenger) || standbyQueue.contains(passenger)) {
            throw new IllegalArgumentException("Passenger is already on this flight.");
        }

        standbyQueue.offer(passenger);
    }

    public Passenger promoteStandbyPassenger() {
        if (!hasAvailableSeat() || standbyQueue.isEmpty()) {
            return null;
        }

        Passenger passenger = standbyQueue.poll();
        passengers.add(passenger);

        return passenger;
    }

    public void reserveSeat(String seatNumber) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException("Seat number cannot be blank.");
        }

        if (reservedSeats.contains(seatNumber)) {
            throw new IllegalArgumentException("Seat is already reserved.");
        }

        reservedSeats.add(seatNumber);
    }

    public void releaseSeat(String seatNumber) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException("Seat number cannot be blank.");
        }

        if (!reservedSeats.remove(seatNumber)) {
            throw new IllegalArgumentException("Seat is not currently reserved.");
        }
    }

    public void delayFlight(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Delay minutes must be greater than zero.");
        }
        switch (status) {
            case DEPARTED -> throw new IllegalStateException("Cannot delay a departed flight.");

            case ARRIVED -> throw new IllegalStateException("Cannot delay an arrived flight.");

            case CANCELED -> throw new IllegalStateException("Cannot delay a canceled flight.");

            default -> {
                delayMinutes = minutes;
                status = FlightStatus.DELAYED;
            }
        }
    }

    public void openBoarding() {
        switch (status) {
            case SCHEDULED, DELAYED -> status = FlightStatus.BOARDING;

            case CANCELED -> throw new IllegalStateException("Cannot open boarding for a canceled flight.");

            case DEPARTED -> throw new IllegalStateException("Cannot open boarding for a departed flight.");

            case ARRIVED -> throw new IllegalStateException("Cannot re-open boarding for an arrived flight.");

            default -> throw new IllegalStateException("Cannot open boarding when flight status is " + status);
        }
    }

    public void closeBoarding() {
        if (status == FlightStatus.BOARDING) {
            status = FlightStatus.CLOSED;
        }
        else {
            throw new IllegalStateException("Cannot close boarding when flight status is " + status);
        }
    }

    public void markDeparted() {
        if (status == FlightStatus.CLOSED) {
            status = FlightStatus.DEPARTED;
        }
        else {
            throw new IllegalStateException("Cannot mark flight as departed when flight status is " + status);
        }
    }

    public void markArrived() {
        if (status == FlightStatus.DEPARTED) {
            status = FlightStatus.ARRIVED;
        }
        else {
            throw new IllegalStateException("Cannot mark flight as arrived when flight status is " + status);
        }
    }

    // --- Interface methods --- //
    public String getCurrentStatus() {
        return status.toString();
    }

    public void updateStatus(String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("Flight status cannot be blank.");
        }

        FlightStatus newFlightStatus;

        try {
            newFlightStatus = FlightStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid flight status: " + newStatus);
        }

        switch (newFlightStatus) {
            case BOARDING -> openBoarding();
            case CLOSED -> closeBoarding();
            case DEPARTED -> markDeparted();
            case ARRIVED -> markArrived();

            default -> throw new IllegalStateException("Flight status cannot be manually changed to " + newFlightStatus);
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

    // --- Abstracts methods --- //
    public abstract void validateTravelDocuments(Passenger passenger);

    public abstract double calculateTravelFee(double baseFare);

    public abstract int getRequiredArrivalMinutes();

    public abstract boolean isInternational();

    // --- Getters & Setters --- //
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

    // --- Object Method Overrides --- //
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Flight flight)) {
            return false;
        }

        return flightNumber.equals(flight.flightNumber) && departureTime.equals(flight.departureTime);
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