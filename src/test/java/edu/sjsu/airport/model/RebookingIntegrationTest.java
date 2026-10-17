package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RebookingIntegrationTest {

    @Test
    void movesConfirmedBookingToReplacementFlight() {
        Passenger passenger = createPassenger("P701");
        Flight original = createFlight("SJ701");
        Flight replacement = createFlight("SJ702");
        Booking booking = createBooking(
                "B701", passenger, original);

        booking.confirmBooking();
        booking.markRebooked(replacement);

        assertEquals(BookingStatus.REBOOKED, booking.getStatus());
        assertSame(replacement, booking.getFlight());
        assertFalse(original.hasPassenger(passenger));
        assertTrue(replacement.hasPassenger(passenger));
    }

    @Test
    void removesStandbyPassengerFromOriginalFlight() {
        Passenger passenger = createPassenger("P702");
        Flight original = createFlight("SJ703");
        Flight replacement = createFlight("SJ704");
        Booking booking = createBooking(
                "B702", passenger, original);

        booking.moveToStandby();
        booking.markRebooked(replacement);

        assertFalse(original.isOnStandby(passenger));
        assertTrue(replacement.hasPassenger(passenger));
        assertEquals(BookingStatus.REBOOKED, booking.getStatus());
    }

    @Test
    void rebookedReservationCanCheckInAndCancel() {
        Passenger passenger = createPassenger("P703");
        Flight original = createFlight("SJ705");
        Flight replacement = createFlight("SJ706");
        Booking booking = createBooking(
                "B703", passenger, original);

        booking.confirmBooking();
        booking.markRebooked(replacement);
        booking.markCheckedIn();
        booking.cancelBooking();

        assertEquals(BookingStatus.CANCELED, booking.getStatus());
        assertFalse(replacement.hasPassenger(passenger));
    }

    @Test
    void rejectsRebookingOntoSameFlight() {
        Passenger passenger = createPassenger("P704");
        Flight flight = createFlight("SJ707");
        Booking booking = createBooking(
                "B704", passenger, flight);

        assertThrows(
                IllegalArgumentException.class,
                () -> booking.markRebooked(flight));
    }

    @Test
    void rebookedReservationPreventsDuplicateActiveBooking() {
        Passenger passenger = createPassenger("P705");
        Flight original = createFlight("SJ708");
        Flight replacement = createFlight("SJ709");
        Booking first = createBooking(
                "B705", passenger, original);

        passenger.addBooking(first);
        first.confirmBooking();
        first.markRebooked(replacement);

        Booking duplicate = createBooking(
                "B706", passenger, replacement);

        assertThrows(
                IllegalStateException.class,
                () -> passenger.addBooking(duplicate));
    }

    private Booking createBooking(
            String confirmationNumber,
            Passenger passenger,
            Flight flight) {

        return new Booking(
                confirmationNumber,
                passenger,
                flight,
                "15A",
                200.0);
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID-701",
                "",
                false);
    }

    private Flight createFlight(String flightNumber) {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                flightNumber,
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                100,
                "California",
                0.08,
                true);
    }
}
