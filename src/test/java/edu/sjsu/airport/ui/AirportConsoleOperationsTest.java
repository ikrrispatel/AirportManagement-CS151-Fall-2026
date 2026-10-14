package edu.sjsu.airport.ui;

import edu.sjsu.airport.model.Airport;
import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportConsoleOperationsTest {

    @Test
    void recordsArrivalAndDeparture() {
        TestSession session = runApplication(
                registerSfoInput()
                        + "5\nSFO\n"
                        + "6\nSFO\n"
                        + "0\n",
                new AirportManagementSystem());

        Airport airport = session.system()
                .findAirport("SFO")
                .orElseThrow();

        assertEquals(1, airport.getDailyArrivals());
        assertEquals(1, airport.getDailyDepartures());
        assertEquals(2, airport.calculateDailyTraffic());
        assertTrue(session.output().contains(
                "Arrival recorded for SFO. Daily traffic: 1"));
        assertTrue(session.output().contains(
                "Departure recorded for SFO. Daily traffic: 2"));
    }

    @Test
    void removesRegisteredAirport() {
        TestSession session = runApplication(
                registerSfoInput()
                        + "4\nSFO\n"
                        + "0\n",
                new AirportManagementSystem());

        assertEquals(0, session.system().getAirportCount());
        assertTrue(session.output().contains(
                "SFO was removed successfully."));
    }

    @Test
    void reportsMissingAirportDuringRemoval() {
        TestSession session = runApplication(
                "4\nLAX\n0\n",
                new AirportManagementSystem());

        assertEquals(0, session.system().getAirportCount());
        assertTrue(session.output().contains(
                "Unable to remove airport:"));
        assertTrue(session.output().contains(
                "No airport found with code LAX."));
    }

    @Test
    void reportsMissingAirportWhenRecordingTraffic() {
        TestSession session = runApplication(
                "5\nLAX\n0\n",
                new AirportManagementSystem());

        assertTrue(session.output().contains(
                "Unable to record traffic:"));
        assertTrue(session.output().contains(
                "No airport found with code LAX."));
    }

    @Test
    void preventsTrafficDuringAirportDisruption() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Airport airport = createAirport();
        airport.reportDisruption("Runway inspection");
        system.addAirport(airport);

        TestSession session = runApplication(
                "5\nSFO\n0\n",
                system);

        assertEquals(0, airport.calculateDailyTraffic());
        assertTrue(session.output().contains(
                "Unable to record traffic:"));
        assertTrue(session.output().contains(
                "Airport operations are suspended"));
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

    private String registerSfoInput() {
        return "1\n"
                + "SFO\n"
                + "San Francisco International Airport\n"
                + "San Francisco\n"
                + "United States\n";
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
