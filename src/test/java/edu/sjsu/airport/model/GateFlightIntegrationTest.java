package edu.sjsu.airport.model;

import edu.sjsu.airport.exception.GateUnavailableException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GateFlightIntegrationTest {

    @Test
    void assignmentAndReleaseUpdateBothObjects() {
        Gate gate = new Gate("A1", "A", 150, true, true);
        Flight flight = createDomesticFlight("SJ401", 100);

        gate.assignFlight(flight);

        assertSame(flight, gate.getAssignedFlight());
        assertSame(gate, flight.getAssignedGate());
        assertFalse(gate.isAvailable());

        gate.releaseFlight();

        assertNull(gate.getAssignedFlight());
        assertNull(flight.getAssignedGate());
        assertTrue(gate.isAvailable());
    }

    @Test
    void rejectsFlightLargerThanGateCapacity() {
        Gate gate = new Gate("A2", "A", 50, true, true);
        Flight flight = createDomesticFlight("SJ402", 100);

        assertFalse(gate.canAcceptFlight(flight));
        assertThrows(
                GateUnavailableException.class,
                () -> gate.assignFlight(flight));
    }

    @Test
    void domesticOnlyGateRejectsInternationalFlight() {
        Gate gate = new Gate("A3", "A", 200, false, true);
        Flight flight = createInternationalFlight("SJ403");

        assertFalse(gate.canAcceptFlight(flight));
        assertThrows(
                GateUnavailableException.class,
                () -> gate.assignFlight(flight));
    }

    @Test
    void flightCannotBeAssignedToTwoGates() {
        Gate firstGate = new Gate(
                "A4", "A", 200, true, true);
        Gate secondGate = new Gate(
                "A5", "A", 200, true, true);
        Flight flight = createDomesticFlight("SJ404", 100);

        firstGate.assignFlight(flight);

        assertFalse(secondGate.canAcceptFlight(flight));
        assertThrows(
                GateUnavailableException.class,
                () -> secondGate.assignFlight(flight));
        assertSame(firstGate, flight.getAssignedGate());
    }

    private DomesticFlight createDomesticFlight(
            String flightNumber,
            int capacity) {

        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new DomesticFlight(
                flightNumber,
                "SFO",
                "LAX",
                departure,
                departure.plusHours(2),
                capacity,
                "California",
                0.08,
                true);
    }

    private InternationalFlight createInternationalFlight(
            String flightNumber) {

        LocalDateTime departure =
                LocalDateTime.now().plusDays(1);

        return new InternationalFlight(
                flightNumber,
                "SFO",
                "LHR",
                departure,
                departure.plusHours(10),
                100,
                "United Kingdom",
                0.20,
                true);
    }
}
