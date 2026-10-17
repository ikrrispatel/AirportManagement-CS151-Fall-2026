package edu.sjsu.airport.interfaces;

public interface Trackable {
    String getCurrentStatus();
    void updateStatus(String status);
    String getTrackingSummary();
}
