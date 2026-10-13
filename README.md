# Airport Operations and Disruption Recovery System

## Overview

This project is a Java-based airport operations system developed for CS 151 Fall 2026. It models flights, passengers, bookings, gates, baggage, airport resources, and operational disruptions.

The system allows airport staff to manage flights and passengers while responding to realistic events such as delays, cancellations, gate changes, missed connections, and baggage tracking updates.

## Developers

- Krish Patel
  - Airport
  - AirportManagementSystem
  - Object limits and duplicate prevention
  - System integration

- Kevin Pham
  - Flight
  - DomesticFlight
  - InternationalFlight
  - Flight tracking and status management

- Thinh Nguyen
  - Passenger
  - Booking
  - Passenger and reservation workflows

- Mohammed Rahman
  - Gate
  - Baggage
  - Gate assignment and baggage tracking

## Design

The system uses object-oriented programming to represent airport operations.

Flight is an abstract class extended by DomesticFlight and InternationalFlight. Flight and Baggage implement the Trackable interface, allowing the system to process different trackable objects polymorphically.

Airport stores and coordinates flights, passengers, bookings, gates, and baggage. AirportManagementSystem manages airports and provides the application-level operations used by the user interface.

All fields are private. Business methods validate operations before changing object state. Duplicate objects and operations that would exceed the 100-instance limit are rejected.

## Installation Instructions

Installation and build instructions will be added after the project build configuration is finalized.

## Usage

The application will provide a text-based menu using one shared Scanner instance. Users will be able to create and manage airport objects, perform operational actions, and enter EXIT to end the program gracefully.

## Contributions

All development must occur on separately named branches. Completed work will be merged into main through pull requests after review and testing.
