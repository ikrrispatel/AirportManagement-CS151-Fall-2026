package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BaggageStatus;
import edu.sjsu.airport.enums.BookingStatus;
import edu.sjsu.airport.exception.InvalidBookingStateException;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassengerTest {

    // 1. Create valid passenger.
    @Test
    void createsValidPassenger() {
        Passenger passenger = createPassenger("P001");

        assertEquals("P001", passenger.getPassengerId());
        assertEquals("Test Passenger", passenger.getFullName());
        assertEquals("CA-ID-001", passenger.getGovernmentId());
        assertEquals("P1234567", passenger.getPassportNumber());
        assertTrue(passenger.isVisaApproved());
    }

    // 2. Reject blank ID.
    @Test
    void rejectsBlankPassengerId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Passenger(
                        " ",
                        "Test Passenger",
                        "CA-ID-001",
                        "P1234567",
                        true));
    }

    // 3. Request valid booking.
    @Test
    void requestsValidBooking() {
        Passenger passenger = createPassenger("P002");
        Flight flight = createFlight("SJ101");

        assertDoesNotThrow(
                () -> passenger.requestBooking(flight));
    }

    // 4. Reject duplicate active booking.
    @Test
    void rejectsDuplicateActiveBooking() {
        Passenger passenger = createPassenger("P003");
        Flight flight = createFlight("SJ102");

        Booking booking = createBooking(
                "B001",
                passenger,
                flight);

        passenger.addBooking(booking);

        assertThrows(
                IllegalStateException.class,
                () -> passenger.requestBooking(flight));
    }

    // 5. Check in confirmed booking.
    @Test
    void checksInConfirmedBooking() {
        Passenger passenger = createPassenger("P004");

        Booking booking = createBooking(
                "B002",
                passenger,
                createFlight("SJ103"));

        passenger.addBooking(booking);
        booking.confirmBooking();

        passenger.checkIn(booking);

        assertEquals(
                BookingStatus.CHECKED_IN,
                booking.getStatus());
    }

    // 6. Reject check-in for canceled booking.
    @Test
    void rejectsCheckInForCanceledBooking() {
        Passenger passenger = createPassenger("P005");

        Booking booking = createBooking(
                "B003",
                passenger,
                createFlight("SJ104"));

        passenger.addBooking(booking);
        booking.confirmBooking();
        booking.cancelBooking();

        assertThrows(
                InvalidBookingStateException.class,
                () -> passenger.checkIn(booking));

        assertEquals(
                BookingStatus.CANCELED,
                booking.getStatus());
    }

    // 7. Add baggage.
    @Test
    void addsBaggage() {
        Passenger passenger = createPassenger("P006");

        Baggage baggage = new Baggage(
                "TAG-001",
                passenger,
                BaggageStatus.CREATED,
                "Check-in counter",
                15.0);

        passenger.addBaggage(baggage);

        assertTrue(
                passenger.getBaggageItems().contains(baggage));
    }

    // 8. Reject baggage belonging to another passenger.
    @Test
    void rejectsBaggageBelongingToAnotherPassenger() {
        Passenger passenger = createPassenger("P007");
        Passenger owner = createPassenger("P008");

        Baggage baggage = new Baggage(
                "TAG-002",
                owner,
                BaggageStatus.CREATED,
                "Check-in counter",
                15.0);

        assertThrows(
                IllegalArgumentException.class,
                () -> passenger.addBaggage(baggage));
    }

    // 9. Display itinerary.
    @Test
    void displaysItinerary() {
        Passenger passenger = createPassenger("P009");

        Booking booking = createBooking(
                "B004",
                passenger,
                createFlight("SJ105"));

        passenger.addBooking(booking);

        String itinerary = passenger.viewItinerary();

        assertTrue(itinerary.contains("Test Passenger"));
        assertTrue(itinerary.contains("P009"));
        assertTrue(itinerary.contains("B004"));
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID-001",
                "P1234567",
                true);
    }

    private Booking createBooking(
            String confirmationNumber,
            Passenger passenger,
            Flight flight) {

        return new Booking(
                confirmationNumber,
                passenger,
                flight,
                "12A",
                200.0);
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