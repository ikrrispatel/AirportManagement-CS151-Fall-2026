package src.main.java.edu.sjsu.airport.model;
public class Baggage {
    private String tagNumber;
    private Passanger owner;
    private Flight assignedFlight;
    private BaggageStatus status;
    private String currentLocation;
    private double weight;
    

    public Baggage(String tagNumber, Passanger owner, BaggageStatus status, String currentLocationocation, double weight) {
        this.tagNumber = tagNumber;
        this.owner = owner;
        this.status = status;
        this.currentLocation = currentLocationocation;
        this.weight = weight;
        this.BaggageStatus = BaggageStatus;

    }

    public String getTagNumber() {
        return tagNumber;
    }

    public Passanger getOwner() {
        return owner;
    }
    
    public Flight getAssignedFlight()
    {
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

    public double getMaxWeight() {
        return maxWeight;
    }

}
