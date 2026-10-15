package edu.sjsu.airport.service;

import edu.sjsu.airport.config.SystemLimits;
import edu.sjsu.airport.exception.CapacityExceededException;
import edu.sjsu.airport.exception.DuplicateAirportException;
import edu.sjsu.airport.model.Airport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Coordinates airports registered with the management system.
 */
public class AirportManagementSystem {
    private final List<Airport> airports;

    public AirportManagementSystem() {
        airports = new ArrayList<>();
    }

    /**
     * Registers an airport after checking system capacity and duplicates.
     *
     * @param airport airport to register
     */
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

    /**
     * Finds an airport using its three-letter airport code.
     *
     * @param airportCode airport code to search for
     * @return matching airport when present
     */
    public Optional<Airport> findAirport(String airportCode) {
        String normalizedCode = normalizeCode(airportCode);

        return airports.stream()
                .filter(airport -> airport.getAirportCode()
                        .equals(normalizedCode))
                .findFirst();
    }

    /**
     * Removes and returns the airport matching the supplied code.
     *
     * @param airportCode airport code to remove
     * @return removed airport
     */
    public Airport removeAirport(String airportCode) {
        Airport airport = findAirport(airportCode)
                .orElseThrow(() -> new NoSuchElementException(
                        "No airport found with code "
                                + normalizeCode(airportCode) + "."));

        airports.remove(airport);
        return airport;
    }

    /**
     * Calculates traffic across every registered airport.
     *
     * @return total arrivals and departures
     */
    public int calculateTotalDailyTraffic() {
        return airports.stream()
                .mapToInt(Airport::calculateDailyTraffic)
                .sum();
    }

    public int getAirportCount() {
        return airports.size();
    }

    public boolean hasCapacity() {
        return airports.size() < SystemLimits.MAXIMUM_INSTANCES;
    }

    /**
     * Returns an unmodifiable snapshot of registered airports.
     *
     * @return airport snapshot
     */
    public List<Airport> getAirports() {
        return List.copyOf(airports);
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
