package edu.sjsu.airport.model;

import edu.sjsu.airport.exception.InvalidTravelDocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;


class InternationalFlightTest {

    private InternationalFlight flight;

    @BeforeEach
    void setUp() {
        flight = new InternationalFlight(
                "I101",
                "SFO",
                "NRT",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 21, 0, 0),
                200,
                "Japan",
                0.10,
                true
        );
    }


    // --- Constructor Validation Tests --- //

    @Test
    void rejectBlankDestinationCountry() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InternationalFlight(
                        "I102",
                        "SFO",
                        "NRT",
                        LocalDateTime.of(2026, 10, 20, 10, 0),
                        LocalDateTime.of(2026, 10, 21, 0, 0),
                        200,
                        "",
                        0.10,
                        true
                )
        );

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectNegativeInternationalTaxRate() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new InternationalFlight(
                        "I102",
                        "SFO",
                        "NRT",
                        LocalDateTime.of(2026, 10, 20, 10, 0),
                        LocalDateTime.of(2026, 10, 21, 0, 0),
                        200,
                        "Japan",
                        -0.10,
                        true
                )
        );

        System.out.println(exception.getMessage());
    }


    // --- Travel Document Tests --- //

    @Test
    void validateInternationalTravelDocuments() {
        Passenger passenger = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "PASS001",
                true
        );

        assertDoesNotThrow(() -> flight.validateTravelDocuments(passenger));
    }

    @Test
    void rejectMissingPassport() {
        Passenger passenger = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "",
                true
        );

        InvalidTravelDocumentException exception = assertThrows(InvalidTravelDocumentException.class, () -> flight.validateTravelDocuments(passenger));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectMissingRequiredVisa() {
        Passenger passenger = new Passenger(
                "P001",
                "Test Passenger",
                "ID001",
                "PASS001",
                false
        );

        InvalidTravelDocumentException exception = assertThrows(InvalidTravelDocumentException.class, () -> flight.validateTravelDocuments(passenger));

        System.out.println(exception.getMessage());
    }

    @Test
    void allowPassengerWhenVisaNotRequired() {
        InternationalFlight noVisaFlight = new InternationalFlight(
                "I102",
                "SFO",
                "NRT",
                LocalDateTime.of(2026, 10, 20, 10, 0),
                LocalDateTime.of(2026, 10, 21, 0, 0),
                200,
                "Japan",
                0.10,
                false
        );

        Passenger passenger = new Passenger(
                "P002",
                "Test Passenger",
                "ID002",
                "PASS002",
                false
        );

        assertDoesNotThrow(() -> noVisaFlight.validateTravelDocuments(passenger));
    }


    // --- Fare and Tax Tests --- //

    @Test
    void calculateInternationalTax() {
        assertEquals(10.0, flight.calculateInternationalTax(100.0));
    }

    @Test
    void calculateTravelFee() {
        assertEquals(110.0, flight.calculateTravelFee(100.0));
    }

    @Test
    void acceptZeroFare() {
        assertEquals(0.0, flight.calculateTravelFee(0.0));
    }

    @Test
    void rejectNegativeFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.calculateTravelFee(-100.0));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectNaNFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.calculateTravelFee(Double.NaN));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectInfiniteFare() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.calculateTravelFee(Double.POSITIVE_INFINITY));

        System.out.println(exception.getMessage());
    }


    // --- Customs Tests --- //

    @Test
    void addCustomsRequirement() {
        assertDoesNotThrow(() -> flight.addCustomsRequirement("Customs declaration"));
    }

    @Test
    void rejectBlankCustomsRequirement() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.addCustomsRequirement(""));

        System.out.println(exception.getMessage());
    }

    @Test
    void rejectDuplicateCustomsRequirement() {
        flight.addCustomsRequirement("Customs declaration");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> flight.addCustomsRequirement("Customs declaration"));

        System.out.println(exception.getMessage());
    }


    // --- Arrival Requirement Tests --- //

    @Test
    void requiredArrivalMinutes() {
        assertEquals(180, flight.getRequiredArrivalMinutes());
    }

    @Test
    void determineInternationalCheckInDeadline() {
        LocalDateTime expected = LocalDateTime.of(2026, 10, 20, 7, 0);

        assertEquals(expected, flight.determineInternationalCheckInDeadline());
    }
}