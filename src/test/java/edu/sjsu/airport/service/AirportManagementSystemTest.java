package edu.sjsu.airport.service;

import edu.sjsu.airport.exception.CapacityExceededException;
import edu.sjsu.airport.exception.DuplicateAirportException;
import edu.sjsu.airport.model.Airport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportManagementSystemTest {

    @Test
    void addsAndFindsAirportByNormalizedCode() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Airport airport = createAirport("SFO");

        system.addAirport(airport);

        assertEquals(1, system.getAirportCount());
        assertSame(
                airport,
                system.findAirport(" sfo ").orElseThrow());
    }

    @Test
    void rejectsDuplicateAirportCode() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        system.addAirport(createAirport("SFO"));

        assertThrows(
                DuplicateAirportException.class,
                () -> system.addAirport(
                        new Airport(
                                "sfo",
                                "Duplicate Airport",
                                "Another City",
                                "Another Country")));
    }

    @Test
    void removesExistingAirport() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Airport airport = createAirport("SFO");
        system.addAirport(airport);

        Airport removed = system.removeAirport("sfo");

        assertSame(airport, removed);
        assertEquals(0, system.getAirportCount());
        assertTrue(system.findAirport("SFO").isEmpty());
    }

    @Test
    void rejectsRemovalOfUnknownAirport() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        assertThrows(
                NoSuchElementException.class,
                () -> system.removeAirport("SFO"));
    }

    @Test
    void calculatesTrafficAcrossRegisteredAirports() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Airport first = createAirport("SFO");
        Airport second = createAirport("SJC");

        first.recordArrival();
        first.recordDeparture();
        second.recordArrival();

        system.addAirport(first);
        system.addAirport(second);

        assertEquals(3, system.calculateTotalDailyTraffic());
    }

    @Test
    void airportListCannotBeModifiedExternally() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        system.addAirport(createAirport("SFO"));

        List<Airport> airports = system.getAirports();

        assertThrows(
                UnsupportedOperationException.class,
                () -> airports.add(createAirport("SJC")));
        assertEquals(1, system.getAirportCount());
    }

    @Test
    void rejectsAirportsBeyondCapacityLimit() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        for (int index = 0;
             index < AirportManagementSystem.MAXIMUM_INSTANCES;
             index++) {
            system.addAirport(createAirport(
                    airportCodeFor(index)));
        }

        assertFalse(system.hasCapacity());
        assertEquals(
                AirportManagementSystem.MAXIMUM_INSTANCES,
                system.getAirportCount());

        assertThrows(
                CapacityExceededException.class,
                () -> system.addAirport(createAirport("ZZZ")));
    }

    private Airport createAirport(String airportCode) {
        return new Airport(
                airportCode,
                "Test Airport " + airportCode,
                "Test City",
                "United States");
    }

    private String airportCodeFor(int index) {
        char secondLetter = (char) ('A' + index / 26);
        char thirdLetter = (char) ('A' + index % 26);
        return "A" + secondLetter + thirdLetter;
    }
}
