package edu.sjsu.airport.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MonetaryValidationTest {

    @Test
    void rejectsNonFiniteBookingFare() {
        DomesticFlight flight = createDomesticFlight(0.08);
        Passenger passenger = createPassenger();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Booking(
                        "B301",
                        passenger,
                        flight,
                        "10A",
                        Double.NaN));
    }

    @Test
    void rejectsNonFiniteDomesticTaxRate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createDomesticFlight(Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsNonFiniteInternationalTaxRate() {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        assertThrows(
                IllegalArgumentException.class,
                () -> new InternationalFlight(
                        "SJ302",
                        "SFO",
                        "LHR",
                        departure,
                        departure.plusHours(10),
                        100,
                        "United Kingdom",
                        Double.NaN,
                        true));
    }

    @Test
    void rejectsNonFiniteTaxCalculationInput() {
        DomesticFlight flight = createDomesticFlight(0.08);

        assertThrows(
                IllegalArgumentException.class,
                () -> flight.calculateDomesticTax(
                        Double.POSITIVE_INFINITY));
    }

    private DomesticFlight createDomesticFlight(double taxRate) {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                "SJ301",
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                100,
                "California",
                taxRate,
                true);
    }

    private Passenger createPassenger() {
        return new Passenger(
                "P301",
                "Test Passenger",
                "CA-ID-301",
                "",
                false);
    }
}
