public class Gate{
    private String gateID;
    private int capacity;
    private boolean international;
    private boolean available;
    private boolean temporarilyClosed;
    
    public Gate(String gateID, int capacity, boolean international, boolean available, boolean temporarilyClosed)
    {
        this.gateID = gateID;
        this.capacity = capacity;
        this.international = international;
        this.available = available;
        this.temporarilyClosed = temporarilyClosed;
    }

    public String getGateID() 
    {
        return gateID;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public boolean isInternational()
    {
        return international;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public boolean isTemporaryClosed()
    {
        return temporarilyClosed;
    }



}