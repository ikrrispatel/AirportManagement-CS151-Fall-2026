package edu.sjsu.airport.config;

/**
 * Defines application-wide object capacity limits.
 */
public final class SystemLimits {
    public static final int MAXIMUM_INSTANCES = 100;

    private SystemLimits() {
        throw new AssertionError(
                "SystemLimits cannot be instantiated.");
    }
}
