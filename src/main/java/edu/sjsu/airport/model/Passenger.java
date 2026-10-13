package main.java.edu.sjsu.airport.model;
import java.util.Objects;

public class Passenger
{
/**
 * Represents a passenger registered with the airport management system
 * Passenger Identity is determined by the unique passenger Id;
 */

    private String passengerId;
    private String fullName;
    private String governmentId;
    private String passportNumber;
    private boolean visaApproved;
    
    /**
     * 
     * @param passengerId unique passenger Identifier
     * @param fullName passenger's full name
     * @param governmentId government issued Identification
     * @param passportNumber passport number, or blank if unavailable
     * @param visaApproved whether the passenger has visa approval
     */
    public Passenger(
            String passengerId,
            String fullName,
            String governmentId,
            String passportNumber,
            boolean visaApproved) {

        this.passengerId = passengerId;
        this.fullName = fullName;
        this.governmentId = governmentId;
        this.passportNumber = passportNumber;
        this.visaApproved = visaApproved;
       
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGovernmentId()
    {
        return governmentId;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public boolean isVisaApproved() {
        return visaApproved;
    }

    @Override 
    public boolean equals(Object other) {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof Passenger passenger))
        {
            return false;
        }
        return passengerId.equals(passenger.passengerId);
    }

    @Override 
    public int hashCode() {
        return Objects.hash(passengerId);
    }
}
