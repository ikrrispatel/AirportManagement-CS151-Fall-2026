package edu.sjsu.airport.ui;

import edu.sjsu.airport.exception.ExitRequestedException;
import edu.sjsu.airport.model.Airport;
import edu.sjsu.airport.service.AirportManagementSystem;

import java.io.PrintStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Runs the console interface for the airport management system.
 */
public final class AirportConsoleApplication {
    private final AirportManagementSystem system;
    private final ConsoleInput input;
    private final PrintStream output;

    public AirportConsoleApplication(
            AirportManagementSystem system,
            ConsoleInput input,
            PrintStream output) {

        this.system = Objects.requireNonNull(
                system,
                "Airport management system cannot be null.");
        this.input = Objects.requireNonNull(
                input,
                "Console input cannot be null.");
        this.output = Objects.requireNonNull(
                output,
                "Output cannot be null.");
    }

    public static void main(String[] args) {
        PrintStream output = System.out;

        try (ConsoleInput input =
                     new ConsoleInput(System.in, output)) {

            AirportConsoleApplication application =
                    new AirportConsoleApplication(
                            new AirportManagementSystem(),
                            input,
                            output);

            application.run();
        } catch (ExitRequestedException exception) {
            output.println();
            output.println("Exit requested. Airport system closed safely.");
        } catch (NoSuchElementException exception) {
            output.println();
            output.println("Input closed. Airport system stopped.");
        }
    }

    /**
     * Displays the main menu until the user chooses to exit.
     */
    public void run() {
        boolean running = true;

        displayWelcomeMessage();

        while (running) {
            displayMainMenu();

            int choice = input.readInt(
                    "Select an option: ",
                    0,
                    4);

            running = handleMainChoice(choice);
        }

        output.println("Airport system closed safely.");
    }

    private boolean handleMainChoice(int choice) {
        switch (choice) {
            case 1 -> registerAirport();
            case 2 -> listAirports();
            case 3 -> findAirport();
            case 4 -> displaySystemSummary();
            case 0 -> {
                return false;
            }
            default -> throw new IllegalStateException(
                    "Unexpected menu choice: " + choice);
        }

        return true;
    }

    private void registerAirport() {
        output.println();
        output.println("Register Airport");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");
        String name = input.readRequiredLine(
                "Airport name: ");
        String city = input.readRequiredLine(
                "City: ");
        String country = input.readRequiredLine(
                "Country: ");

        try {
            Airport airport = new Airport(
                    code,
                    name,
                    city,
                    country);

            system.addAirport(airport);

            output.printf(
                    "%s was registered successfully.%n",
                    airport.getAirportCode());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to register airport: "
                            + exception.getMessage());
        }
    }

    private void listAirports() {
        output.println();
        output.println("Registered Airports");

        List<Airport> airports = system.getAirports();

        if (airports.isEmpty()) {
            output.println("No airports are currently registered.");
            return;
        }

        for (int index = 0; index < airports.size(); index++) {
            output.printf("%d. ", index + 1);
            displayAirport(airports.get(index));
        }
    }

    private void findAirport() {
        output.println();
        output.println("Find Airport");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");

        try {
            system.findAirport(code)
                    .ifPresentOrElse(
                            this::displayAirport,
                            () -> output.println(
                                    "No airport was found with code "
                                            + code.toUpperCase() + "."));
        } catch (IllegalArgumentException exception) {
            output.println(
                    "Unable to search: "
                            + exception.getMessage());
        }
    }

    private void displayAirport(Airport airport) {
        output.printf(
                "%s - %s (%s, %s) | %s | Daily traffic: %d%n",
                airport.getAirportCode(),
                airport.getName(),
                airport.getCity(),
                airport.getCountry(),
                airport.isOperational()
                        ? "Operational"
                        : "Operations suspended",
                airport.calculateDailyTraffic());
    }

    private void displayWelcomeMessage() {
        output.println("======================================");
        output.println(" Airport Operations Management System ");
        output.println("======================================");
        output.println("Type EXIT at any prompt to leave safely.");
    }

    private void displayMainMenu() {
        output.println();
        output.println("Main Menu");
        output.println("1. Register an airport");
        output.println("2. List registered airports");
        output.println("3. Find an airport");
        output.println("4. View system summary");
        output.println("0. Exit");
    }

    private void displaySystemSummary() {
        output.println();
        output.println("System Summary");
        output.printf(
                "Registered airports: %d/%d%n",
                system.getAirportCount(),
                AirportManagementSystem.MAXIMUM_INSTANCES);
        output.printf(
                "Daily flight movements: %d%n",
                system.calculateTotalDailyTraffic());
        output.printf(
                "Accepting airport registrations: %s%n",
                system.hasCapacity() ? "Yes" : "No");
    }
}
