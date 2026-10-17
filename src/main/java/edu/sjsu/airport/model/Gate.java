package edu.sjsu.airport.model;

import java.time.LocalDateTime;

import edu.sjsu.airport.exception.GateUnavailableException;

public class Gate {
    private String gateNumber;
    private String terminal;
    private int maximumPassengerCapacity;
    private boolean internationalCapable;
    private boolean available;
    private Flight assignedFlight;
    private LocalDateTime unavailableUntil;

    public Gate(String gateNumber, String terminal, int maximumPassengerCapacity, boolean internationalCapable,
            boolean available) {

        if (gateNumber == null || gateNumber.isBlank()) {
            throw new IllegalArgumentException("Gate Number cannot be blank");
        }

        if (terminal == null || terminal.isBlank()) {
            throw new IllegalArgumentException("Terminal cannot be blank");
        }

        if (maximumPassengerCapacity <= 0) {
            throw new IllegalArgumentException("Passenger capacity must be greater than 0");
        }

        this.gateNumber = gateNumber;
        this.terminal = terminal;
        this.internationalCapable = internationalCapable;
        this.available = available;
        this.maximumPassengerCapacity = maximumPassengerCapacity;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public String getTerminal() {
        return terminal;
    }

    public int getMaximumPassengerCapacity() {
        return maximumPassengerCapacity;
    }

    public LocalDateTime getUnavailableUntil() {
        return unavailableUntil;
    }

    public Flight getAssignedFlight() {
        return assignedFlight;
    }

    public boolean isInternationalCapable() {
        return internationalCapable;
    }

    public boolean isAvailable() {
        if (unavailableUntil != null && !LocalDateTime.now().isBefore(unavailableUntil)) {
            unavailableUntil = null;

            if (assignedFlight == null) {
                available = true;
            }
        }

        return available && assignedFlight == null && unavailableUntil == null;
    }

    public boolean canAcceptFlight(Flight flight) {
        if (flight == null || !isAvailable()) {
            return false;
        }

        if (flight.isInternationalCapable() && !internationalCapable) {
            return false;
        }

        return true;
    }

    public void assignFlight(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null.");
        }

        if (!canAcceptFlight(flight)) {
            throw new GateUnavailableException();
        }

        assignedFlight = flight;
        available = false;
    }

    public void releaseFlight() {
        if (assignedFlight == null) {
            throw new IllegalStateException("No Flight currently assigned to this gate");
        }

        assignedFlight = null;
        available = true;
    }

    public void markTemporarilyUnavailable(LocalDateTime until) {
        if (assignedFlight != null) {
            throw new IllegalStateException("Cannot mark gate unavailable while flight is assigned");
        }

        if (until == null || !until.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Unavailable time");
        }

        unavailableUntil = until;
    }

    public void restoreAvailability() {
        unavailableUntil = null;

        if (assignedFlight == null) {
            available = true;
        } else {
            available = false;
        }
    }

    @Override
    public String toString() {
        return "Gate{" +
                "gateNumber='" + gateNumber + '\'' +
                ", terminal='" + terminal + '\'' +
                ", maximumPassengerCapacity=" + maximumPassengerCapacity +
                ", internationalCapable=" + internationalCapable +
                ", available=" + isAvailable() +
                ", assignedFlight=" + assignedFlight +
                ", unavailableUntil=" + unavailableUntil +
                '}';

    }

}