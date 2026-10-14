package edu.sjsu.airport.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents an airport managed by the airport operations system.
 *
 * Airport identity is determined by its unique three-letter airport code.
 */
public class Airport {
    private final String airportCode;
    private String name;
    private String city;
    private String country;
    private boolean operational;
    private int dailyArrivals;
    private int dailyDepartures;
    private String disruptionMessage;

    /**
     * Creates an operational airport with no recorded traffic or disruptions.
     *
     * @param airportCode unique three-letter airport code
     * @param name official airport name
     * @param city city served by the airport
     * @param country country containing the airport
     */
    public Airport(
            String airportCode,
            String name,
            String city,
            String country) {

        this.airportCode = normalizeAirportCode(airportCode);
        this.name = requireText(name, "Airport name");
        this.city = requireText(city, "City");
        this.country = requireText(country, "Country");
        this.operational = true;
        this.dailyArrivals = 0;
        this.dailyDepartures = 0;
        this.disruptionMessage = "";
    }

    public String getAirportCode() {
        return airportCode;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public boolean isOperational() {
        return operational;
    }

    public int getDailyArrivals() {
        return dailyArrivals;
    }

    public int getDailyDepartures() {
        return dailyDepartures;
    }

    public String getDisruptionMessage() {
        return disruptionMessage;
    }

    /**
     * Records a completed arrival when the airport is operational.
     */
    public void recordArrival() {
        ensureOperational();
        dailyArrivals++;
    }

    /**
     * Records a completed departure when the airport is operational.
     */
    public void recordDeparture() {
        ensureOperational();
        dailyDepartures++;
    }

    /**
     * Closes the airport because of an active operational disruption.
     *
     * @param reason explanation shown to airport staff
     */
    public void reportDisruption(String reason) {
        disruptionMessage = requireText(reason, "Disruption reason");
        operational = false;
    }

    /**
     * Resolves the active disruption and reopens the airport.
     */
    public void resolveDisruption() {
        if (operational) {
            throw new IllegalStateException(
                    "The airport has no active disruption.");
        }

        disruptionMessage = "";
        operational = true;
    }

    /**
     * Clears traffic totals at the beginning of a new operating day.
     */
    public void resetDailyTraffic() {
        dailyArrivals = 0;
        dailyDepartures = 0;
    }

    /**
     * Returns the total number of recorded flight movements today.
     *
     * @return arrivals plus departures
     */
    public int calculateDailyTraffic() {
        return dailyArrivals + dailyDepartures;
    }

    /**
     * Updates the airport's descriptive location information.
     *
     * @param city new city
     * @param country new country
     */
    public void relocate(String city, String country) {
        this.city = requireText(city, "City");
        this.country = requireText(country, "Country");
    }

    private void ensureOperational() {
        if (!operational) {
            throw new IllegalStateException(
                    "Airport operations are suspended: "
                            + disruptionMessage);
        }
    }


    private static String normalizeAirportCode(String airportCode) {
        String normalizedCode =
                requireText(airportCode, "Airport code")
                        .toUpperCase(Locale.ROOT);

        if (!normalizedCode.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                    "Airport code must contain exactly three letters.");
        }

        return normalizedCode;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be blank.");
        }

        return value.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Airport airport)) {
            return false;
        }

        return airportCode.equals(airport.airportCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airportCode);
    }

    @Override
    public String toString() {
        return "Airport{"
                + "airportCode='" + airportCode + '\''
                + ", name='" + name + '\''
                + ", city='" + city + '\''
                + ", country='" + country + '\''
                + ", operational=" + operational
                + ", dailyArrivals=" + dailyArrivals
                + ", dailyDepartures=" + dailyDepartures
                + ", disruptionMessage='" + disruptionMessage + '\''
                + '}';
    }
}
