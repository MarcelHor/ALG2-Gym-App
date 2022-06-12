package horvath.gym;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Class, which represents user. Includes their information
 * @author Marcel Horváth
 */
public class User implements Serializable {
    public static final String RECORDS_DIR = "records";
    public static final String RECORD_SUFFIX = "_record.txt";
    private final String name;
    private final String lastName;
    private final UUID id;
    private final GymTools.Gender gender;
    private List<LocalDate> userReservations = new ArrayList<>();
    private HashMap<String, List<Integer>> records = new HashMap<String,  List<Integer>>();
    private final String password;
    private double balance;

    public User(String name, String lastName, GymTools.Gender gender, String password){
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
        this.id = UUID.randomUUID();
        this.password = GymTools.encryptThisString(password);

    }

    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public GymTools.Gender getGender() {
        return gender;
    }
    public String getPassword() {
        return password;
    }
    public UUID getId() {
        return id;
    }
    public double getBalance() {
        return balance;
    }

    public List getUserReservations() {
        return userReservations;
    }
    public String getReservationsToString() {
        return userReservations.toString().replaceAll("\\[|\\]|\"","");
    }

    public HashMap<String, List<Integer>> getRecords() {
        return records;
    }

    /**
     * Creates new key and values for the inputted record, saves records
     * @param exercise string of record
     * @param weight weight of the exercise
     */
    public void setRecords(String exercise, Integer weight) {
        if(!records.containsKey(exercise)){
            records.put(exercise, new ArrayList<>());
        }
        records.get(exercise).add(weight);
    }


    /**
     * Formats records and returns them
     * @return string of formatted records
     */
    public String getRecordsToString() {
        StringBuffer Zaznamy = new StringBuffer();
        for (Map.Entry<String, List<Integer>> map : getRecords().entrySet()) {
            Zaznamy.append((map.getKey() + " = " + map.getValue()).replaceAll("\\[|\\]|\"","") + System.lineSeparator());
            System.out.println();
        }
        return Zaznamy.toString();
    }

    /**
     * Sorts Arraylist in natural or reverse order
     * @param choice int to control, which sorting will be used 1 - reverse order, 2 - natural order
     */
    public void sortRecords(int choice){
        switch(choice) {
            case 1:
                for (List<Integer> list : records.values()) {
                    list.sort(Comparator.reverseOrder());
                }
                records = GymTools.sortByValueReverseOrder(records);
                break;
            case 2:
                for (List<Integer> list : records.values()) {
                    list.sort(Comparator.naturalOrder());
                }
                records = GymTools.sortByValueNaturalOrder(records);
                break;
        }
    }

    /**
     * Prints the record file
     * Creates .txt file in data/records/"+name+"_record.txt
     */
    public void printRecords(File dir) {
        File recordsDir = Path.of(dir.getPath(), RECORDS_DIR).toFile();
        if (!recordsDir.exists()) {
            recordsDir.mkdirs();
        }

        try (PrintWriter out = new PrintWriter(Path.of(dir.getPath(), RECORDS_DIR, name+RECORD_SUFFIX).toFile())) {
            out.println(getRecordsToString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * Converts the currency
     * @param country string of country to decide, which fee should be used
     * @rate rate exchange rate
     */
    public void convertBalance(String country, float rate){
        if(country.equals("CZ")){
            balance *=rate;
        }
        else if(country.equals("US")){
            balance *=1/rate;
        }
    }
    /**
     * Adds amount to the user/balance
     * @param balance amount to be added
     */
    public void setBalance(int balance) {
        this.balance += balance;
    }

    /**
     * Adds reservation, and deducts balance
     * @param datum reservation date
     * @param fee amount, which will be deducted from the balance
     */
    public void setReservations(LocalDate datum, double fee){
        balance -= fee;
        userReservations.add(datum);
    }

    /**
     * Removes reservation, and adds balance to the user
     * @param datum reservation date
     * @param fee amount, which will be added to the balance
     */
    public void removeReservation(LocalDate datum, double fee){
        balance += fee;
        userReservations.remove(datum);
    }


    /**
     * Formats user class to string
     * @return formated string of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", id=" + id +
                ", gender=" + gender +
                ", userReservations=" + userReservations +
                ", records=" + records +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                '}';
    }
}
