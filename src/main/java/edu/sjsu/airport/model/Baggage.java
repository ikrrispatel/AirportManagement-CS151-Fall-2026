package edu.sjsu.airport.model;

public class Baggage {
    private String tagNumber;
    private Passenger owner;
    private Flight assignedFlight;
    private BaggageStatus status;
    private String currentLocation;
    private double weight;

    public Baggage(String tagNumber, Passenger owner, BaggageStatus status, String currentLocation, double weight) {

        if (tagNumber == null || tagNumber.isBlank()) {
            throw new IllegalArgumentException("Tag number cannot be blank");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        if (currentLocation == null || currentLocation.isBlank()) {
            throw new IllegalArgumentException("Current location cannot be blank");
        }

        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }

        this.tagNumber = tagNumber;
        this.owner = owner;
        this.status = status;
        this.currentLocation = currentLocation;
        this.weight = weight;

    }

    public String getTagNumber() {
        return tagNumber;
    }

    public Passenger getOwner() {
        return owner;
    }

    public Flight getAssignedFlight() {
        return assignedFlight;
    }

    public BaggageStatus getStatus() {
        return status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public double getWeight() {
        return weight;
    }

    public void checkInBaggage(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }

        if (status != BaggageStatus.CREATED) {
            throw new IllegalStateException("Only newly created baggage can be checked in");
        }

        assignedFlight = flight;
        status = BaggageStatus.CHECKED_IN;
    }

    public void loadOnFlight() {
        if (status != BaggageStatus.CHECKED_IN) {
            throw new IllegalStateException("Baggage must be checked in before loaded");
        }

        status = BaggageStatus.LOADED;

    }

    public void transferToFlight(Flight replacementFlight) {
        if (replacementFlight == null) {
            throw new IllegalArgumentException("Replacement flight cannot be null");
        }

        if (status != BaggageStatus.CHECKED_IN && status != BaggageStatus.LOADED && status != BaggageStatus.MISROUTED) {
            throw new IllegalStateException("Baggage cannot be transferred in its current state");
        }

        assignedFlight = replacementFlight;
        status = BaggageStatus.TRANSFERRED;

    }

    public void markMisrouted(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Misrouted baggage location cannot be blank");
        }

        currentLocation = location;
        status = BaggageStatus.MISROUTED;
    }

    public void markReadyForClaim() {
        if (status != BaggageStatus.IN_TRANSIT && status != BaggageStatus.LOADED
                && status != BaggageStatus.TRANSFERRED) {
            throw new IllegalStateException("Baggae is not ready to be claimed");
        }

        status = BaggageStatus.READY_FOR_CLAIM;
    }

    public void markClaimed() {
        if (status != BaggageStatus.READY_FOR_CLAIM) {
            throw new IllegalStateException("Baggage must be ready for claim before collection");
        }

        status = BaggageStatus.CLAIMED;
    }

    @Override
    public String toString() {
        return "Baggage{" +
                "tagNumber=" + tagNumber + '\'' +
                ", owner=" + owner +
                ", assignedFlight" + assignedFlight +
                ", status=" + status +
                ", currentLocation='" + currentLocation + '\'' +
                ", weight=" + weight +
                '}';
    }

}
