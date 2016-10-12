import java.util.*;

public class TicketBooker {

    public static List<String> customerDetails = new ArrayList<>();
    private int totalNumOfCustomer = 1000;
    public List<String> nationality = new ArrayList<>(Arrays.asList("USA","China","India","Japan","Germany","France","Mexico","Canada"));
    public List<String> livingCity = new ArrayList<>();
    private List<String> customerColumnNames = new ArrayList<>(Arrays.asList("Customer_Name","Living_City","Nationality"));
    private String custTableName = "Customer_Details";
    DatabaseHelper databaseHelper = new DatabaseHelper();
    Random random = new Random();
    List<String> paymentMethods = new ArrayList<>(Arrays.asList("cash","card","check"));
    List<String> businessClassSeed = new ArrayList<>(Arrays.asList("'false'","'false'","'false'","'false'","'false'","'false'","'true'"));
    List<String> keysToAvoidForCreation = new ArrayList<>(Arrays.asList("business_class"));

    public TicketBooker(){
        for(String airport : DBInitializer.airportCodes){
            livingCity.add(airport.split(" ")[0]);
        }
    }

    public void initializeNameArray(){
        Random random = new Random();
        String allCharacters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        char[] allCharArray = allCharacters.toCharArray();

        for (int i = 0; i < totalNumOfCustomer; i++) {
            String custDetails = "";
            for (int j = 0; j < 8; j++) {
                 custDetails += Character.toString(allCharArray[random.nextInt(26)]);
            }

            custDetails += " " + livingCity.get(random.nextInt(livingCity.size())) + " " + nationality.get(random.nextInt(nationality.size()));

            customerDetails.add(custDetails);
        }

    }

    public String getInsertQueryForCustomer(int i){
            List<String> columnValues = new ArrayList<>();
            columnValues.add("'"+customerDetails.get(i).split(" ")[0]+"'");
            columnValues.add("'"+customerDetails.get(i).split(" ")[1]+"'");
            columnValues.add("'"+customerDetails.get(i).split(" ")[2]+"'");
            return new DatabaseHelper().getInsertQueryWithColumns(custTableName,columnValues,customerColumnNames);
    }

    public String getPreparedReservationQuery(){
        List<String> columnNames = new ArrayList<>();
        columnNames.add("Customer_ID");
        columnNames.add("Reservation_Date");
        columnNames.add("Payment_Method");
        columnNames.add("Total_Duration");
        columnNames.add("isRoundTrip");

        List<String> columnValues = new ArrayList<>();
        columnValues.add("?");
        columnValues.add("?");
        columnValues.add("?");
        columnValues.add("?");
        columnValues.add("?");

        return databaseHelper.getInsertQueryWithColumns("Reservation_Details",columnValues,columnNames);

    }

    public void bookATicket(){

        Properties tripFlightProps = getRandomSeatNumber(getRandomTripAndFlight());

        ArrayList<Properties> trips = new ArrayList<>();

        if (tripFlightProps == null)
            return;

        if(checkIfAllSeatsAreBookedForFlight(tripFlightProps))
            return;

        if(checkIfAllSeatsAreBookedForSelectClass(tripFlightProps))
            return;

        List<String> checkIfExistsColumnNames = new ArrayList<>();
        List<String> checkIfExistsColumnValues = new ArrayList<>();

        for (String key : tripFlightProps.keySet().toArray(new String[0]))
        {
            if(!keysToAvoidForCreation.contains(key)) {
                checkIfExistsColumnNames.add(key);
                checkIfExistsColumnValues.add("'" + tripFlightProps.getProperty(key) + "'");
            }
        }

        if (checkSeatIsBooked(checkIfExistsColumnNames, checkIfExistsColumnValues) == 0)
        {
            String customer_ID = addCustomerIfNotExitsReturnID();

            int numberOfStops = random.nextInt(3);

            if(numberOfStops > 0){
                trips = getConnectingFlight(numberOfStops);
                if(trips == null){
                    //bookATicket();
                    return;
                }
            }

            trips.add(tripFlightProps);

            databaseHelper.bookTicket(customer_ID,trips);

//            if(false){
//                Collections.reverse(trips);
//                databaseHelper.bookTicket(customer_ID,trips);
//            }

//            String reservationID = createReservation(customer_ID);
//            if(!reservationID.isEmpty()){
//                String tableName = "Trip_Reservation_Details";
//
//                List<String> columnValues = new ArrayList<>();
//                columnValues.add(tripFlightProps.getProperty("Flight_Number"));
//                columnValues.add(reservationID);
//                columnValues.add("'"+ tripFlightProps.getProperty("Trip_Date") +"'");
//                columnValues.add(tripFlightProps.getProperty("Seat_Number"));
//                columnValues.add("'False'");
//                columnValues.add("'False'");
//
//                databaseHelper.executeUpdate(databaseHelper.getInsertQuery(tableName,columnValues));
//            }
        }else {
//            bookATicket();
//            System.out.println("Conflict arised at :" + tripFlightProps);
        }
    }

    private ArrayList<Properties> getConnectingFlight(int numberOfStops) {
        ArrayList<Properties> trips = new ArrayList<>();
        for (int i = 0; i < numberOfStops; i++) {
            Properties tempProps = getRandomSeatNumber(getRandomTripAndFlight());

            if (tempProps == null)
                return null;

            List<String> checkIfExistsColumnNames = new ArrayList<>();
            List<String> checkIfExistsColumnValues = new ArrayList<>();

            for (String key : tempProps.keySet().toArray(new String[0]))
            {
                if(!keysToAvoidForCreation.contains(key)) {
                    checkIfExistsColumnNames.add(key);
                    checkIfExistsColumnValues.add("'" + tempProps.getProperty(key) + "'");
                }
            }

            if (checkSeatIsBooked(checkIfExistsColumnNames, checkIfExistsColumnValues) == 0){
                trips.add(tempProps);
            }else
                i--;
        }

        return trips;
    }

    private int checkSeatIsBooked(List<String> checkIfExistsColumnNames, List<String> checkIfExistsColumnValues) {
        return databaseHelper.executeCount(databaseHelper.addWhereClause(databaseHelper.getCountSelectQuery("Trip_Reservation_Details"),checkIfExistsColumnNames,checkIfExistsColumnValues));
    }

    private boolean checkIfAllSeatsAreBookedForFlight(Properties tripFlightProps) {
        int totalNumOfSeats = databaseHelper.executeCount(databaseHelper.addWhereClause(databaseHelper.getCountSelectQuery("Seat_Details"),new ArrayList<String>(Arrays.asList("Flight_Number")),new ArrayList<String>(Arrays.asList(tripFlightProps.getProperty("Flight_Number")))));
        int numOfSeatsBooked = checkSeatIsBooked(new ArrayList<String>(Arrays.asList("Flight_Number","Trip_Date")), new ArrayList<String>(Arrays.asList(tripFlightProps.getProperty("Flight_Number"),"'"+tripFlightProps.getProperty("Trip_Date")+"'")));

        if(totalNumOfSeats <= numOfSeatsBooked) {
            System.out.println("## All Seats have been Booked for the flight : "+ tripFlightProps.getProperty("Flight_Number") + " : " + tripFlightProps.getProperty("Trip_Date"));
            System.out.println("Total Num Of seats : "+ totalNumOfSeats + " || No Of seats Booked : " + numOfSeatsBooked);
            System.out.println("Locking the Flight");
            setFlightAsFullyBooked(tripFlightProps);
            return true;
        } else
            return false;
    }

    public void setFlightAsFullyBooked(Properties tripFlightProps) {
        String updateQuery = "UPDATE Trip_Details SET isFullyBooked = 'TRUE' WHERE Flight_Number = "+ tripFlightProps.getProperty("Flight_Number") + " AND Trip_Date = '" + tripFlightProps.getProperty("Trip_Date") +"'";
        databaseHelper.executeUpdate(updateQuery);

        String allFlightsFullQuery = "SELECT COUNT(*) FROM Trip_Details WHERE isFullyBooked = 'false'";
        if(databaseHelper.executeCount(allFlightsFullQuery) == 0){
            System.out.println("All Flights have been Succesfully Booked \n.......... Program Will now Close..........");
            System.exit(0);
        }
    }

    private boolean checkIfAllSeatsAreBookedForSelectClass(Properties tripFlightProps) {

        int totalNumOfSeats = databaseHelper.executeCount(databaseHelper.addWhereClause(databaseHelper.getCountSelectQuery("Seat_Details"),new ArrayList<String>(Arrays.asList("Flight_Number","Business_Class")),new ArrayList<String>(Arrays.asList(tripFlightProps.getProperty("Flight_Number"),tripFlightProps.getProperty("business_class")))));
        int numOfSeatsBooked = databaseHelper.executeCount(databaseHelper.addWhereClause(databaseHelper.getCountSelectQuery("Trip_Reservation_Details") + " Natural JOIN Seat_Details ",new ArrayList<String>(Arrays.asList("Flight_Number","Trip_Date","Business_Class")),new ArrayList<String>(Arrays.asList(tripFlightProps.getProperty("Flight_Number"),"'"+tripFlightProps.getProperty("Trip_Date")+"'",tripFlightProps.getProperty("business_class")))));

        if(totalNumOfSeats <= numOfSeatsBooked) {
            System.out.println("## All Seats have been Booked for the flight : "+ tripFlightProps.getProperty("Flight_Number") +" and is it business Class : "+ tripFlightProps.getProperty("business_class"));
            System.out.println("Total Num Of seats : "+ totalNumOfSeats + " || No Of seats Booked : " + numOfSeatsBooked);
            return true;
        } else
            return false;
    }

    private String createReservation(String customer_id) {
        String preparedStatement = getPreparedReservationQuery();

        List<String> columnValues = new ArrayList<>();
        columnValues.add(customer_id);
        columnValues.add(String.valueOf(System.currentTimeMillis()));
        columnValues.add(paymentMethods.get(random.nextInt(paymentMethods.size())));
        columnValues.add(Integer.toString(random.nextInt(10)+10));
        columnValues.add("'"+Boolean.toString(random.nextBoolean())+"'");

        return databaseHelper.executePreparedStatementReturningID(preparedStatement,columnValues);
    }

    private String addCustomerIfNotExitsReturnID() {

        List<String> whereCustName = new ArrayList<String>(Arrays.asList("Customer_Name"));
        int randomCustomerNumber = random.nextInt(customerDetails.size());
        List<String> whereCustValue = new ArrayList<String>(Arrays.asList("'"+customerDetails.get(randomCustomerNumber).split(" ")[0]+ "'"));

        if((databaseHelper.executeCount(databaseHelper.addWhereClause(databaseHelper.getCountSelectQuery(custTableName),whereCustName,whereCustValue))) == 0){
            String query = getInsertQueryForCustomer(randomCustomerNumber);
            databaseHelper.executeUpdate(query);
        }

        String getCustomerID = " SELECT CUSTOMER_ID FROM CUSTOMER_DETAILS WHERE CUSTOMER_NAME = " + whereCustValue.get(0);

        return databaseHelper.executeCustomerID(getCustomerID);
    }

    private Properties getRandomSeatNumber(Properties tripFlightProps) {
        if(tripFlightProps == null){
            return null;
        }

        String tableName = "Seat_Details";
        List<String> columnNames = new ArrayList<>(Arrays.asList("Seat_Number"));
        List<String> whereColumnNames = new ArrayList<>();
        List<String> whereColumnValues = new ArrayList<>();

        tripFlightProps.setProperty("business_class",businessClassSeed.get(random.nextInt(businessClassSeed.size())));
//        for (String key : tripFlightProps.keySet().toArray(new String[0]))
//        {
//            whereColumnNames.add(key);
//            whereColumnValues.add("'"+ tripFlightProps.getProperty(key) + "'");
//        }

        whereColumnNames.add("flight_number");
        whereColumnValues.add("'"+tripFlightProps.getProperty("Flight_Number")+"'");
        whereColumnNames.add("business_class");
        whereColumnValues.add(tripFlightProps.getProperty("business_class"));

        Properties props = databaseHelper.selectRandomDetailsWithWhereClause(tableName,columnNames,whereColumnNames,whereColumnValues,tripFlightProps);
        if(props == null){
            //bookATicket();
            return null;
        }

        tripFlightProps.setProperty("Seat_Number",props.getProperty("Seat_Number"));

        return tripFlightProps;
    }

    private Properties getRandomTripAndFlight() {
        String tableName = "Trip_Details";
        List<String> columnNames = new ArrayList<>(Arrays.asList("Trip_Date","Flight_Number"));

        Properties props = databaseHelper.selectRandomDetails(tableName,columnNames);
        if(props == null){
            //bookATicket();
            return null;
        }

        return props;
    }
}
