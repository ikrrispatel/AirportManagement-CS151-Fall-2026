package edu.sjsu.airport.exceptions;

public class FlightFullException extends RuntimeException {
    public FlightFullException(String message) {
        super(message);
    }
}
