package edu.sjsu.airport.ui;

import edu.sjsu.airport.model.DomesticFlight;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.InternationalFlight;
import edu.sjsu.airport.model.Passenger;
import edu.sjsu.airport.service.AirportManagementSystem;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public final class FlightPassengerConsole {
    private final AirportManagementSystem system;
    private final ConsoleInput input;
    private final PrintStream output;

    public FlightPassengerConsole(
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
                    "Select an option: ", 0, 8);

            switch (choice) {
                case 1 -> registerPassenger();
                case 2 -> registerDomesticFlight();
                case 3 -> registerInternationalFlight();
                case 4 -> listPassengers();
                case 5 -> listFlights();
                case 6 -> delayFlight();
                case 7 -> updateFlightStatus();
                case 8 -> cancelFlight();
                case 0 -> running = false;
                default -> throw new IllegalStateException(
                        "Unexpected menu choice: " + choice);
            }
        }
    }

    private void registerPassenger() {
        output.println();
        output.println("Register Passenger");

        try {
            Passenger passenger = new Passenger(
                    input.readRequiredLine("Passenger ID: "),
                    input.readRequiredLine("Full name: "),
                    input.readRequiredLine("Government ID: "),
                    input.readLine(
                            "Passport number (blank if unavailable): "),
                    input.readYesNo("Visa approved? (yes/no): "));

            system.addPassenger(passenger);
            output.println(
                    passenger.getPassengerId()
                            + " was registered successfully.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to register passenger: "
                            + exception.getMessage());
        }
    }

    private void registerDomesticFlight() {
        output.println();
        output.println("Register Domestic Flight");

        try {
            DomesticFlight flight = new DomesticFlight(
                    input.readRequiredLine("Flight number: "),
                    input.readRequiredLine("Origin: "),
                    input.readRequiredLine("Destination: "),
                    readDateTime("Departure (YYYY-MM-DDTHH:MM): "),
                    readDateTime("Arrival (YYYY-MM-DDTHH:MM): "),
                    input.readInt("Passenger capacity: ", 1, 10000),
                    input.readRequiredLine("Operating state: "),
                    input.readDouble("Domestic tax rate: ", 0.0),
                    input.readYesNo(
                            "Resident discount available? (yes/no): "));

            system.addFlight(flight);
            output.println(
                    flight.getFlightNumber()
                            + " was registered successfully.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to register flight: "
                            + exception.getMessage());
        }
    }

    private void registerInternationalFlight() {
        output.println();
        output.println("Register International Flight");

        try {
            InternationalFlight flight =
                    new InternationalFlight(
                            input.readRequiredLine("Flight number: "),
                            input.readRequiredLine("Origin: "),
                            input.readRequiredLine("Destination: "),
                            readDateTime(
                                    "Departure (YYYY-MM-DDTHH:MM): "),
                            readDateTime(
                                    "Arrival (YYYY-MM-DDTHH:MM): "),
                            input.readInt(
                                    "Passenger capacity: ",
                                    1,
                                    10000),
                            input.readRequiredLine(
                                    "Destination country: "),
                            input.readDouble(
                                    "International tax rate: ",
                                    0.0),
                            input.readYesNo(
                                    "Visa required? (yes/no): "));

            system.addFlight(flight);
            output.println(
                    flight.getFlightNumber()
                            + " was registered successfully.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to register flight: "
                            + exception.getMessage());
        }
    }

    private void listPassengers() {
        output.println();
        output.println("Registered Passengers");

        if (system.getPassengers().isEmpty()) {
            output.println("No passengers are registered.");
            return;
        }

        system.getPassengers().forEach(output::println);
    }

    private void listFlights() {
        output.println();
        output.println("Registered Flights");

        if (system.getFlights().isEmpty()) {
            output.println("No flights are registered.");
            return;
        }

        system.getFlights().stream()
                .map(Flight::getTrackingSummary)
                .forEach(output::println);
    }

    private void delayFlight() {
        output.println();
        output.println("Delay Flight");

        try {
            Flight flight = requireFlight();
            int minutes = input.readInt(
                    "Delay minutes: ", 1, 10080);
            flight.delayFlight(minutes);

            output.println(
                    flight.getFlightNumber()
                            + " delayed by "
                            + minutes
                            + " minutes.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to delay flight: "
                            + exception.getMessage());
        }
    }

    private void updateFlightStatus() {
        output.println();
        output.println("Update Flight Status");

        try {
            Flight flight = requireFlight();
            String status = input.readRequiredLine(
                    "New status: ");
            flight.updateStatus(status);

            output.println(flight.getTrackingSummary());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to update flight: "
                            + exception.getMessage());
        }
    }

    private void cancelFlight() {
        output.println();
        output.println("Cancel Flight");

        try {
            Flight flight = requireFlight();
            flight.cancelFlight();

            output.println(
                    flight.getFlightNumber()
                            + " was canceled.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to cancel flight: "
                            + exception.getMessage());
        }
    }

    private Flight requireFlight() {
        String flightNumber = input.readRequiredLine(
                "Flight number: ");

        return system.findFlight(flightNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No flight found with number "
                                        + flightNumber + "."));
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

    private void displayMenu() {
        output.println();
        output.println("Flight and Passenger Menu");
        output.println("1. Register a passenger");
        output.println("2. Register a domestic flight");
        output.println("3. Register an international flight");
        output.println("4. List passengers");
        output.println("5. List flights");
        output.println("6. Delay a flight");
        output.println("7. Update a flight status");
        output.println("8. Cancel a flight");
        output.println("0. Return to main menu");
    }
}
