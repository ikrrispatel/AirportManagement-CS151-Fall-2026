package edu.sjsu.airport.service;

import edu.sjsu.airport.config.SystemLimits;
import edu.sjsu.airport.exception.CapacityExceededException;
import edu.sjsu.airport.exception.DuplicateEntityException;
import edu.sjsu.airport.model.Baggage;
import edu.sjsu.airport.enums.BaggageStatus;
import edu.sjsu.airport.model.Booking;
import edu.sjsu.airport.model.DomesticFlight;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Gate;
import edu.sjsu.airport.model.Passenger;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectRegistryTest {

    @Test
    void registersAndFindsEveryManagedObjectType() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Passenger passenger = createPassenger("P801");
        Flight flight = createFlight("SJ801", 0);
        Booking booking = new Booking(
                "B801", passenger, flight, "10A", 100.0);
        Gate gate = new Gate(
                "G801", "A", 150, true, true);
        Baggage baggage = new Baggage(
                "TAG801",
                passenger,
                BaggageStatus.CREATED,
                "Counter",
                20.0);

        system.addPassenger(passenger);
        system.addFlight(flight);
        system.addBooking(booking);
        system.addGate(gate);
        system.addBaggage(baggage);

        assertSame(
                passenger,
                system.findPassenger(" p801 ").orElseThrow());
        assertSame(
                flight,
                system.findFlight("sj801").orElseThrow());
        assertSame(
                booking,
                system.findBooking("b801").orElseThrow());
        assertSame(
                gate,
                system.findGate("g801").orElseThrow());
        assertSame(
                baggage,
                system.findBaggage("tag801").orElseThrow());
    }

    @Test
    void rejectsDuplicateIdentifiersIgnoringCase() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Passenger passenger = createPassenger("P802");
        Flight flight = createFlight("SJ802", 0);

        system.addPassenger(passenger);
        system.addFlight(flight);
        system.addBooking(new Booking(
                "B802", passenger, flight, "10A", 100.0));
        system.addGate(new Gate(
                "G802", "A", 150, true, true));
        system.addBaggage(new Baggage(
                "TAG802",
                passenger,
                BaggageStatus.CREATED,
                "Counter",
                20.0));

        assertThrows(
                DuplicateEntityException.class,
                () -> system.addPassenger(
                        createPassenger("p802")));
        assertThrows(
                DuplicateEntityException.class,
                () -> system.addFlight(
                        createFlight("sj802", 1)));
        assertThrows(
                DuplicateEntityException.class,
                () -> system.addBooking(new Booking(
                        "b802",
                        passenger,
                        flight,
                        "11A",
                        120.0)));
        assertThrows(
                DuplicateEntityException.class,
                () -> system.addGate(new Gate(
                        "g802", "B", 100, false, true)));
        assertThrows(
                DuplicateEntityException.class,
                () -> system.addBaggage(new Baggage(
                        "tag802",
                        passenger,
                        BaggageStatus.CREATED,
                        "Counter",
                        15.0)));
    }

    @Test
    void enforcesIndependentCapacityForEveryRegistry() {
        AirportManagementSystem system =
                new AirportManagementSystem();
        Passenger bookingPassenger =
                createPassenger("BOOKING-OWNER");
        Flight bookingFlight =
                createFlight("BOOKING-FLIGHT", 500);

        for (int index = 0;
             index < SystemLimits.MAXIMUM_INSTANCES;
             index++) {

            system.addPassenger(
                    createPassenger("P" + index));
            system.addFlight(
                    createFlight("F" + index, index));
            system.addBooking(new Booking(
                    "B" + index,
                    bookingPassenger,
                    bookingFlight,
                    "S" + index,
                    100.0));
            system.addGate(new Gate(
                    "G" + index,
                    "T",
                    200,
                    true,
                    true));
            system.addBaggage(new Baggage(
                    "TAG" + index,
                    bookingPassenger,
                    BaggageStatus.CREATED,
                    "Counter",
                    20.0));
        }

        assertEquals(
                SystemLimits.MAXIMUM_INSTANCES,
                system.getPassengerCount());
        assertEquals(
                SystemLimits.MAXIMUM_INSTANCES,
                system.getFlightCount());
        assertEquals(
                SystemLimits.MAXIMUM_INSTANCES,
                system.getBookingCount());
        assertEquals(
                SystemLimits.MAXIMUM_INSTANCES,
                system.getGateCount());
        assertEquals(
                SystemLimits.MAXIMUM_INSTANCES,
                system.getBaggageCount());

        assertThrows(
                CapacityExceededException.class,
                () -> system.addPassenger(
                        createPassenger("OVER")));
        assertThrows(
                CapacityExceededException.class,
                () -> system.addFlight(
                        createFlight("OVER", 900)));
        assertThrows(
                CapacityExceededException.class,
                () -> system.addBooking(new Booking(
                        "OVER",
                        bookingPassenger,
                        bookingFlight,
                        "OVER",
                        100.0)));
        assertThrows(
                CapacityExceededException.class,
                () -> system.addGate(new Gate(
                        "OVER",
                        "T",
                        200,
                        true,
                        true)));
        assertThrows(
                CapacityExceededException.class,
                () -> system.addBaggage(new Baggage(
                        "OVER",
                        bookingPassenger,
                        BaggageStatus.CREATED,
                        "Counter",
                        20.0)));
    }

    private Passenger createPassenger(String passengerId) {
        return new Passenger(
                passengerId,
                "Test Passenger",
                "CA-ID",
                "",
                false);
    }

    private Flight createFlight(
            String flightNumber,
            int minuteOffset) {

        LocalDateTime departure =
                LocalDateTime.now()
                        .plusDays(1)
                        .plusMinutes(minuteOffset);

        return new DomesticFlight(
                flightNumber,
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                150,
                "California",
                0.08,
                true);
    }
}
