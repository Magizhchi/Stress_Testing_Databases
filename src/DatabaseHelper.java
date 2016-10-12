import org.postgresql.util.PSQLException;

import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseHelper {

    public static final String reservationQuery = new TicketBooker().getPreparedReservationQuery();

    public static final String tripReservationQuery = " INSERT INTO Trip_Reservation_Details ( Flight_Number,Reservation_ID,Trip_Date,Seat_Number,Trip_Completed,Ticket_Cancelled) VALUES (?,?,?,?,?,?)";

    Random random = new Random();

    public Connection getConnection(){
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");
            Properties credentials = new Properties();
            credentials.put("user", "*****");
            credentials.put("password", "*****");

            Properties localCredential = new Properties();
            localCredential.put("user","******");
            localCredential.put("password","******");
            con = DriverManager.getConnection("jdbc:postgresql://***.***.***.***/******", credentials);
//            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dbhw2", localCredential);

        } catch(PSQLException e) {
            System.out.println(e.getMessage());
        }catch (SQLException e) {
            System.out.println("Error Connecting to the database");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find the given Class");
            e.printStackTrace();
        }

        return con;
    }

    public String getSelectQuery(String tableName, List<String> columnNames){
        return "SELECT " + columnNames.toString().replace("[", "").replace("]", "") + " FROM " +tableName;
    }

    public String getCountSelectQuery(String tableName){
        return "SELECT COUNT(*) FROM " + tableName + " AS totalCount";
    }

    public String addWhereClause (String query, List<String> columnNames, List<String> columnValues){
        query += " WHERE ";
        int i = 0;
        for(String columnvalue :columnValues){
            query += columnNames.get(i) + " = " + columnValues.get(i) + " AND ";
            i++;
        }

        return query.substring(0,query.length() - 5);
    }

    public Properties selectRandomDetails(String tableName,List<String> columnNames) {
        Properties props = new Properties();

        int executeCount = executeCount("SELECT COUNT(*) FROM (SELECT Trip_Date, Flight_Number FROM Trip_Details WHERE isFullyBooked = 'false') AS totalCount");
        if(executeCount == 0)
            return null;
        int randomlySelectedRow = random.nextInt(executeCount) + 2;

        Connection con = getConnection();
        Statement stmt = null;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(addWhereClause(getSelectQuery(tableName,columnNames),new ArrayList<String>(Arrays.asList("isFullyBooked")),new ArrayList<String>(Arrays.asList("false"))));
            if (!rs.next() ) {
                return null;
            }
            for (int i = 2; i < randomlySelectedRow; i++)
                rs.next();

            props.setProperty(columnNames.get(0),rs.getString(columnNames.get(0)));
            props.setProperty(columnNames.get(1),rs.getString(columnNames.get(1)));



        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }

        return props;
    }

    public int executeCount(String query) {
        int count = 0;
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            count = rs.getInt("COUNT");
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }

        return count;
    }

    public String executeCustomerID(String query) {
        String count = "";
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            count = rs.getString("CUSTOMER_ID");
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }

        return count;
    }

    public static String getCountDistinctQuery(String tableName, List<String> columnNames) {
        String query = "SELECT COUNT(*) FROM (SELECT ";// FROM ?) AS totalCount";

        if (columnNames.size() > 0)
            query += columnNames.toString().replace("[", "").replace("]", "");
        else
            query += "*";

        query += " FROM " + tableName + ") AS totalCount";


        return query;
    }

    public String getInsertQuery(String tableName,List<String> columnValues){
        String query = "INSERT INTO "+ tableName + " VALUES(";

        query += columnValues.toString().replace("[", "").replace("]", "");

        return query + ")";
    }

    public String getInsertQueryWithColumns(String tableName,List<String> columnValues,List<String> columnNames){
        String query = "INSERT INTO "+ tableName + " (";

        query += columnNames.toString().replace("[", "").replace("]", "");

        query += ") VALUES (" + columnValues.toString().replace("[", "").replace("]", "") + ")";

        return query;
    }

    public int executeUpdate(String sql){
        Connection con = getConnection();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }
        return 0;
    }

    public ResultSet executeQuery(String sql){
        Connection con = getConnection();
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = con.createStatement();
            return stmt.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }
        return resultSet;
    }

    public Properties selectRandomDetailsWithWhereClause(String tableName, List<String> columnNames, List<String> whereColumnNames, List<String> whereColumnValues, Properties tripFlightProps) {
        Properties props = new Properties();

        int executeCount = executeCount(addWhereClause(getCountSelectQuery(tableName),whereColumnNames,whereColumnValues) + " and Seat_Number Not In (Select Seat_number From Trip_Reservation_Details where Flight_number = "+tripFlightProps.getProperty("Flight_Number")+" and Trip_Date = '"+tripFlightProps.getProperty("Trip_Date")+"')");
        if( executeCount == 0)
            return null;

        int randomlySelectedRow = random.nextInt(executeCount) + 2;

        Connection con = getConnection();
        Statement stmt = null;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(addWhereClause(getSelectQuery(tableName,columnNames),whereColumnNames,whereColumnValues)+ " and Seat_Number Not In (Select Seat_number From Trip_Reservation_Details where Flight_number = "+tripFlightProps.getProperty("Flight_Number")+" and Trip_Date = '"+tripFlightProps.getProperty("Trip_Date")+"')");
            if (!rs.next() ) {
                return null;
            }
            for (int i = 3; i < randomlySelectedRow; i++)
                rs.next();

                props.setProperty(columnNames.get(0),rs.getString(columnNames.get(0)));



        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }

        return props;
    }

    private void closeAllConnections(Connection con, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public String executePreparedStatementReturningID(String preparedStatement, List<String> columnValues) {
        Connection con = null;
        PreparedStatement stmt = null;
        try{
            con = getConnection();
            stmt = con.prepareStatement(preparedStatement,Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, Integer.parseInt(columnValues.get(0)));
            stmt.setDate(2,new Date(Long.parseLong(columnValues.get(1))));
            stmt.setString(3,columnValues.get(2));
            stmt.setInt(4, Integer.parseInt(columnValues.get(3)));
            stmt.setBoolean(5,new Boolean(columnValues.get(4)));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getString(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(con, stmt);
        }

        return "";
    }

    public synchronized void bookTicket(String customer_id, ArrayList<Properties> trips) {

        List<String> paymentMethods = new ArrayList<>(Arrays.asList("cash","card","check"));

        List<String> columnValuesReservation = new ArrayList<>();
        columnValuesReservation.add(customer_id);
        columnValuesReservation.add(String.valueOf(System.currentTimeMillis()));
        columnValuesReservation.add(paymentMethods.get(random.nextInt(paymentMethods.size())));
        columnValuesReservation.add(Integer.toString(random.nextInt(10)+10));
        columnValuesReservation.add("'"+Boolean.toString(random.nextBoolean())+"'");

        Connection con = getConnection();
        PreparedStatement preparedStatementReservation = null;
        PreparedStatement preparedStatementTripReservation = null;
        try {
            con.setAutoCommit(false);

            preparedStatementReservation = con.prepareStatement(reservationQuery,Statement.RETURN_GENERATED_KEYS);
            preparedStatementReservation.setInt(1, Integer.parseInt(columnValuesReservation.get(0)));
            preparedStatementReservation.setDate(2,new Date(Long.parseLong(columnValuesReservation.get(1))));
            preparedStatementReservation.setString(3,columnValuesReservation.get(2));
            preparedStatementReservation.setInt(4, Integer.parseInt(columnValuesReservation.get(3)));
            preparedStatementReservation.setBoolean(5,new Boolean(columnValuesReservation.get(4)));

            int affectedRows = preparedStatementReservation.executeUpdate();

            String reservationID = "";

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = preparedStatementReservation.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservationID = generatedKeys.getString(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            if(reservationID.isEmpty())
                throw new RuntimeException("Unable to retrieve the Reservation ID Hence Rolling Back Changes");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            for(Properties tripFlightProps : trips) {
                preparedStatementTripReservation = con.prepareStatement(tripReservationQuery);
                preparedStatementTripReservation.setInt(1, Integer.parseInt(tripFlightProps.getProperty("Flight_Number")));
                preparedStatementTripReservation.setInt(2, Integer.parseInt(reservationID));
                preparedStatementTripReservation.setDate(3, new Date(format.parse(tripFlightProps.getProperty("Trip_Date")).getTime()));
                preparedStatementTripReservation.setInt(4, Integer.parseInt(tripFlightProps.getProperty("Seat_Number")));
                preparedStatementTripReservation.setBoolean(5, new Boolean(false));
                preparedStatementTripReservation.setBoolean(6, new Boolean(false));

                preparedStatementTripReservation.executeUpdate();
//                System.out.println("Successfully booked ticket for FligntNumber:"+tripFlightProps.getProperty("Flight_Number")+"  TripDate:"+tripFlightProps.getProperty("Trip_Date")+"  SeatNumber:"+tripFlightProps.getProperty("Seat_Number"));
            }

            con.commit();

            for(Properties tripFlightProps : trips){
                SynchronizedCounter.getSynchronizedCounter().incrementTripsCounter();
            }

            SynchronizedCounter.getSynchronizedCounter().incrementTicketCounter();


        } catch (SQLException | ParseException | RuntimeException e) {
            System.out.println("Conflict arised For the ticket : "+ trips);
            System.out.println("Rolling Back All Changes......");
//            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("OOPs unable to rollback reservation : "+ trips);
            }
        } finally {
            if(preparedStatementTripReservation!=null || con!=null ||preparedStatementReservation!=null ){
                try {
                    preparedStatementTripReservation.close();
                    preparedStatementReservation.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
