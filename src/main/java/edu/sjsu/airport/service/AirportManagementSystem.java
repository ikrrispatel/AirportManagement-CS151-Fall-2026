package edu.sjsu.airport.service;

import edu.sjsu.airport.config.SystemLimits;
import edu.sjsu.airport.exception.CapacityExceededException;
import edu.sjsu.airport.exception.DuplicateAirportException;
import edu.sjsu.airport.exception.DuplicateEntityException;
import edu.sjsu.airport.model.Airport;
import edu.sjsu.airport.model.Baggage;
import edu.sjsu.airport.model.Booking;
import edu.sjsu.airport.model.Flight;
import edu.sjsu.airport.model.Gate;
import edu.sjsu.airport.model.Passenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Coordinates objects registered with the airport management system.
 */
public class AirportManagementSystem {
    private final List<Airport> airports;
    private final List<Flight> flights;
    private final List<Passenger> passengers;
    private final List<Booking> bookings;
    private final List<Gate> gates;
    private final List<Baggage> baggageItems;

    public AirportManagementSystem() {
        airports = new ArrayList<>();
        flights = new ArrayList<>();
        passengers = new ArrayList<>();
        bookings = new ArrayList<>();
        gates = new ArrayList<>();
        baggageItems = new ArrayList<>();
    }

    public void addAirport(Airport airport) {
        Objects.requireNonNull(airport, "Airport cannot be null.");

        if (!hasCapacity()) {
            throw new CapacityExceededException(
                    "airport", SystemLimits.MAXIMUM_INSTANCES);
        }

        if (airports.contains(airport)) {
            throw new DuplicateAirportException(
                    airport.getAirportCode());
        }

        airports.add(airport);
    }

    public void addFlight(Flight flight) {
        addUnique(
                flights,
                flight,
                "flight",
                Flight::getFlightNumber);
    }

    public void addPassenger(Passenger passenger) {
        addUnique(
                passengers,
                passenger,
                "passenger",
                Passenger::getPassengerId);
    }

    public void addBooking(Booking booking) {
        addUnique(
                bookings,
                booking,
                "booking",
                Booking::getConfirmationNumber);
    }

    public void addGate(Gate gate) {
        addUnique(
                gates,
                gate,
                "gate",
                Gate::getGateNumber);
    }

    public void addBaggage(Baggage baggage) {
        addUnique(
                baggageItems,
                baggage,
                "baggage",
                Baggage::getTagNumber);
    }

    public Optional<Airport> findAirport(String airportCode) {
        String normalizedCode = normalizeCode(airportCode);

        return airports.stream()
                .filter(airport -> airport.getAirportCode()
                        .equals(normalizedCode))
                .findFirst();
    }

    public Optional<Flight> findFlight(String flightNumber) {
        return findByIdentifier(
                flights,
                flightNumber,
                Flight::getFlightNumber);
    }

    public Optional<Passenger> findPassenger(String passengerId) {
        return findByIdentifier(
                passengers,
                passengerId,
                Passenger::getPassengerId);
    }

    public Optional<Booking> findBooking(
            String confirmationNumber) {

        return findByIdentifier(
                bookings,
                confirmationNumber,
                Booking::getConfirmationNumber);
    }

    public Optional<Gate> findGate(String gateNumber) {
        return findByIdentifier(
                gates,
                gateNumber,
                Gate::getGateNumber);
    }

    public Optional<Baggage> findBaggage(String tagNumber) {
        return findByIdentifier(
                baggageItems,
                tagNumber,
                Baggage::getTagNumber);
    }

    public Airport removeAirport(String airportCode) {
        Airport airport = findAirport(airportCode)
                .orElseThrow(() -> new NoSuchElementException(
                        "No airport found with code "
                                + normalizeCode(airportCode) + "."));

        airports.remove(airport);
        return airport;
    }

    public int calculateTotalDailyTraffic() {
        return airports.stream()
                .mapToInt(Airport::calculateDailyTraffic)
                .sum();
    }

    public int getAirportCount() {
        return airports.size();
    }

    public int getFlightCount() {
        return flights.size();
    }

    public int getPassengerCount() {
        return passengers.size();
    }

    public int getBookingCount() {
        return bookings.size();
    }

    public int getGateCount() {
        return gates.size();
    }

    public int getBaggageCount() {
        return baggageItems.size();
    }

    public boolean hasCapacity() {
        return hasCapacity(airports);
    }

    public List<Airport> getAirports() {
        return List.copyOf(airports);
    }

    public List<Flight> getFlights() {
        return List.copyOf(flights);
    }

    public List<Passenger> getPassengers() {
        return List.copyOf(passengers);
    }

    public List<Booking> getBookings() {
        return List.copyOf(bookings);
    }

    public List<Gate> getGates() {
        return List.copyOf(gates);
    }

    public List<Baggage> getBaggageItems() {
        return List.copyOf(baggageItems);
    }

    private <T> void addUnique(
            List<T> objects,
            T object,
            String objectType,
            Function<T, String> identifierExtractor) {

        Objects.requireNonNull(
                object,
                capitalize(objectType) + " cannot be null.");

        if (!hasCapacity(objects)) {
            throw new CapacityExceededException(
                    objectType,
                    SystemLimits.MAXIMUM_INSTANCES);
        }

        String identifier =
                identifierExtractor.apply(object);

        boolean duplicate = objects.stream()
                .map(identifierExtractor)
                .anyMatch(existing ->
                        existing.equalsIgnoreCase(identifier));

        if (duplicate) {
            throw new DuplicateEntityException(
                    objectType,
                    identifier);
        }

        objects.add(object);
    }

    private <T> Optional<T> findByIdentifier(
            List<T> objects,
            String identifier,
            Function<T, String> identifierExtractor) {

        String validatedIdentifier =
                requireIdentifier(identifier);

        return objects.stream()
                .filter(object -> identifierExtractor.apply(object)
                        .equalsIgnoreCase(validatedIdentifier))
                .findFirst();
    }

    private boolean hasCapacity(List<?> objects) {
        return objects.size()
                < SystemLimits.MAXIMUM_INSTANCES;
    }

    private static String requireIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Identifier cannot be blank.");
        }

        return identifier.trim();
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0))
                + value.substring(1);
    }

    private static String normalizeCode(String airportCode) {
        if (airportCode == null || airportCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Airport code cannot be blank.");
        }

        String normalizedCode =
                airportCode.trim().toUpperCase(Locale.ROOT);

        if (!normalizedCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Airport code must contain exactly three letters.");
        }

        return normalizedCode;
    }
}
