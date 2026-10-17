package edu.sjsu.airport.ui;

import edu.sjsu.airport.config.SystemLimits;
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
                    11);

            running = handleMainChoice(choice);
        }

        output.println("Airport system closed safely.");
    }

    private boolean handleMainChoice(int choice) {
        switch (choice) {
            case 1 -> registerAirport();
            case 2 -> listAirports();
            case 3 -> findAirport();
            case 4 -> removeAirport();
            case 5 -> recordTraffic(true);
            case 6 -> recordTraffic(false);
            case 7 -> displaySystemSummary();
            case 8 -> reportDisruption();
            case 9 -> resolveDisruption();
            case 10 -> resetAirportTraffic();
            case 11 -> new FlightPassengerConsole(
                    system,
                    input,
                    output).run();
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

    private void removeAirport() {
        output.println();
        output.println("Remove Airport");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");

        try {
            Airport removedAirport = system.removeAirport(code);

            output.printf(
                    "%s was removed successfully.%n",
                    removedAirport.getAirportCode());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to remove airport: "
                            + exception.getMessage());
        }
    }

    private void recordTraffic(boolean arrival) {
        output.println();
        output.println(
                arrival ? "Record Arrival" : "Record Departure");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");

        try {
            Airport airport = system.findAirport(code)
                    .orElseThrow(() ->
                            new NoSuchElementException(
                                    "No airport found with code "
                                            + code.toUpperCase()
                                            + "."));

            if (arrival) {
                airport.recordArrival();
            } else {
                airport.recordDeparture();
            }

            output.printf(
                    "%s recorded for %s. Daily traffic: %d%n",
                    arrival ? "Arrival" : "Departure",
                    airport.getAirportCode(),
                    airport.calculateDailyTraffic());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to record traffic: "
                            + exception.getMessage());
        }
    }

    private void reportDisruption() {
        output.println();
        output.println("Report Airport Disruption");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");
        String reason = input.readRequiredLine(
                "Disruption reason: ");

        try {
            Airport airport = requireAirport(code);
            airport.reportDisruption(reason);

            output.printf(
                    "%s operations suspended: %s%n",
                    airport.getAirportCode(),
                    airport.getDisruptionMessage());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to report disruption: "
                            + exception.getMessage());
        }
    }

    private void resolveDisruption() {
        output.println();
        output.println("Resolve Airport Disruption");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");

        try {
            Airport airport = requireAirport(code);
            airport.resolveDisruption();

            output.printf(
                    "%s operations resumed.%n",
                    airport.getAirportCode());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to resolve disruption: "
                            + exception.getMessage());
        }
    }

    private void resetAirportTraffic() {
        output.println();
        output.println("Reset Airport Traffic");

        String code = input.readRequiredLine(
                "Three-letter airport code: ");

        try {
            Airport airport = requireAirport(code);
            airport.resetDailyTraffic();

            output.printf(
                    "%s daily traffic was reset.%n",
                    airport.getAirportCode());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to reset traffic: "
                            + exception.getMessage());
        }
    }

    private Airport requireAirport(String code) {
        return system.findAirport(code)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "No airport found with code "
                                        + code.toUpperCase()
                                        + "."));
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
        output.println("4. Remove an airport");
        output.println("5. Record an arrival");
        output.println("6. Record a departure");
        output.println("7. View system summary");
        output.println("8. Report an airport disruption");
        output.println("9. Resolve an airport disruption");
        output.println("10. Reset an airport's daily traffic");
        output.println("11. Manage flights and passengers");
        output.println("0. Exit");
    }

    private void displaySystemSummary() {
        output.println();
        output.println("System Summary");
        output.printf(
                "Registered airports: %d/%d%n",
                system.getAirportCount(),
                SystemLimits.MAXIMUM_INSTANCES);
        output.printf(
                "Daily flight movements: %d%n",
                system.calculateTotalDailyTraffic());
        output.printf(
                "Accepting airport registrations: %s%n",
                system.hasCapacity() ? "Yes" : "No");
    }
}
