package main.java.edu.sjsu.airport.model;

import main.java.edu.sjsu.airport.enums.FlightStatus;

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
}