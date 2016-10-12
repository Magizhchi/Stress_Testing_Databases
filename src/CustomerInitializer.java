//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//
//public class CustomerInitializer {
//
//    public static List<String> customerDetails = new ArrayList<>();
//    private int totalNumOfCustomer = 25;
//    public List<String> livingCity = new ArrayList<>();
//    private List<String> customerColumnNames = new ArrayList<>(Arrays.asList("Customer_Name","Living_City","Nationality"));
//    private String custTableName = "Customer_Details";
//    public List<String> nationality = new ArrayList<>(Arrays.asList("USA","China","India","Japan","Germany","France","Mexico","Canada"));
//    Random random = new Random();
//
//    public CustomerInitializer(){
//        for(String airport : DBInitializer.airportCodes){
//            livingCity.add(airport.split(" ")[0]);
//        }
//    }
//
//    public void initializeNameArray(){
//        Random random = new Random();
//        String allCharacters = "QWERTYUIOPASDFGHJKLZXCVBNM";
//        char[] allCharArray = allCharacters.toCharArray();
//
//        for (int i = 0; i < totalNumOfCustomer; i++) {
//            String custDetails = "";
//            for (int j = 0; j < 8; j++) {
//                custDetails += Character.toString(allCharArray[random.nextInt(26)]);
//            }
//
//            custDetails += " " + livingCity.get(random.nextInt(livingCity.size())) + " " + nationality.get(random.nextInt(nationality.size()));
//
//            customerDetails.add(custDetails);
//        }
//
//    }
//}
