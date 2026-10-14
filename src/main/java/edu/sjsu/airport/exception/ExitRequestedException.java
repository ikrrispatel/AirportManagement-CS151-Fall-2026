package edu.sjsu.airport.exception;

/**
 * Signals that the user entered the global EXIT command.
 */
public class ExitRequestedException extends RuntimeException {

    public ExitRequestedException() {
        super("The user requested to exit the application.");
    }
}
