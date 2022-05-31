package horvath.gym;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Main class to list/add/remove reservations, log/register users
 * @author Marcel Horváth
 */
public class Gym {
    private final int MAX_PEOPLE = 5;
    private HashMap<LocalDate, List<UUID>> reservations;
    private final int feeCz = 50;
    private final int feeUs = 2;
    private int fee;
    private User user = null;

    /**
     * Creates instance of gym
     * Loads already saved reservations, if there are any
     */
    public Gym (){
        try {
            File f = new File("data/reservations.dat");
            if(f.exists() && !f.isDirectory()) {
                reservations = (HashMap<LocalDate, List<UUID>>) GymTools.objectLoader("data/reservations.dat");
            }
            else {
                reservations = new HashMap<>();
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new GymException("Reservation file error", 104);
        }
    }
    public User getUser() {
        return user;
    }
    public int getFee() {
        return fee;
    }

    public int getFeeCZ() {
        return feeCz;
    }

    public int getFeeUs() {
        return feeUs;
    }

    public int getMAX_PEOPLE() {
        return MAX_PEOPLE;
    }

    public HashMap<LocalDate, List<UUID>> getReservations() {
        return reservations;
    }

    /**
     * Registers user and saves him to database
     * @param name users name
     * @param lastName users last name
     * @param gender users gender
     * @param password users password
     */
    public void registerUser(String name, String lastName, GymTools.Gender gender, String password) {
        this.user = new User(name,lastName,gender,password);
        try{
            GymTools.writeObjectToDisk(user, "" + "data/users/"+user.getName().toLowerCase() + user.getLastName().toLowerCase()+".acc");
        }
        catch(IOException ioe){
            throw new GymException("User not found", 102);
        }
    }

    /**
     * Logs in the user, checks if the password is right
     * @param name users name
     * @param password users password
     * @return boolean is logged in?
     */
    public boolean logInUser(String name, String password){
        try{
            user = (User) GymTools.objectLoader("data/users/"+name.replaceAll("\\s+","")+".acc");
            if(!GymTools.encryptThisString(password).equals(user.getPassword())){
                user = null;
                return false;
            }
            return true;
        }
        catch(IOException | ClassNotFoundException ioe){
            throw new GymException("User not found", 102);
        }
    }

    /**
     * Logs off the user, saves data
     */
    public void logOff(){
        try {
            GymTools.writeObjectToDisk(user, "data/users/"+user.getName().toLowerCase(Locale.ROOT) + user.getLastName().toLowerCase(Locale.ROOT)+".acc");
            GymTools.writeObjectToDisk(reservations, "data/reservations.dat");

        } catch (IOException e) {
            throw new GymException("Files could not be saved",105);
        }
        user = null;
    }

    /**
     * Sets a reservation for an inputted date,
     * checks if user has money and if there are not a lot of people reserved for one date
     * @param country string of country to decide, which fee should be used
     * @param day day in a date
     * @param month month in a date
     * @param year year in a date
     * @return result of reservation: 0 - reservation was set, 1 - Not enough fund, 2 - Already reserved, 3 - Too many people reserved
     */
    public int setReservation(String country, int day, int month, int year){
        setFee(country);
        if(user.getBalance() < fee){
            return 1;
        }
        LocalDate datum = LocalDate.of(year,month,day);
        if(!reservations.containsKey(datum)){
            reservations.put(datum, new ArrayList<>());
        }

        if(user.getUserReservations().contains(datum)){
            return 2;
        }

        if(reservations.get(datum).size() >= MAX_PEOPLE){
            return 3;
        }

        System.out.println(reservations.get(datum).size()-1);
        reservations.get(datum).add(user.getId());
        user.setReservations(datum,fee);
        System.out.println(reservations.get(datum).size()-1);
        return 0;
    }


    /**
     * Sets fee for different gender and converts the currency
     * @param country string of country to decide, which fee should be used
     */
    public void setFee(String country) {
        if(user.getGender() == GymTools.Gender.M){
            if(country.equals("CZ")){
                fee = feeCz;
            }
            else if(country.equals("US")){
                fee = feeUs;
            }
        }
        else if(user.getGender() == GymTools.Gender.F){
            if(country.equals("CZ")){
                fee = (int) Math.floor(feeCz *0.5f);
            }
            else if(country.equals("US")){
                fee = (int) Math.floor(feeUs *0.5f);
            }
        }
    }

    /**
     * Deletes reservation from a list, gives user back their money
     * @param country string of country to decide, which fee should be used
     * @param day day in a date
     * @param month month in a date
     * @param year year in a date
     * @return boolean is reservation deleted?
     */
    public boolean deleteReservation(String country, int day, int month, int year){
        setFee(country);
        LocalDate datum = LocalDate.of(year,month,day);
        if(!reservations.containsKey(datum)){
            return false;
        }
        reservations.get(datum).remove(user.getId());
        user.removeReservation(datum, fee);
        return true;
    }

}
