package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.FlightStatus;

import java.time.LocalDateTime;
import java.util.*;


public abstract class Flight {
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
}