public class Baggage {
    private String baggageID;
    private String owner;
    private String status;
    private String location;
    private double weight;
    private double maxWeight;

    public Baggage(String baggageID, String owner, String status, String location, double weight, double maxWeight) {
        this.baggageID = baggageID;
        this.owner = owner;
        this.status = status;
        this.location = location;
        this.weight = weight;
        this.maxWeight = maxWeight;

    }

    public String getBaggageID() {
        return baggageID;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public double getWeight() {
        return weight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

}
