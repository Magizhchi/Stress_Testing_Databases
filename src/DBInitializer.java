import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DBInitializer {

    DatabaseHelper databaseHelper = new DatabaseHelper();
    Random random = new Random();
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static ArrayList<String> airportCodes = new ArrayList<>(Arrays.asList("Houston USA","Chennai India","Beijing China","NewYork USA"));
    ArrayList<String> flightCodes = new ArrayList<>(Arrays.asList("10001","10002","10003","10004","10005","10006","10007","10008","10009","10010"));

    private int maxNumOfDaysPerFlight = 3;
    private int minimumNumberOfFlights = 20;

    public void initializeDB(){
        cleanDB();
        createAirports();
        createFlights();
        createSeats();
        createTrips();

    }

    private void createTrips() {
        System.out.println("------- Now Creating Trips -----");
        String tableName = "Trip_Details";
        long rangeBegin = System.currentTimeMillis();    // Timestamp.valueOf("2016-04-01 00:00:00").getTime();
        long rangeEnd = 7776000000L + System.currentTimeMillis();
        long diff = rangeEnd - rangeBegin + 1;
        Timestamp rand = new Timestamp(rangeBegin + (long)(random.nextFloat() * diff) );
        Date randDate = new Date(rand.getTime());
        List<String> tripDates = new ArrayList<>();
        for (String flightCode : flightCodes){
            while (tripDates.size() < maxNumOfDaysPerFlight){
                if( !tripDates.contains(randDate.toString()))
                    tripDates.add(randDate.toString());
                randDate = new Date(new Timestamp(rangeBegin + (long)(random.nextFloat() * diff) ).getTime());
            }

            for(String tripDate: tripDates){
                List<String> columnValues = new ArrayList<>();
                columnValues.add("'"+ tripDate +"'");
                columnValues.add(flightCode);
                columnValues.add("false");
                columnValues.add("null");
                columnValues.add(Integer.toString(random.nextInt(10) + 1));
                columnValues.add("false");

                databaseHelper.executeUpdate(databaseHelper.getInsertQuery(tableName, columnValues));
            }

            tripDates = new ArrayList<>();
        }
    }

    private void createSeats() {
        System.out.println("------- Now Creating Seats -----");
        String tableName = "Seat_Details";
        for(String flightCode : flightCodes){
            int totalNumOfSeats = random.nextInt(40) + minimumNumberOfFlights;
            for (int i = 1; i < totalNumOfSeats; i++) {
                List<String> columnValues = new ArrayList<>();
                columnValues.add(flightCode);
                columnValues.add(Integer.toString(i));
                if(i < totalNumOfSeats*0.2)
                    columnValues.add("true");
                else
                    columnValues.add("false");

                databaseHelper.executeUpdate(databaseHelper.getInsertQuery(tableName, columnValues));
            }
        }
    }

    private void createFlights() {
        System.out.println("------- Now Creating Flights -----");
        int millisInDay = 24*60*60*1000;
        String tableName = "Flight_Details";
        for(String flightCode:flightCodes){
            List<String> columnValues = new ArrayList<>();
            columnValues.add(flightCode);
            columnValues.add("'"+new Time((long) random.nextInt(millisInDay)).toString()+"'");
            int airportCode = random.nextInt(airportCodes.size());
            int nextAirportCode = random.nextInt(airportCodes.size());
            while( airportCode == nextAirportCode){
                nextAirportCode = random.nextInt(airportCodes.size());
            }
            columnValues.add("'" + airportCodes.get(airportCode).split(" ")[0] + "'");
            columnValues.add("'" + airportCodes.get(nextAirportCode).split(" ")[0] + "'");

            databaseHelper.executeUpdate(databaseHelper.getInsertQuery(tableName, columnValues));
        }
    }

    private void cleanDB() {
        System.out.println("------- Clearing all Previous Details -----");
        List<String> tableNames = new ArrayList<>(Arrays.asList("Trip_Reservation_Details","Reservation_Details","Customer_Details","Trip_Details","Seat_Details","Flight_Details","Airport_Details"));
        tableNames.stream()
                  .forEach(tableName -> databaseHelper.executeUpdate("DELETE FROM "+ tableName));

        String clearAutoCustomer = "Select setval('customer_details_id_seq', 1000 )";
        databaseHelper.executeQuery(clearAutoCustomer);

        String clearAutoReservation = "Select setval('reservation_details_id_seq', 10000 )";
        databaseHelper.executeQuery(clearAutoReservation);
    }

    public void createAirports(){
        System.out.println("------- Now Creating Airports -----");
        String tableName = "Airport_Details";
        for(String airportCode: airportCodes){
            List<String> columnValues = new ArrayList<>();
            columnValues.add("'"+airportCode.split(" ")[0]+"'");
            columnValues.add("'"+airportCode.split(" ")[1]+"'");
            columnValues.add(decimalFormat.format(random.nextFloat()).toString());
            columnValues.add(decimalFormat.format(random.nextFloat()).toString());
            columnValues.add(decimalFormat.format(random.nextFloat()).toString());
            columnValues.add(decimalFormat.format(random.nextFloat()).toString());

            databaseHelper.executeUpdate(databaseHelper.getInsertQuery(tableName, columnValues));
        }
    }


}
