package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BookingStatus;
import edu.sjsu.airport.exception.PassengerNotCheckedInException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingTest {

    // 1. Confirm pending booking.
    @Test
    void confirmsPendingBooking() {
        Passenger passenger = createPassenger("P101");
        Flight flight = createFlight("SJ201");

        Booking booking = createBooking(
                "B101",
                passenger,
                flight,
                "12A");

        assertEquals(
                BookingStatus.PENDING,
                booking.getStatus());

        booking.confirmBooking();

        assertEquals(
                BookingStatus.CONFIRMED,
                booking.getStatus());
        assertTrue(flight.hasPassenger(passenger));
    }

    // 2. Cancel confirmed booking.
    @Test
    void cancelsConfirmedBooking() {
        Passenger passenger = createPassenger("P102");
        Flight flight = createFlight("SJ202");

        Booking booking = createBooking(
                "B102",
                passenger,
                flight,
                "12A");

        booking.confirmBooking();
        booking.cancelBooking();

        assertEquals(
                BookingStatus.CANCELED,
                booking.getStatus());
        assertFalse(flight.hasPassenger(passenger));
    }

    // 3. Release canceled seat.
    @Test
    void releasesCanceledSeat() {
        Flight flight = createFlight("SJ203");

        Booking booking = createBooking(
                "B103",
                createPassenger("P103"),
                flight,
                "12A");

        booking.confirmBooking();
        booking.cancelBooking();

        assertDoesNotThrow(
                () -> flight.reserveSeat("12A"));
    }

    // 4. Change to available seat.
    @Test
    void changesToAvailableSeat() {
        Flight flight = createFlight("SJ204");

        Booking booking = createBooking(
                "B104",
                createPassenger("P104"),
                flight,
                "12A");

        booking.confirmBooking();
        booking.changeSeat("14B");

        assertEquals("14B", booking.getSeatNumber());

        assertDoesNotThrow(
                () -> flight.reserveSeat("12A"));
    }

    // 5. Reject occupied seat.
    @Test
    void rejectsOccupiedSeat() {
        Flight flight = createFlight("SJ205");

        Booking first = createBooking(
                "B105",
                createPassenger("P105"),
                flight,
                "12A");

        Booking second = createBooking(
                "B106",
                createPassenger("P106"),
                flight,
                "14B");

        first.confirmBooking();
        second.confirmBooking();

        assertThrows(
                IllegalArgumentException.class,
                () -> second.changeSeat("12A"));

        assertEquals("14B", second.getSeatNumber());
    }

    // 6. Move booking to standby.
    @Test
    void movesBookingToStandby() {
        Passenger passenger = createPassenger("P107");
        Flight flight = createFlight("SJ206");

        Booking booking = createBooking(
                "B107",
                passenger,
                flight,
                "12A");

        booking.moveToStandby();

        assertEquals(
                BookingStatus.STANDBY,
                booking.getStatus());
        assertTrue(flight.isOnStandby(passenger));
    }

    // 7. Promote standby booking.
    @Test
    void promotesStandbyBooking() {
        Passenger passenger = createPassenger("P108");
        Flight flight = createFlight("SJ207");

        Booking booking = createBooking(
                "B108",
                passenger,
                flight,
                "12A");

        booking.moveToStandby();

        assertSame(
                passenger,
                flight.promoteStandbyPassenger());

        booking.confirmBooking();

        assertEquals(
                BookingStatus.CONFIRMED,
                booking.getStatus());
        assertTrue(flight.hasPassenger(passenger));
        assertFalse(flight.isOnStandby(passenger));
    }

    // 8. Mark confirmed booking as checked in.
    @Test
    void marksConfirmedBookingAsCheckedIn() {
        Booking booking = createBooking(
                "B109",
                createPassenger("P109"),
                createFlight("SJ208"),
                "12A");

        booking.confirmBooking();
        booking.markCheckedIn();

        assertEquals(
                BookingStatus.CHECKED_IN,
                booking.getStatus());
    }

    // 9. Reject boarding before check-in.
    @Test
    void rejectsBoardingBeforeCheckIn() {
        Booking booking = createBooking(
                "B110",
                createPassenger("P110"),
                createFlight("SJ209"),
                "12A");

        booking.confirmBooking();

        assertThrows(
                PassengerNotCheckedInException.class,
                booking::markBoarded);

        assertEquals(
                BookingStatus.CONFIRMED,
                booking.getStatus());
    }

    // 10. Rebook onto replacement flight.
    @Test
    void rebooksOntoReplacementFlight() {
        Passenger passenger = createPassenger("P111");
        Flight originalFlight = createFlight("SJ210");
        Flight replacementFlight = createFlight("SJ211");

        Booking booking = createBooking(
                "B111",
                passenger,
                originalFlight,
                "12A");

        booking.confirmBooking();
        booking.markRebooked(replacementFlight);

        assertEquals(
                BookingStatus.REBOOKED,
                booking.getStatus());
        assertSame(
                replacementFlight,
                booking.getFlight());
    }

    private Booking createBooking(
            String confirmationNumber,
            Passenger passenger,
            Flight flight,
            String seatNumber) {

        return new Booking(
                confirmationNumber,
                passenger,
                flight,
                seatNumber,
                200.0);
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID-101",
                "P1234567",
                true);
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