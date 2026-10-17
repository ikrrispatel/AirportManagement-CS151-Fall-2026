package edu.sjsu.airport.exception;

/**
 * Thrown when a passenger attempts to board
 * before completing check-in.
 */
public class PassengerNotCheckedInException
        extends IllegalStateException {

    public PassengerNotCheckedInException(String message) {
        super(message);
    }
}