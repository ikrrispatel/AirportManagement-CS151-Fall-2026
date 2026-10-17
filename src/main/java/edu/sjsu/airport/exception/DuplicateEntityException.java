package edu.sjsu.airport.exception;

/**
 * Thrown when an object with the same identifying value is registered twice.
 */
public class DuplicateEntityException extends IllegalArgumentException {

    public DuplicateEntityException(
            String objectType,
            String identifier) {

        super("A " + objectType + " with identifier "
                + identifier + " already exists.");
    }
}
