public class AirlineTicketing {

    public static void main(String[] args) throws InterruptedException {
        TicketBooker ticketBooker = new TicketBooker();
        ticketBooker.initializeNameArray();

        int totalNumOFThreads = 40;
//
//        if(args.length > 1) {
//            totalNumOFThreads = Integer.parseInt(args[1]);
//            System.out.println("Selected Number of threads is : "+ args[1]);
//        }else{
//            System.out.println("INCORRECT INPUT");
//            System.out.println("USAGE : <Number Of Threads>");
//            System.out.println("P.S Each thread will book 10 Tickets");
//        }

        if(false) {

            ticketBooker.bookATicket();

        }else {

          //  new DBInitializer().initializeDB();

            ticketingThread thread[] = new ticketingThread[totalNumOFThreads];

            for (int i = 0; i < totalNumOFThreads; i++) {

                thread[i] = new ticketingThread(i);
                thread[i].start();

            }


            for (int i = 0; i < totalNumOFThreads; i++) {
                thread[i].t.join();
            }
        }

        System.out.println("The total Number of reservations is : "+ SynchronizedCounter.getSynchronizedCounter().getTicketCounter() +" || trips booked is : " + SynchronizedCounter.getSynchronizedCounter().getTripsCounter());
    }

}
