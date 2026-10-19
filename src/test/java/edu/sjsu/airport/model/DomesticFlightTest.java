package edu.sjsu.airport.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;


class DomesticFlightTest {
    private DomesticFlight flight;

    @BeforeEach
    void setUp() {
        flight = new DomesticFlight(
                "D101",
                "SJC",
                "LAX",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 20, 12, 0),
                100,
                "CA",
                0.10,
                true
        );
    }


    // --- Constructor Validation Tests --- //

    @Test
    void rejectBlankOperatingState() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DomesticFlight(
                        "D102",
                        "SJC",
                        "LAX",
                        LocalDateTime.of(2026, 10, 20, 10, 0),
                        LocalDateTime.of(2026, 10, 20, 12, 0),
                        100,
                        "",
                        0.10,
                        true
                )
        );

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectNegativeDomesticTaxRate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new DomesticFlight(
                        "D102",
                        "SJC",
                        "LAX",
                        LocalDateTime.of(2026, 10, 20, 10, 0),
                        LocalDateTime.of(2026, 10, 20, 12, 0),
                        100,
                        "CA",
                        -0.10,
                        true
                )
        );

        System.out.println(exception.getMessage());
    }


    // --- Travel Document Tests --- //

    @Test
    void validateDomesticTravelDocuments() {
        Passenger passenger = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "",
                false
        );

        assertDoesNotThrow(() -> flight.validateTravelDocuments(passenger));
    }


    // --- Fare and Tax Tests --- //

    @Test
    void calculateDomesticTax() {
        assertEquals(10.0, flight.calculateDomesticTax(100.0));
    }

    @Test
    void applyResidentDiscount() {
        assertEquals(90.0, flight.applyResidentDiscount(100.0));
    }

    @Test
    void noResidentDiscount() {
        DomesticFlight noDiscountFlight = new DomesticFlight(
                "D102",
                "SJC",
                "LAX",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 20, 12, 0),
                100,
                "CA",
                0.10,
                false
        );

        assertEquals(100.0, noDiscountFlight.applyResidentDiscount(100.0));
    }

    @Test
    void acceptZeroFare() {
        assertEquals(0.0, flight.applyResidentDiscount(0.0));
    }

    @Test
    void rejectNegativeFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.applyResidentDiscount(-100.0));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectNaNFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.applyResidentDiscount(Double.NaN));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectInfiniteFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.applyResidentDiscount(Double.POSITIVE_INFINITY));

        System.out.println(exception.getMessage());
    }

    @Test
    void calculateTravelFee() {
        assertEquals(99.0, flight.calculateTravelFee(100.0));
    }


    // --- Boarding Tests --- //

    @Test
    void determineBoardingGroup() {
        assertEquals(3, flight.determineBoardingGroup(1));
        assertEquals(3, flight.determineBoardingGroup(10));

        assertEquals(2, flight.determineBoardingGroup(11));
        assertEquals(2, flight.determineBoardingGroup(20));

        assertEquals(1, flight.determineBoardingGroup(21));
    }

    @Test
    void rejectInvalidSeatRow() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.determineBoardingGroup(0));

        System.out.println(exception.getMessage());
    }


// --- Arrival Requirement Tests --- //

    @Test
    void requiredArrivalMinutes() {
        assertEquals(120, flight.getRequiredArrivalMinutes());
    }
}