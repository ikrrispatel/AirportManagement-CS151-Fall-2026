package edu.sjsu.airport.exception;

/**
 * Thrown when an airport with the same airport code already exists.
 */
public class DuplicateAirportException extends IllegalArgumentException {

    public DuplicateAirportException(String airportCode) {
        super("An airport with code " + airportCode + " already exists.");
    }
}
