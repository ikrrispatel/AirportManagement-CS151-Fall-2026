package edu.sjsu.airport.exception;

public class GateUnavailableException  extends RuntimeException{

    public GateUnavailableException()
    {
        super("Gate cannot accept this flight.");
    }
    
}
