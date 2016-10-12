import java.util.Random;

public class ticketingThread implements Runnable {

    private Integer threadID;
    public Thread t;
    private TicketBooker ticketBooker = new TicketBooker();

    public ticketingThread(int threadNumber){
        threadID = threadNumber;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            ticketBooker.bookATicket();
        }
    }

    public void start(){
        System.out.println("Starting Thread ----" + threadID);
        if(t == null){
            t = new Thread(this,threadID.toString());
            t.start();
        }
    }
}
