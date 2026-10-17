package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.FlightStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlightLifecycleTest {

    @Test
    void followsValidFlightLifecycle() {
        Flight flight = createFlight("SJ501");

        flight.delayFlight(30);
        assertEquals(FlightStatus.DELAYED, flight.getStatus());
        assertEquals(30, flight.getDelayMinutes());

        flight.openBoarding();
        flight.closeBoarding();
        flight.markDeparted();
        flight.markArrived();

        assertEquals(FlightStatus.ARRIVED, flight.getStatus());
    }

    @Test
    void rejectsInvalidLifecycleTransition() {
        Flight flight = createFlight("SJ502");

        assertThrows(
                IllegalStateException.class,
                flight::markDeparted);
    }

    @Test
    void cancellationPreventsBoarding() {
        Flight flight = createFlight("SJ503");

        flight.cancelFlight();

        assertEquals(FlightStatus.CANCELED, flight.getStatus());
        assertThrows(
                IllegalStateException.class,
                flight::openBoarding);
        assertThrows(
                IllegalStateException.class,
                flight::cancelFlight);
    }

    @Test
    void trackingUpdateAcceptsTrimmedStatus() {
        Flight flight = createFlight("SJ504");

        flight.updateStatus(" boarding ");

        assertEquals(FlightStatus.BOARDING, flight.getStatus());
        assertEquals("BOARDING", flight.getCurrentStatus());
    }

    private Flight createFlight(String flightNumber) {
        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                flightNumber,
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                100,
                "California",
                0.08,
                true);
    }
}
