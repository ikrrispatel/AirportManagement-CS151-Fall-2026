package edu.sjsu.airport.ui;

import edu.sjsu.airport.exception.ExitRequestedException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleInputTest {

    @Test
    void requiredLineRejectsBlankInput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput(
                "\nSFO\n",
                output)) {
            assertEquals(
                    "SFO",
                    input.readRequiredLine("Airport code: "));
        }

        assertTrue(output.toString().contains(
                "Input cannot be blank"));
    }

    @Test
    void integerInputRejectsTextAndOutOfRangeValues() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput(
                "abc\n9\n3\n",
                output)) {
            assertEquals(
                    3,
                    input.readInt("Choice: ", 1, 5));
        }

        String messages = output.toString();
        assertTrue(messages.contains("valid whole number"));
        assertTrue(messages.contains("from 1 to 5"));
    }

    @Test
    void decimalInputRejectsValuesBelowMinimum() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput(
                "-1\n25.50\n",
                output)) {
            assertEquals(
                    25.50,
                    input.readDouble("Fare: ", 0.0));
        }

        assertTrue(output.toString().contains(
                "greater than or equal"));
    }

    @Test
    void yesResponseReturnsTrue() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput("y\n", output)) {
            assertTrue(input.readYesNo("Continue? "));
        }
    }

    @Test
    void noResponseReturnsFalse() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput("NO\n", output)) {
            assertFalse(input.readYesNo("Continue? "));
        }
    }

    @Test
    void exitCommandWorksFromLineInput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput(
                " exit \n",
                output)) {
            assertThrows(
                    ExitRequestedException.class,
                    () -> input.readLine("Command: "));
        }
    }

    @Test
    void exitCommandWorksDuringNumericInput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (ConsoleInput input = createInput(
                "EXIT\n",
                output)) {
            assertThrows(
                    ExitRequestedException.class,
                    () -> input.readInt("Choice: ", 1, 5));
        }
    }

    private ConsoleInput createInput(
            String text,
            ByteArrayOutputStream output) {

        return new ConsoleInput(
                new ByteArrayInputStream(
                        text.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(
                        output,
                        true,
                        StandardCharsets.UTF_8));
    }
}
