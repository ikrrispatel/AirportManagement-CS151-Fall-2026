package edu.sjsu.airport.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TravelFeeIntegrationTest {

    @Test
    void domesticFareAppliesResidentDiscountBeforeTax() {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);
        DomesticFlight flight = new DomesticFlight(
                "SJ201",
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                10,
                "California",
                0.08,
                true);

        Booking booking = new Booking(
                "B201",
                createPassenger("P201"),
                flight,
                "10A",
                200.0);

        booking.confirmBooking();

        assertEquals(194.40, booking.getTotalFare(), 0.001);
    }

    @Test
    void internationalFareAddsInternationalTaxOnce() {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);
        InternationalFlight flight = new InternationalFlight(
                "SJ202",
                "SFO",
                "LHR",
                departure,
                departure.plusHours(10),
                10,
                "United Kingdom",
                0.20,
                false);

        Booking booking = new Booking(
                "B202",
                createPassenger("P202"),
                flight,
                "20B",
                100.0);

        booking.confirmBooking();

        assertEquals(120.0, booking.getTotalFare(), 0.001);
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID-123",
                "P1234567",
                true);
    }
}
