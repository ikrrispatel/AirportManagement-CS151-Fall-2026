package edu.sjsu.airport.exception;

/**
 * Thrown when an operation is not allowed for
 * the booking's current status.
 */
public class InvalidBookingStateException
        extends IllegalStateException {

    public InvalidBookingStateException(String message) {
        super(message);
    }
}