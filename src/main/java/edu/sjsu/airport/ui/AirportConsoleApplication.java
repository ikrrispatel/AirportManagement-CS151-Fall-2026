package edu.sjsu.airport.ui;

import edu.sjsu.airport.exception.ExitRequestedException;
import edu.sjsu.airport.service.AirportManagementSystem;

import java.io.PrintStream;
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

        output.println("======================================");
        output.println(" Airport Operations Management System ");
        output.println("======================================");
        output.println("Type EXIT at any prompt to leave safely.");

        while (running) {
            displayMainMenu();

            int choice = input.readInt(
                    "Select an option: ",
                    0,
                    1);

            switch (choice) {
                case 1 -> displaySystemSummary();
                case 0 -> running = false;
                default -> throw new IllegalStateException(
                        "Unexpected menu choice: " + choice);
            }
        }

        output.println("Airport system closed safely.");
    }

    private void displayMainMenu() {
        output.println();
        output.println("Main Menu");
        output.println("1. View system summary");
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
