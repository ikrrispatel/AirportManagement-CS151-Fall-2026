package edu.sjsu.airport.exception;

public class InvalidTravelDocumentException extends RuntimeException {
    public InvalidTravelDocumentException(String message) {
        super(message);
    }
}
