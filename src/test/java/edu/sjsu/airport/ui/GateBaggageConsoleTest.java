package edu.sjsu.airport.ui;

import edu.sjsu.airport.model.Baggage;
import edu.sjsu.airport.enums.BaggageStatus;
import edu.sjsu.airport.model.DomesticFlight;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Gate;
import edu.sjsu.airport.model.Passenger;
import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GateBaggageConsoleTest {

    @Test
    void registersAndListsGate() {
        AirportManagementSystem system =
                new AirportManagementSystem();

        String output = runConsole(
                system,
                "1\nA1\nTerminal A\n150\nyes\n"
                        + "2\n0\n");

        assertEquals(1, system.getGateCount());
        assertTrue(output.contains(
                "A1 was registered successfully."));
        assertTrue(output.contains("Terminal A"));
    }

    @Test
    void assignsAndReleasesFlightAtGate() {
        AirportManagementSystem system =
                createSystem();
        Gate gate = new Gate(
                "A2", "Terminal A", 150, true, true);
        system.addGate(gate);

        String output = runConsole(
                system,
                "3\nA2\nSJ1101\n"
                        + "4\nA2\n"
                        + "0\n");

        assertNull(gate.getAssignedFlight());
        assertNull(system.findFlight("SJ1101")
                .orElseThrow()
                .getAssignedGate());
        assertTrue(output.contains(
                "SJ1101 assigned to A2."));
        assertTrue(output.contains("A2 was released."));
    }

    @Test
    void registersAndProcessesBaggageLifecycle() {
        AirportManagementSystem system =
                createSystem();

        runConsole(
                system,
                "7\nP1101\nTAG1101\nCheck-in Counter\n20\n"
                        + "9\nTAG1101\nSJ1101\n"
                        + "10\nTAG1101\nLOADED\n"
                        + "10\nTAG1101\nIN_TRANSIT\n"
                        + "10\nTAG1101\nREADY_FOR_CLAIM\n"
                        + "10\nTAG1101\nCLAIMED\n"
                        + "0\n");

        Baggage baggage =
                system.findBaggage("TAG1101").orElseThrow();

        assertEquals(BaggageStatus.CLAIMED, baggage.getStatus());
        assertNull(baggage.getAssignedFlight());
        assertEquals(
                1,
                system.findPassenger("P1101")
                        .orElseThrow()
                        .getBaggageItems()
                        .size());
    }

    @Test
    void displaysFlightAndBaggagePolymorphically() {
        AirportManagementSystem system =
                createSystem();
        Passenger passenger =
                system.findPassenger("P1101").orElseThrow();
        Baggage baggage = new Baggage(
                "TAG1102",
                passenger,
                BaggageStatus.CREATED,
                "Counter",
                15.0);
        system.addBaggage(baggage);

        String output = runConsole(
                system,
                "13\n0\n");

        assertTrue(output.contains("Flight SJ1101"));
        assertTrue(output.contains("Baggage TAG1102"));
    }

    @Test
    void removesBaggageFromPassenger() {
        AirportManagementSystem system =
                createSystem();
        Passenger passenger =
                system.findPassenger("P1101").orElseThrow();
        Baggage baggage = new Baggage(
                "TAG1103",
                passenger,
                BaggageStatus.CREATED,
                "Counter",
                15.0);

        system.addBaggage(baggage);
        passenger.addBaggage(baggage);

        String output = runConsole(
                system,
                "14\nP1101\nTAG1103\n0\n");

        assertTrue(passenger.getBaggageItems().isEmpty());
        assertTrue(system.findBaggage("TAG1103").isPresent());
        assertTrue(output.contains("TAG1103"));
    }

    private AirportManagementSystem createSystem() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Passenger passenger = new Passenger(
                "P1101",
                "Console Passenger",
                "CA-ID-1101",
                "",
                false);

        system.addPassenger(passenger);
        system.addFlight(createFlight());
        return system;
    }

    private Flight createFlight() {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                "SJ1101",
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                100,
                "California",
                0.08,
                true);
    }

    private String runConsole(
            AirportManagementSystem system,
            String commands) {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(
                output,
                true,
                StandardCharsets.UTF_8);

        try (ConsoleInput input = new ConsoleInput(
                new ByteArrayInputStream(
                        commands.getBytes(StandardCharsets.UTF_8)),
                printStream)) {

            new GateBaggageConsole(
                    system,
                    input,
                    printStream).run();
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
