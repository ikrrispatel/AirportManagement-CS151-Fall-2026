package edu.sjsu.airport.ui;

import edu.sjsu.airport.interfaces.Trackable;
import edu.sjsu.airport.model.Baggage;
import edu.sjsu.airport.enums.BaggageStatus;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Gate;
import edu.sjsu.airport.model.Passenger;
import edu.sjsu.airport.service.AirportManagementSystem;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GateBaggageConsole {
    private final AirportManagementSystem system;
    private final ConsoleInput input;
    private final PrintStream output;

    public GateBaggageConsole(
            AirportManagementSystem system,
            ConsoleInput input,
            PrintStream output) {

        this.system = Objects.requireNonNull(system);
        this.input = Objects.requireNonNull(input);
        this.output = Objects.requireNonNull(output);
    }

    public void run() {
        boolean running = true;

        while (running) {
            displayMenu();
            int choice = input.readInt(
                    "Select an option: ", 0, 13);

            switch (choice) {
                case 1 -> registerGate();
                case 2 -> listGates();
                case 3 -> assignFlightToGate();
                case 4 -> releaseGate();
                case 5 -> markGateUnavailable();
                case 6 -> restoreGate();
                case 7 -> registerBaggage();
                case 8 -> listBaggage();
                case 9 -> checkInBaggage();
                case 10 -> updateBaggageStatus();
                case 11 -> transferBaggage();
                case 12 -> markBaggageMisrouted();
                case 13 -> displayTrackingSummaries();
                case 0 -> running = false;
                default -> throw new IllegalStateException(
                        "Unexpected menu choice: " + choice);
            }
        }
    }

    private void registerGate() {
        output.println();
        output.println("Register Gate");

        try {
            Gate gate = new Gate(
                    input.readRequiredLine("Gate number: "),
                    input.readRequiredLine("Terminal: "),
                    input.readInt(
                            "Maximum passenger capacity: ",
                            1,
                            10000),
                    input.readYesNo(
                            "International capable? (yes/no): "),
                    true);

            system.addGate(gate);
            output.println(
                    gate.getGateNumber()
                            + " was registered successfully.");
        } catch (RuntimeException exception) {
            printFailure("register gate", exception);
        }
    }

    private void listGates() {
        output.println();
        output.println("Registered Gates");

        if (system.getGates().isEmpty()) {
            output.println("No gates are registered.");
            return;
        }

        system.getGates().forEach(output::println);
    }

    private void assignFlightToGate() {
        output.println();
        output.println("Assign Flight to Gate");

        try {
            Gate gate = requireGate();
            Flight flight = requireFlight();
            gate.assignFlight(flight);

            output.println(
                    flight.getFlightNumber()
                            + " assigned to "
                            + gate.getGateNumber()
                            + ".");
        } catch (RuntimeException exception) {
            printFailure("assign flight", exception);
        }
    }

    private void releaseGate() {
        output.println();
        output.println("Release Gate");

        try {
            Gate gate = requireGate();
            gate.releaseFlight();

            output.println(
                    gate.getGateNumber() + " was released.");
        } catch (RuntimeException exception) {
            printFailure("release gate", exception);
        }
    }

    private void markGateUnavailable() {
        output.println();
        output.println("Mark Gate Temporarily Unavailable");

        try {
            Gate gate = requireGate();
            LocalDateTime until = readDateTime(
                    "Unavailable until (YYYY-MM-DDTHH:MM): ");
            gate.markTemporarilyUnavailable(until);

            output.println(
                    gate.getGateNumber()
                            + " unavailable until "
                            + until
                            + ".");
        } catch (RuntimeException exception) {
            printFailure("mark gate unavailable", exception);
        }
    }

    private void restoreGate() {
        output.println();
        output.println("Restore Gate");

        try {
            Gate gate = requireGate();
            gate.restoreAvailability();

            output.println(
                    gate.getGateNumber()
                            + " availability restored.");
        } catch (RuntimeException exception) {
            printFailure("restore gate", exception);
        }
    }

    private void registerBaggage() {
        output.println();
        output.println("Register Baggage");

        try {
            Passenger passenger = requirePassenger();
            Baggage baggage = new Baggage(
                    input.readRequiredLine("Tag number: "),
                    passenger,
                    BaggageStatus.CREATED,
                    input.readRequiredLine("Current location: "),
                    input.readDouble("Weight: ", 0.01));

            passenger.addBaggage(baggage);
            system.addBaggage(baggage);

            output.println(
                    baggage.getTagNumber()
                            + " was registered successfully.");
        } catch (RuntimeException exception) {
            printFailure("register baggage", exception);
        }
    }

    private void listBaggage() {
        output.println();
        output.println("Registered Baggage");

        if (system.getBaggageItems().isEmpty()) {
            output.println("No baggage is registered.");
            return;
        }

        system.getBaggageItems().forEach(output::println);
    }

    private void checkInBaggage() {
        output.println();
        output.println("Check In Baggage");

        try {
            Baggage baggage = requireBaggage();
            Flight flight = requireFlight();
            baggage.checkInBaggage(flight);

            output.println(
                    baggage.getTagNumber()
                            + " checked in for "
                            + flight.getFlightNumber()
                            + ".");
        } catch (RuntimeException exception) {
            printFailure("check in baggage", exception);
        }
    }

    private void updateBaggageStatus() {
        output.println();
        output.println("Update Baggage Status");

        try {
            Baggage baggage = requireBaggage();
            String status = input.readRequiredLine(
                    "New status: ");
            baggage.updateStatus(status);

            output.println(baggage.getTrackingSummary());
        } catch (RuntimeException exception) {
            printFailure("update baggage", exception);
        }
    }

    private void transferBaggage() {
        output.println();
        output.println("Transfer Baggage");

        try {
            Baggage baggage = requireBaggage();
            Flight flight = requireFlight();
            baggage.transferToFlight(flight);

            output.println(
                    baggage.getTagNumber()
                            + " transferred to "
                            + flight.getFlightNumber()
                            + ".");
        } catch (RuntimeException exception) {
            printFailure("transfer baggage", exception);
        }
    }

    private void markBaggageMisrouted() {
        output.println();
        output.println("Mark Baggage Misrouted");

        try {
            Baggage baggage = requireBaggage();
            String location = input.readRequiredLine(
                    "Current location: ");
            baggage.markMisrouted(location);

            output.println(baggage.getTrackingSummary());
        } catch (RuntimeException exception) {
            printFailure("mark baggage misrouted", exception);
        }
    }

    private void displayTrackingSummaries() {
        output.println();
        output.println("Trackable Operations");

        List<Trackable> trackableObjects =
                new ArrayList<>();
        trackableObjects.addAll(system.getFlights());
        trackableObjects.addAll(system.getBaggageItems());

        if (trackableObjects.isEmpty()) {
            output.println("No trackable objects are registered.");
            return;
        }

        trackableObjects.stream()
                .map(Trackable::getTrackingSummary)
                .forEach(output::println);
    }

    private Gate requireGate() {
        String number = input.readRequiredLine(
                "Gate number: ");

        return system.findGate(number)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No gate found with number "
                                        + number + "."));
    }

    private Flight requireFlight() {
        String number = input.readRequiredLine(
                "Flight number: ");

        return system.findFlight(number)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No flight found with number "
                                        + number + "."));
    }

    private Passenger requirePassenger() {
        String id = input.readRequiredLine(
                "Passenger ID: ");

        return system.findPassenger(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No passenger found with ID "
                                        + id + "."));
    }

    private Baggage requireBaggage() {
        String tag = input.readRequiredLine(
                "Tag number: ");

        return system.findBaggage(tag)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No baggage found with tag "
                                        + tag + "."));
    }

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            String value = input.readRequiredLine(prompt);

            try {
                return LocalDateTime.parse(value);
            } catch (DateTimeParseException exception) {
                output.println(
                        "Enter a date and time like "
                                + "2026-10-20T14:30.");
            }
        }
    }

    private void printFailure(
            String operation,
            RuntimeException exception) {

        output.println(
                "Unable to "
                        + operation
                        + ": "
                        + exception.getMessage());
    }

    private void displayMenu() {
        output.println();
        output.println("Gate and Baggage Menu");
        output.println("1. Register a gate");
        output.println("2. List gates");
        output.println("3. Assign a flight to a gate");
        output.println("4. Release a gate");
        output.println("5. Mark a gate temporarily unavailable");
        output.println("6. Restore a gate");
        output.println("7. Register baggage");
        output.println("8. List baggage");
        output.println("9. Check in baggage");
        output.println("10. Update baggage status");
        output.println("11. Transfer baggage");
        output.println("12. Mark baggage misrouted");
        output.println("13. View all tracking summaries");
        output.println("0. Return to main menu");
    }
}
