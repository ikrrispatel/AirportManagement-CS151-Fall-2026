package edu.sjsu.airport.model;

import edu.sjsu.airport.enums.BaggageStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaggageTest {

    @Test
    void followsCompleteBaggageLifecycle() {
        Passenger passenger = createPassenger();
        Flight flight = createFlight("SJ601");
        Baggage baggage = new Baggage(
                "TAG-601",
                passenger,
                BaggageStatus.CREATED,
                "Check-in counter",
                20.0);

        baggage.checkInBaggage(flight);
        baggage.loadOnFlight();
        baggage.markInTransit();
        baggage.markReadyForClaim();
        baggage.markClaimed();

        assertEquals(BaggageStatus.CLAIMED, baggage.getStatus());
        assertNull(baggage.getAssignedFlight());
    }

    @Test
    void rejectsLoadingBeforeCheckIn() {
        Baggage baggage = createBaggage("TAG-602");

        assertThrows(
                IllegalStateException.class,
                baggage::loadOnFlight);
    }

    @Test
    void transfersBaggageToReplacementFlight() {
        Baggage baggage = createBaggage("TAG-603");
        Flight originalFlight = createFlight("SJ603");
        Flight replacementFlight = createFlight("SJ604");

        baggage.checkInBaggage(originalFlight);
        baggage.transferToFlight(replacementFlight);

        assertEquals(BaggageStatus.TRANSFERRED, baggage.getStatus());
        assertSame(replacementFlight, baggage.getAssignedFlight());
    }

    @Test
    void trackableMethodsUseValidatedTransitions() {
        Baggage baggage = createBaggage("TAG-604");
        baggage.checkInBaggage(createFlight("SJ605"));

        baggage.updateStatus(" loaded ");
        baggage.updateStatus("in_transit");

        assertEquals("IN_TRANSIT", baggage.getCurrentStatus());
        assertTrue(
                baggage.getTrackingSummary().contains("TAG-604"));
        assertThrows(
                IllegalStateException.class,
                () -> baggage.updateStatus("CREATED"));
    }

    @Test
    void rejectsInvalidConstructorValues() {
        Passenger passenger = createPassenger();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Baggage(
                        "TAG-605",
                        passenger,
                        BaggageStatus.CREATED,
                        "Counter",
                        Double.NaN));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Baggage(
                        " ",
                        passenger,
                        BaggageStatus.CREATED,
                        "Counter",
                        10.0));
    }

    private Baggage createBaggage(String tagNumber) {
        return new Baggage(
                tagNumber,
                createPassenger(),
                BaggageStatus.CREATED,
                "Check-in counter",
                15.0);
    }

    private Passenger createPassenger() {
        return new Passenger(
                "P601",
                "Test Passenger",
                "CA-ID-601",
                "P6012345",
                true);
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
