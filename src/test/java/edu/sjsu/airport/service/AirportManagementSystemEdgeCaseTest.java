package edu.sjsu.airport.service;

import edu.sjsu.airport.model.Airport;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportManagementSystemEdgeCaseTest {

    @Test
    void rejectsNullAirportWithoutChangingRegistry() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        assertThrows(
                NullPointerException.class,
                () -> system.addAirport(null));

        assertEquals(0, system.getAirportCount());
    }

    @Test
    void rejectsBlankAirportCodeDuringLookup() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        assertThrows(
                IllegalArgumentException.class,
                () -> system.findAirport(" "));

        assertEquals(0, system.getAirportCount());
    }

    @Test
    void rejectsInvalidAirportCodeDuringRemoval() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        system.addAirport(createAirport("SFO"));

        assertThrows(
                IllegalArgumentException.class,
                () -> system.removeAirport("SF"));

        assertEquals(1, system.getAirportCount());
        assertTrue(system.findAirport("SFO").isPresent());
    }

    @Test
    void failedRemovalDoesNotChangeRegistry() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        system.addAirport(createAirport("SFO"));

        assertThrows(
                NoSuchElementException.class,
                () -> system.removeAirport("SJC"));

        assertEquals(1, system.getAirportCount());
        assertTrue(system.findAirport("SFO").isPresent());
    }

    @Test
    void returnedAirportListIsASnapshot() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        system.addAirport(createAirport("SFO"));
        List<Airport> snapshot = system.getAirports();

        system.addAirport(createAirport("SJC"));

        assertEquals(1, snapshot.size());
        assertEquals(2, system.getAirportCount());
    }

    private Airport createAirport(String code) {
        return new Airport(
                code,
                "Test Airport " + code,
                "Test City",
                "United States");
    }
}
