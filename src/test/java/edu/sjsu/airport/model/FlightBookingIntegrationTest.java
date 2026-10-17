package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightBookingIntegrationTest {

    @Test
    void confirmsStandbyBookingAndRemovesPassengerFromQueue() {
        DomesticFlight flight = createFlight("SJ101");
        Passenger passenger = createPassenger("P001");
        Booking booking = new Booking(
                "B001", passenger, flight, "12A", 200.0);

        booking.moveToStandby();

        assertTrue(flight.isOnStandby(passenger));

        booking.confirmBooking();

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertTrue(flight.hasPassenger(passenger));
        assertFalse(flight.isOnStandby(passenger));
    }

    @Test
    void confirmsBookingAfterFlightPromotesStandbyPassenger() {
        DomesticFlight flight = createFlight("SJ102");
        Passenger passenger = createPassenger("P002");
        Booking booking = new Booking(
                "B002", passenger, flight, "14B", 250.0);

        booking.moveToStandby();

        assertSame(passenger, flight.promoteStandbyPassenger());
        assertTrue(flight.hasPassenger(passenger));
        assertFalse(flight.hasAvailableSeat());

        assertDoesNotThrow(booking::confirmBooking);
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertTrue(flight.hasPassenger(passenger));
        assertFalse(flight.isOnStandby(passenger));
    }

    private DomesticFlight createFlight(String flightNumber) {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                flightNumber,
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                1,
                "California",
                0.08,
                true);
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID-123",
                "",
                false);
    }
}
