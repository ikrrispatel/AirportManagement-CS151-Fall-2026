package edu.sjsu.airport.ui;

import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportConsoleApplicationTest {

    @Test
    void exitsNormallyFromMainMenu() {
        TestSession session = runApplication("0\n");

        assertTrue(session.output().contains(
                "Airport system closed safely."));
        assertEquals(0, session.system().getAirportCount());
    }

    @Test
    void displaysEmptySystemSummary() {
        TestSession session = runApplication("7\n0\n");

        assertTrue(session.output().contains(
                "Registered airports: 0/100"));
        assertTrue(session.output().contains(
                "Daily flight movements: 0"));
        assertTrue(session.output().contains(
                "Accepting airport registrations: Yes"));
    }

    @Test
    void registersAirportFromConsoleInput() {
        TestSession session = runApplication(
                "1\n"
                        + "sfo\n"
                        + "San Francisco International Airport\n"
                        + "San Francisco\n"
                        + "United States\n"
                        + "0\n");

        assertEquals(1, session.system().getAirportCount());
        assertTrue(session.system()
                .findAirport("SFO")
                .isPresent());
        assertTrue(session.output().contains(
                "SFO was registered successfully."));
    }

    @Test
    void listsRegisteredAirport() {
        TestSession session = runApplication(
                registerSfoInput()
                        + "2\n"
                        + "0\n");

        assertTrue(session.output().contains(
                "SFO - San Francisco International Airport"));
        assertTrue(session.output().contains(
                "San Francisco, United States"));
    }

    @Test
    void findsRegisteredAirportIgnoringCodeCase() {
        TestSession session = runApplication(
                registerSfoInput()
                        + "3\n"
                        + "sFo\n"
                        + "0\n");

        assertTrue(session.output().contains(
                "SFO - San Francisco International Airport"));
    }

    @Test
    void reportsAirportThatDoesNotExist() {
        TestSession session = runApplication(
                "3\n"
                        + "LAX\n"
                        + "0\n");

        assertTrue(session.output().contains(
                "No airport was found with code LAX."));
    }

    @Test
    void rejectsDuplicateAirportRegistration() {
        TestSession session = runApplication(
                registerSfoInput()
                        + registerSfoInput()
                        + "0\n");

        assertEquals(1, session.system().getAirportCount());
        assertTrue(session.output().contains(
                "Unable to register airport:"));
    }

    @Test
    void rejectsInvalidAirportCodeWithoutStoppingApplication() {
        TestSession session = runApplication(
                "1\n"
                        + "SF\n"
                        + "Invalid Airport\n"
                        + "San Francisco\n"
                        + "United States\n"
                        + "7\n"
                        + "0\n");

        assertEquals(0, session.system().getAirportCount());
        assertTrue(session.output().contains(
                "Airport code must contain exactly three letters."));
        assertTrue(session.output().contains(
                "Registered airports: 0/100"));
    }

    @Test
    void relocatesRegisteredAirportFromConsoleInput() {
        TestSession session = runApplication(
                registerSfoInput()
                        + "14\n"
                        + "SFO\n"
                        + "San Jose\n"
                        + "United States\n"
                        + "0\n");

        var airport = session.system()
                .findAirport("SFO")
                .orElseThrow();

        assertEquals("San Jose", airport.getCity());
        assertEquals("United States", airport.getCountry());
        assertTrue(session.output().contains(
                "SFO was relocated to San Jose, United States."));
    }

    private TestSession runApplication(String text) {
        AirportManagementSystem system =
                new AirportManagementSystem();
        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try (ConsoleInput input = new ConsoleInput(
                new ByteArrayInputStream(
                        text.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(
                        output,
                        true,
                        StandardCharsets.UTF_8))) {

            AirportConsoleApplication application =
                    new AirportConsoleApplication(
                            system,
                            input,
                            new PrintStream(
                                    output,
                                    true,
                                    StandardCharsets.UTF_8));

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

    private record TestSession(
            AirportManagementSystem system,
            String output) {
    }
}
