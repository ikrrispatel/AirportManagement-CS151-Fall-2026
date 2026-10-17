package edu.sjsu.airport.ui;

import edu.sjsu.airport.enums.FlightStatus;
import edu.sjsu.airport.model.DomesticFlight;
import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlightPassengerConsoleTest {

    @Test
    void registersAndListsPassenger() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        String output = runConsole(
                system,
                "1\nP901\nTest Passenger\nCA-ID-901\n\nno\n"
                        + "4\n0\n");

        assertEquals(1, system.getPassengerCount());
        assertTrue(output.contains(
                "P901 was registered successfully."));
        assertTrue(output.contains("Test Passenger"));
    }

    @Test
    void registersAndListsDomesticFlight() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        String output = runConsole(
                system,
                "2\nSJ901\nSFO\nLAX\n"
                        + "2026-10-20T10:00\n"
                        + "2026-10-20T12:00\n"
                        + "100\nCalifornia\n0.08\nyes\n"
                        + "5\n0\n");

        assertEquals(1, system.getFlightCount());
        assertTrue(output.contains(
                "SJ901 was registered successfully."));
        assertTrue(output.contains("SFO -> LAX"));
    }

    @Test
    void registersInternationalFlight() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        String output = runConsole(
                system,
                "3\nSJ902\nSFO\nLHR\n"
                        + "2026-10-20T10:00\n"
                        + "2026-10-20T20:00\n"
                        + "200\nUnited Kingdom\n0.20\nyes\n"
                        + "0\n");

        assertEquals(1, system.getFlightCount());
        assertTrue(system.findFlight("SJ902")
                .orElseThrow()
                .isInternational());
        assertTrue(output.contains(
                "SJ902 was registered successfully."));
    }

    @Test
    void performsFlightStatusOperations() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);
        DomesticFlight flight = new DomesticFlight(
                "SJ903",
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                100,
                "California",
                0.08,
                true);
        system.addFlight(flight);

        String output = runConsole(
                system,
                "6\nSJ903\n45\n"
                        + "7\nSJ903\nBOARDING\n"
                        + "8\nSJ903\n"
                        + "0\n");

        assertEquals(FlightStatus.CANCELED, flight.getStatus());
        assertTrue(output.contains("delayed by 45 minutes"));
        assertTrue(output.contains("SJ903 was canceled"));
    }

    private String runConsole(
            AirportManagementSystem system,
            String commands) {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try (ConsoleInput input = new ConsoleInput(
                new ByteArrayInputStream(
                        commands.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(
                        output,
                        true,
                        StandardCharsets.UTF_8))) {

            new FlightPassengerConsole(
                    system,
                    input,
                    new PrintStream(
                            output,
                            true,
                            StandardCharsets.UTF_8))
                    .run();
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
