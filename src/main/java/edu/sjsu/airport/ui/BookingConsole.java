package edu.sjsu.airport.ui;

import edu.sjsu.airport.model.Booking;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Passenger;
import edu.sjsu.airport.service.AirportManagementSystem;

import java.io.PrintStream;
import java.util.Objects;

public final class BookingConsole {
    private final AirportManagementSystem system;
    private final ConsoleInput input;
    private final PrintStream output;

    public BookingConsole(
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
                    "Select an option: ", 0, 9);

            switch (choice) {
                case 1 -> createBooking();
                case 2 -> listBookings();
                case 3 -> performBookingAction(
                        "Confirm Booking",
                        Booking::confirmBooking);
                case 4 -> performBookingAction(
                        "Move Booking to Standby",
                        Booking::moveToStandby);
                case 5 -> performBookingAction(
                        "Check In Passenger",
                        Booking::markCheckedIn);
                case 6 -> performBookingAction(
                        "Board Passenger",
                        Booking::markBoarded);
                case 7 -> performBookingAction(
                        "Cancel Booking",
                        Booking::cancelBooking);
                case 8 -> rebookPassenger();
                case 9 -> changeSeat();
                case 0 -> running = false;
                default -> throw new IllegalStateException(
                        "Unexpected menu choice: " + choice);
            }
        }
    }

    private void createBooking() {
        output.println();
        output.println("Create Booking");

        try {
            Passenger passenger = requirePassenger();
            Flight flight = requireFlight();

            Booking booking = new Booking(
                    input.readRequiredLine(
                            "Confirmation number: "),
                    passenger,
                    flight,
                    input.readRequiredLine("Seat number: "),
                    input.readDouble("Base fare: ", 0.0));

            passenger.requestBooking(flight);
            passenger.addBooking(booking);
            system.addBooking(booking);

            output.println(
                    booking.getConfirmationNumber()
                            + " was created successfully.");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to create booking: "
                            + exception.getMessage());
        }
    }

    private void listBookings() {
        output.println();
        output.println("Registered Bookings");

        if (system.getBookings().isEmpty()) {
            output.println("No bookings are registered.");
            return;
        }

        system.getBookings().forEach(output::println);
    }

    private void performBookingAction(
            String heading,
            BookingAction action) {

        output.println();
        output.println(heading);

        try {
            Booking booking = requireBooking();
            action.perform(booking);
            output.println(
                    booking.getConfirmationNumber()
                            + " status: "
                            + booking.getStatus());
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to update booking: "
                            + exception.getMessage());
        }
    }

    private void rebookPassenger() {
        output.println();
        output.println("Rebook Passenger");

        try {
            Booking booking = requireBooking();
            Flight replacement = requireFlight();
            booking.markRebooked(replacement);

            output.println(
                    booking.getConfirmationNumber()
                            + " moved to "
                            + replacement.getFlightNumber()
                            + ".");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to rebook passenger: "
                            + exception.getMessage());
        }
    }

    private void changeSeat() {
        output.println();
        output.println("Change Seat");

        try {
            Booking booking = requireBooking();
            String seat = input.readRequiredLine(
                    "New seat number: ");
            booking.changeSeat(seat);

            output.println(
                    booking.getConfirmationNumber()
                            + " seat changed to "
                            + booking.getSeatNumber()
                            + ".");
        } catch (RuntimeException exception) {
            output.println(
                    "Unable to change seat: "
                            + exception.getMessage());
        }
    }

    private Passenger requirePassenger() {
        String id = input.readRequiredLine("Passenger ID: ");

        return system.findPassenger(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No passenger found with ID "
                                        + id + "."));
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

    private Booking requireBooking() {
        String confirmation = input.readRequiredLine(
                "Confirmation number: ");

        return system.findBooking(confirmation)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No booking found with confirmation "
                                        + confirmation + "."));
    }

    private void displayMenu() {
        output.println();
        output.println("Booking Menu");
        output.println("1. Create a booking");
        output.println("2. List bookings");
        output.println("3. Confirm a booking");
        output.println("4. Move a booking to standby");
        output.println("5. Check in a passenger");
        output.println("6. Board a passenger");
        output.println("7. Cancel a booking");
        output.println("8. Rebook a passenger");
        output.println("9. Change a seat");
        output.println("0. Return to main menu");
    }

    @FunctionalInterface
    private interface BookingAction {
        void perform(Booking booking);
    }
}
