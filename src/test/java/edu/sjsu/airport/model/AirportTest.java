package edu.sjsu.airport.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportTest {

    @Test
    void constructorNormalizesAirportCode() {
        Airport airport = new Airport(
                "sfo",
                "San Francisco International Airport",
                "San Francisco",
                "United States");

        assertEquals("SFO", airport.getAirportCode());
        assertTrue(airport.isOperational());
        assertEquals(0, airport.calculateDailyTraffic());
    }

    @Test
    void constructorRejectsInvalidAirportCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Airport(
                        "SF",
                        "Invalid Airport",
                        "San Francisco",
                        "United States"));
    }

    @Test
    void constructorRejectsBlankLocationInformation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Airport(
                        "SFO",
                        "San Francisco International Airport",
                        " ",
                        "United States"));
    }

    @Test
    void recordsArrivalsAndDepartures() {
        Airport airport = createAirport();

        airport.recordArrival();
        airport.recordArrival();
        airport.recordDeparture();

        assertEquals(2, airport.getDailyArrivals());
        assertEquals(1, airport.getDailyDepartures());
        assertEquals(3, airport.calculateDailyTraffic());
    }

    @Test
    void disruptionSuspendsOperationsUntilResolved() {
        Airport airport = createAirport();

        airport.reportDisruption("Runway inspection");

        assertFalse(airport.isOperational());
        assertEquals(
                "Runway inspection",
                airport.getDisruptionMessage());

        assertThrows(
                IllegalStateException.class,
                airport::recordArrival);

        airport.resolveDisruption();

        assertTrue(airport.isOperational());
        assertEquals("", airport.getDisruptionMessage());
    }

    @Test
    void resetDailyTrafficClearsBothCounters() {
        Airport airport = createAirport();
        airport.recordArrival();
        airport.recordDeparture();

        airport.resetDailyTraffic();

        assertEquals(0, airport.getDailyArrivals());
        assertEquals(0, airport.getDailyDepartures());
        assertEquals(0, airport.calculateDailyTraffic());
    }

    @Test
    void airportsWithSameCodeAreEqual() {
        Airport first = createAirport();
        Airport second = new Airport(
                "sfo",
                "Different Name",
                "Different City",
                "Different Country");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    private Airport createAirport() {
        return new Airport(
                "SFO",
                "San Francisco International Airport",
                "San Francisco",
                "United States");
    }
}
