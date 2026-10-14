package edu.sjsu.airport.model;

import java.time.LocalDateTime;

import edu.sjsu.airport.exception.GateUnavailableException;

public class Gate {
    private String gateNumber;
    private String terminal;
    private int maximumPassangerCapacity;
    private boolean internationalCapable;
    private boolean available;
    private Flight assignedFlight;
    private LocalDateTime unavailableUntil;

    public Gate(String gateNumer,String terminal, int maximumPassangerCapacity, boolean internationalCapable, boolean available) {

        if (gateNumer == null || gateNumer.isBlank())
        {
            throw new IllegalArgumentException("Gate Number cannot be blank");
        }

        if(terminal == null || terminal.isBlank())
        {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        if(maximumPassangerCapacity <=0 )
        {
            throw new IllegalArgumentException("A closed gate cannot be available");
        }



        this.gateNumber = gateNumer;
        this.terminal = terminal;
        this.internationalCapable = internationalCapable;
        this.available = true;
        this.maximumPassangerCapacity = maximumPassangerCapacity;
    }

    public String getGateID() {
        return gateNumber;
    }

    public String getTerminal() {
        return terminal;
    }

    public boolean isinternationalCapable() {
        return internationalCapable;
    }

    public boolean isAvailable() {
     if (unavailableUntil != null && !LocalDateTime.now().isBefore(unavailableUntil))
        {
            unavailableUntil = null;
        }

        return available && assignedFlight(null) && unavailableUntil == null;
    }

    public boolean getAssignedFlight() {
        return assignedFlight;
    }

    public boolean canAcceptFlight(Flight flight) {
        if (!isAvailable()) {
            return false;
        }

        if (flight.isinternationalCapable) {
            return false;
        }

        return true;
    }

    public boolean assignedFlight(Flight flight) {
        if (!canAcceptFlight(flight))
        {
            throw new GateUnavailableException();
        }

        assignedFlight = flight;
        available = false;
    }

}