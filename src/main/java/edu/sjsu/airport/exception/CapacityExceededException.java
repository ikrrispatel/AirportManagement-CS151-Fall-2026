package edu.sjsu.airport.exception;

/**
 * Thrown when the system reaches the maximum number of allowed objects.
 */
public class CapacityExceededException extends IllegalStateException {

    public CapacityExceededException(String objectType, int maximum) {
        super("Cannot store more than " + maximum
                + " " + objectType + " instances.");
    }
}
