package edu.sjsu.airport.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportEdgeCaseTest {

    @Test
    void relocationTrimsValidatedLocation() {
        Airport airport = createAirport();

        airport.relocate("  Oakland  ", "  United States  ");

        assertEquals("Oakland", airport.getCity());
        assertEquals("United States", airport.getCountry());
    }

    @Test
    void failedRelocationDoesNotPartiallyChangeLocation() {
        Airport airport = createAirport();

        assertThrows(
                IllegalArgumentException.class,
                () -> airport.relocate("Oakland", " "));

        assertEquals("San Francisco", airport.getCity());
        assertEquals("United States", airport.getCountry());
    }

    @Test
    void blankDisruptionReasonDoesNotSuspendOperations() {
        Airport airport = createAirport();

        assertThrows(
                IllegalArgumentException.class,
                () -> airport.reportDisruption(" "));

        assertTrue(airport.isOperational());
        assertEquals("", airport.getDisruptionMessage());
    }

    @Test
    void disruptionBlocksBothTrafficOperations() {
        Airport airport = createAirport();
        airport.reportDisruption("Runway inspection");

        assertThrows(
                IllegalStateException.class,
                airport::recordArrival);
        assertThrows(
                IllegalStateException.class,
                airport::recordDeparture);

        assertEquals(0, airport.calculateDailyTraffic());
    }

    @Test
    void resolvingWithoutDisruptionIsRejected() {
        Airport airport = createAirport();

        assertThrows(
                IllegalStateException.class,
                airport::resolveDisruption);

        assertTrue(airport.isOperational());
    }

    @Test
    void newerDisruptionReasonReplacesPreviousReason() {
        Airport airport = createAirport();
        airport.reportDisruption("Weather");

        airport.reportDisruption("Runway inspection");

        assertEquals(
                "Runway inspection",
                airport.getDisruptionMessage());
    }

    private Airport createAirport() {
        return new Airport(
                "SFO",
                "San Francisco International Airport",
                "San Francisco",
                "United States");
    }
}
