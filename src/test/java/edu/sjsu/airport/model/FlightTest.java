package edu.sjsu.airport.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import edu.sjsu.airport.exception.FlightFullException;
import edu.sjsu.airport.enums.FlightStatus;

class FlightTest {

    private Flight flight;

    @BeforeEach
    void setUp() {
        flight = new DomesticFlight(
                "SJ101",
                "SJC",
                "LAX",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 20, 12, 0),
                10,
                "CA",
                0.10,
                false
        );
    }

    @Test
    void addPassengerToAvailableFlight() {
        Passenger passenger = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "",
                false
        );

        flight.addPassenger(passenger);

        assertTrue(flight.hasAvailableSeat());
    }

    @Test
    void rejectDuplicatePassenger() {
        Passenger passenger = new Passenger(
                "P002",
                "Test Passenger",
                "ID002",
                "",
                false
        );

        flight.addPassenger(passenger);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.addPassenger(passenger));
        System.out.println(exception.getMessage());
    }

    @Test
    void rejectPassengerWhenFlightIsFull() {
        for (int i = 1; i <= 10; i++) {
            Passenger passenger = new Passenger(
                    "P00" + i,
                    "Test Passenger",
                    "ID00" + i,
                    "",
                    false
            );

            flight.addPassenger(passenger);
        }

        Passenger extraPassenger = new Passenger(
                "P011",
                "Test Passenger",
                "ID011",
                "",
                false
        );

        FlightFullException exception = assertThrows(FlightFullException.class, () -> flight.addPassenger(extraPassenger));
        System.out.println(exception.getMessage());
    }

    @Test
    void promoteFirstStandbyPassenger() {
        Passenger passenger1 = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "",
                false
        );

        Passenger passenger2 = new Passenger(
                "P002",
                "Test Passenger",
                "ID002",
                "",
                false
        );

        flight.addToStandby(passenger1);
        flight.addToStandby(passenger2);

        assertEquals(passenger1, flight.promoteStandbyPassenger());
    }

    @Test
    void reserveAvailableSeat() {
        assertDoesNotThrow(() -> flight.reserveSeat("A1"));
    }

    @Test
    void rejectReservedSeat() {
        flight.reserveSeat("A1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.reserveSeat("A1"));
        System.out.println(exception.getMessage());
    }

    @Test
    void releaseReservedSeat() {
        flight.reserveSeat("A1");

        flight.releaseSeat("A1");

        assertDoesNotThrow(() -> flight.reserveSeat("A1"));
    }

    @Test
    void delayFlight() {
        flight.delayFlight(30);

        assertEquals(30, flight.getDelayMinutes());
        assertEquals(FlightStatus.DELAYED, flight.getStatus());
    }

    @Test
    void rejectNegativeDelay() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.delayFlight(-30));
        System.out.println(exception.getMessage());
    }

    @Test
    void openBoarding() {
        flight.openBoarding();

        assertEquals(FlightStatus.BOARDING,  flight.getStatus());
    }

    @Test
    void verifyDomesticAndInternationalPolymorphism() {
        Flight domestic = new DomesticFlight(
                "D101",
                "SJC",
                "LAX",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 20, 12, 0),
                100,
                "CA",
                0.10,
                false
        );

        Flight international = new InternationalFlight(
                "I101",
                "SFO",
                "NRT",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 21, 0, 0),
                200,
                "Japan",
                0.10,
                false
        );

        assertEquals(120, domestic.getRequiredArrivalMinutes());
        assertEquals(180, international.getRequiredArrivalMinutes());
    }
}