public class SynchronizedCounter {

    static SynchronizedCounter synchronizedCounter;

    private static int totalTicketsBooked = 0;
    private static int totalTripsBooked = 0;

    private SynchronizedCounter(){
    }

    public static synchronized SynchronizedCounter getSynchronizedCounter(){
        if(synchronizedCounter == null){
            synchronizedCounter = new SynchronizedCounter();
        }
        return synchronizedCounter;
    }

    public static void incrementTicketCounter(){
        totalTicketsBooked++;
    }

    public int getTicketCounter(){
        return totalTicketsBooked;
    }

    public static void incrementTripsCounter(){
        totalTripsBooked++;
    }

    public int getTripsCounter(){
        return totalTripsBooked;
    }

}
