package main.java.edu.sjsu.airport.model;
import java.util.Objects;

public class Booking {
/**
 * Represents a passenger's reservation of a flight
 * 
 * Booking identity is determined by its unique confirmation number
 */
    private final String confirmationNumber;
    private final Passenger passenger;
    private Flight flight;
    private String seatNumber;
    private final double baseFare;
    private double totalFare;
    private BookingStatus status;
    
    /**
     * @param confirmationNumber unique booking confirmation number
     * @param passenger passenger making the reservation
     * @param flight flight that is being reserved
     * @param seatNumber requested seat
     * @param baseFare the base price of the booking
     */
    public Booking(
            String confirmationNumber,
            Passenger passenger,
            Flight flight,
            String seatNumber,
            double baseFare) {
        
        this.confirmationNumber = confirmationNumber;
        this.passenger = passenger;
        this.flight = flight;
        this.seatNumber = seatNumber;
        this.baseFare = baseFare;
    }

}