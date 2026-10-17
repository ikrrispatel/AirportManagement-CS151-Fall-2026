package edu.sjsu.airport.exceptions;

public class InvalidTravelDocumentException extends RuntimeException {
    public InvalidTravelDocumentException(String message) {
        super(message);
    }
}
