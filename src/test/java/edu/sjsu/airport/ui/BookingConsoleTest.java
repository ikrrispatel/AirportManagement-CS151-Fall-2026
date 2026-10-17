package edu.sjsu.airport.ui;

import edu.sjsu.airport.enums.BookingStatus;
import edu.sjsu.airport.model.Booking;
import edu.sjsu.airport.model.DomesticFlight;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Passenger;
import edu.sjsu.airport.service.AirportManagementSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingConsoleTest {

    @Test
    void createsConfirmsAndListsBooking() {
        AirportManagementSystem system = createSystem();

        String output = runConsole(
                system,
                "1\nP1001\nSJ1001\nB1001\n12A\n200\n"
                        + "3\nB1001\n"
                        + "2\n0\n");

        Booking booking =
                system.findBooking("B1001").orElseThrow();

        assertEquals(1, system.getBookingCount());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertTrue(output.contains(
                "B1001 was created successfully."));
        assertTrue(output.contains("confirmationNumber='B1001'"));
    }

    @Test
    void checksInAndBoardsPassenger() {
        AirportManagementSystem system = createSystem();

        runConsole(
                system,
                "1\nP1001\nSJ1001\nB1002\n14B\n150\n"
                        + "3\nB1002\n"
                        + "5\nB1002\n"
                        + "6\nB1002\n"
                        + "0\n");

        assertEquals(
                BookingStatus.BOARDED,
                system.findBooking("B1002")
                        .orElseThrow()
                        .getStatus());
    }

    @Test
    void rebooksStandbyPassengerThroughConsole() {
        AirportManagementSystem system = createSystem();

        runConsole(
                system,
                "1\nP1001\nSJ1001\nB1003\n16C\n175\n"
                        + "4\nB1003\n"
                        + "8\nB1003\nSJ1002\n"
                        + "0\n");

        Booking booking =
                system.findBooking("B1003").orElseThrow();
        Passenger passenger =
                system.findPassenger("P1001").orElseThrow();
        Flight original =
                system.findFlight("SJ1001").orElseThrow();
        Flight replacement =
                system.findFlight("SJ1002").orElseThrow();

        assertEquals(BookingStatus.REBOOKED, booking.getStatus());
        assertSame(replacement, booking.getFlight());
        assertFalse(original.isOnStandby(passenger));
        assertTrue(replacement.hasPassenger(passenger));
    }

    private AirportManagementSystem createSystem() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Passenger passenger = new Passenger(
                "P1001",
                "Console Passenger",
                "CA-ID-1001",
                "",
                false);

        system.addPassenger(passenger);
        system.addFlight(createFlight("SJ1001", 0));
        system.addFlight(createFlight("SJ1002", 60));
        return system;
    }

    private Flight createFlight(
            String number,
            int minuteOffset) {

        LocalDateTime departure =
                LocalDateTime.now()
                        .plusDays(1)
                        .plusMinutes(minuteOffset);

        return new DomesticFlight(
                number,
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

            new BookingConsole(
                    system,
                    input,
                    printStream).run();
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
