package edu.sjsu.airport.ui;

import edu.sjsu.airport.model.Airport;
import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportDisruptionConsoleTest {

    @Test
    void reportsAirportDisruption() {
        Airport airport = createAirport();
        AirportManagementSystem system =
                createSystemWith(airport);

        TestSession session = runApplication(
                "8\nSFO\nRunway inspection\n0\n",
                system);

        assertFalse(airport.isOperational());
        assertEquals(
                "Runway inspection",
                airport.getDisruptionMessage());
        assertTrue(session.output().contains(
                "SFO operations suspended: Runway inspection"));
    }

    @Test
    void resolvesActiveDisruption() {
        Airport airport = createAirport();
        airport.reportDisruption("Weather closure");

        TestSession session = runApplication(
                "9\nSFO\n0\n",
                createSystemWith(airport));

        assertTrue(airport.isOperational());
        assertEquals("", airport.getDisruptionMessage());
        assertTrue(session.output().contains(
                "SFO operations resumed."));
    }

    @Test
    void rejectsResolutionWithoutActiveDisruption() {
        Airport airport = createAirport();

        TestSession session = runApplication(
                "9\nSFO\n0\n",
                createSystemWith(airport));

        assertTrue(airport.isOperational());
        assertTrue(session.output().contains(
                "Unable to resolve disruption:"));
        assertTrue(session.output().contains(
                "The airport has no active disruption."));
    }

    @Test
    void resetsDailyAirportTraffic() {
        Airport airport = createAirport();
        airport.recordArrival();
        airport.recordArrival();
        airport.recordDeparture();

        TestSession session = runApplication(
                "10\nSFO\n0\n",
                createSystemWith(airport));

        assertEquals(0, airport.getDailyArrivals());
        assertEquals(0, airport.getDailyDepartures());
        assertEquals(0, airport.calculateDailyTraffic());
        assertTrue(session.output().contains(
                "SFO daily traffic was reset."));
    }

    @Test
    void reportsMissingAirportForDisruption() {
        TestSession session = runApplication(
                "8\nLAX\nRunway inspection\n0\n",
                new AirportManagementSystem());

        assertTrue(session.output().contains(
                "Unable to report disruption:"));
        assertTrue(session.output().contains(
                "No airport found with code LAX."));
    }

    @Test
    void reportsMissingAirportForTrafficReset() {
        TestSession session = runApplication(
                "10\nLAX\n0\n",
                new AirportManagementSystem());

        assertTrue(session.output().contains(
                "Unable to reset traffic:"));
        assertTrue(session.output().contains(
                "No airport found with code LAX."));
    }

    private TestSession runApplication(
            String text,
            AirportManagementSystem system) {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        PrintStream printStream = new PrintStream(
                output,
                true,
                StandardCharsets.UTF_8);

        try (ConsoleInput input = new ConsoleInput(
                new ByteArrayInputStream(
                        text.getBytes(StandardCharsets.UTF_8)),
                printStream)) {

            AirportConsoleApplication application =
                    new AirportConsoleApplication(
                            system,
                            input,
                            printStream);

            application.run();
        }

        return new TestSession(
                system,
                output.toString(StandardCharsets.UTF_8));
    }

    private AirportManagementSystem createSystemWith(
            Airport airport) {

        AirportManagementSystem system =
                new AirportManagementSystem();
        system.addAirport(airport);
        return system;
    }

    private Airport createAirport() {
        return new Airport(
                "SFO",
                "San Francisco International Airport",
                "San Francisco",
                "United States");
    }

    private record TestSession(
            AirportManagementSystem system,
            String output) {
    }
}
