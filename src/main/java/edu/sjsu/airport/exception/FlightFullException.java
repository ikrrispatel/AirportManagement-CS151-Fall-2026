package edu.sjsu.airport.exception;

public class FlightFullException extends RuntimeException {
    public FlightFullException(String message) {
        super(message);
    }
}
