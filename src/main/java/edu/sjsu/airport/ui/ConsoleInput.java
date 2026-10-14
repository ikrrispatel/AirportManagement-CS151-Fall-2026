package edu.sjsu.airport.ui;

import edu.sjsu.airport.exception.ExitRequestedException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

/**
 * Provides validated console input through the application's only Scanner.
 */
public final class ConsoleInput implements AutoCloseable {
    public static final String EXIT_COMMAND = "EXIT";

    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleInput(InputStream input, PrintStream output) {
        scanner = new Scanner(
                Objects.requireNonNull(input, "Input cannot be null."));
        this.output = Objects.requireNonNull(
                output,
                "Output cannot be null.");
    }

    /**
     * Reads one line after displaying a prompt.
     *
     * @param prompt text displayed before reading
     * @return trimmed user input
     */
    public String readLine(String prompt) {
        output.print(Objects.requireNonNull(
                prompt,
                "Prompt cannot be null."));

        if (!scanner.hasNextLine()) {
            throw new NoSuchElementException(
                    "The input stream was closed.");
        }

        String value = scanner.nextLine().trim();
        checkForExit(value);
        return value;
    }

    /**
     * Reads a required nonblank line.
     *
     * @param prompt text displayed before reading
     * @return nonblank trimmed input
     */
    public String readRequiredLine(String prompt) {
        while (true) {
            String value = readLine(prompt);

            if (!value.isBlank()) {
                return value;
            }

            output.println("Input cannot be blank. Please try again.");
        }
    }

    /**
     * Reads an integer inside an inclusive range.
     *
     * @param prompt text displayed before reading
     * @param minimum smallest accepted value
     * @param maximum largest accepted value
     * @return validated integer
     */
    public int readInt(
            String prompt,
            int minimum,
            int maximum) {

        if (minimum > maximum) {
            throw new IllegalArgumentException(
                    "Minimum cannot exceed maximum.");
        }

        while (true) {
            String value = readLine(prompt);

            try {
                int number = Integer.parseInt(value);

                if (number < minimum || number > maximum) {
                    output.printf(
                            "Enter a number from %d to %d.%n",
                            minimum,
                            maximum);
                    continue;
                }

                return number;
            } catch (NumberFormatException exception) {
                output.println(
                        "Enter a valid whole number.");
            }
        }
    }

    /**
     * Reads a decimal value at or above a minimum.
     *
     * @param prompt text displayed before reading
     * @param minimum smallest accepted value
     * @return validated decimal value
     */
    public double readDouble(
            String prompt,
            double minimum) {

        while (true) {
            String value = readLine(prompt);

            try {
                double number = Double.parseDouble(value);

                if (!Double.isFinite(number)
                        || number < minimum) {
                    output.printf(
                            "Enter a number greater than or equal to %.2f.%n",
                            minimum);
                    continue;
                }

                return number;
            } catch (NumberFormatException exception) {
                output.println(
                        "Enter a valid number.");
            }
        }
    }

    /**
     * Reads a yes-or-no response.
     *
     * @param prompt text displayed before reading
     * @return true for yes and false for no
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt);

            if (value.equalsIgnoreCase("yes")
                    || value.equalsIgnoreCase("y")) {
                return true;
            }

            if (value.equalsIgnoreCase("no")
                    || value.equalsIgnoreCase("n")) {
                return false;
            }

            output.println(
                    "Enter yes or no.");
        }
    }

    private void checkForExit(String value) {
        if (EXIT_COMMAND.equalsIgnoreCase(value)) {
            throw new ExitRequestedException();
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
