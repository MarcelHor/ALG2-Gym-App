package horvath.gym;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

/**
 * Main class to list/add/remove reservations, log/register users
 *
 * @author Marcel Horváth
 */
public class Gym implements Serializable {
    public static final String GYM_SUFFIX = ".gym";
    public static final String RESERVATION_FILE = "reservation.dat";
    public static final String ACCOUNT_SUFFIX = ".acc";
    public static final String USERS_DIR = "users";
    private final String gymName;
    private final int maxPeople;
    private final File gymRoot;
    private Balance currentFee;
    private HashMap<LocalDate, List<UUID>> reservations;
    private final Balance feeCz;
    private final Balance feeUs;
    private User user = null;

    /**
     * Creates instance of gym
     * Loads already saved reservations, if there are any
     */
    public Gym(String gymName, int maxPeople, Path gymRoot, float feeCz, float feeUs) {
        this.gymName = gymName;
        this.feeCz = new Balance(Currency.getInstance("CZK"), feeCz);
        this.feeUs = new Balance(Currency.getInstance("USD"), feeUs);
        this.maxPeople = maxPeople;
        this.gymRoot = gymRoot.toFile();
        try {
            File userDir = new File(this.gymRoot + "/" + USERS_DIR);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }
            File reservationFile = new File(this.gymRoot + "/" + RESERVATION_FILE);
            if (reservationFile.exists() && !reservationFile.isDirectory()) {
                reservations = (HashMap<LocalDate, List<UUID>>) GymTools.objectLoader(Path.of(String.valueOf(gymRoot), RESERVATION_FILE));
            } else {
                reservations = new HashMap<>();
            }
            GymTools.writeObjectToDisk(this,Path.of(String.valueOf(gymRoot), this.gymName +GYM_SUFFIX));
        } catch (IOException e) {
            e.printStackTrace();
            throw new GymException("file could not be loaded", 104);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new GymException("file was not found",107);
        }
    }

    public String getGymName() {
        return gymName;
    }

    public User getUser() {
        return user;
    }

    public Balance getCurrentFee() {
        return currentFee;
    }

    public float getFeeCZ() {
        return feeCz.amount();
    }

    public float getFeeUs() {
        return feeUs.amount();
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public HashMap<LocalDate, List<UUID>> getReservations() {
        return reservations;
    }

    public File getGymRoot() {
        return gymRoot;
    }

    /**
     * Registers user and saves him to database
     * @param name     users name
     * @param lastName users last name
     * @param gender   users gender
     * @param password users password
     */
    public void registerUser(String name, String lastName, GymTools.Gender gender, String password) {
        this.user = new User(name, lastName, gender, password);
        try {
            GymTools.writeObjectToDisk(user,Path.of(String.valueOf(gymRoot),USERS_DIR,user.getName().toLowerCase(Locale.ROOT)+user.getLastName().toLowerCase(Locale.ROOT)+ACCOUNT_SUFFIX));
        } catch (IOException e) {
            e.printStackTrace();
            throw new GymException("User not found", 102);
        }
    }

    /**
     * Loads in the users file
     *
     * @param name users/file name
     * @return boolean: was users file found?
     */
    public boolean loadUser(String name) {
        try {
            user = (User) GymTools.objectLoader(Path.of(String.valueOf(gymRoot), USERS_DIR, name.replaceAll("\\s+", "") + ACCOUNT_SUFFIX));
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Hashes string input and then compares it to the users password
     *
     * @param password inputted string
     * @return boolean: is the password correct?
     */
    public boolean checkPassword(String password) {
        if (!GymTools.encryptThisString(password).equals(user.getPassword())) {
            user = null;
            return false;
        }
        return true;
    }

    /**
     * Logs off the user, saves data
     */
    public void logOff() {
        try {
            GymTools.writeObjectToDisk(user,Path.of(String.valueOf(gymRoot),USERS_DIR,user.getName().toLowerCase(Locale.ROOT)+user.getLastName().toLowerCase(Locale.ROOT)+ACCOUNT_SUFFIX));
            GymTools.writeObjectToDisk(reservations, Path.of(String.valueOf(gymRoot),RESERVATION_FILE));

        } catch (IOException e) {
            throw new GymException("Files could not be saved", 105);
        }
        user = null;
    }

    public void saveUser() {
        try {
            GymTools.writeObjectToDisk(user,Path.of(String.valueOf(gymRoot),USERS_DIR,user.getName().toLowerCase(Locale.ROOT)+user.getLastName().toLowerCase(Locale.ROOT)+ACCOUNT_SUFFIX));
        } catch (IOException e) {
            throw new GymException("User file could not be saved", 105);
        }
    }

    public void saveReservation() {
        try {
            GymTools.writeObjectToDisk(reservations, Path.of(String.valueOf(gymRoot),RESERVATION_FILE));

        } catch (IOException e) {
            throw new GymException("Reservation file could not be saved", 105);
        }
    }

    /**
     * Sets a reservation for an inputted date,
     * checks if user has money and if there are not a lot of people reserved for one date
     *
     * @param country string of country to decide, which fee should be used
     * @param day     day in a date
     * @param month   month in a date
     * @param year    year in a date
     * @return result of reservation: 0 - reservation was set, 1 - Not enough fund, 2 - Already reserved, 3 - Too many people reserved
     */
    public int setReservation(String country, int day, int month, int year) {
        setCurrentFee(country);
        if (user.getBalance() < currentFee.amount()) {
            return 1;
        }
        LocalDate datum = LocalDate.of(year, month, day);
        if (!reservations.containsKey(datum)) {
            reservations.put(datum, new ArrayList<>());
        }

        if (user.getUserReservations().contains(datum)) {
            return 2;
        }

        if (reservations.get(datum).size() >= maxPeople) {
            return 3;
        }
        reservations.get(datum).add(user.getId());
        user.setReservations(datum, currentFee.amount());
        saveReservation();
        return 0;
    }


    /**
     * Sets current fee
     * @param country string of country to decide, which fee should be used
     */
    public void setCurrentFee(String country) {
        boolean man = user.getGender() == GymTools.Gender.M;
        if (country.equals("CZ")) {
            setFee(man, feeCz);
        } else if (country.equals("US")) {
            setFee(man, feeUs);
        }
    }

    /**
     * Converts fee
     * @param man checks the gender of user
     * @param balance balance to be converted
     */
    public void setFee(boolean man, Balance balance) {
        if (man) currentFee = balance;
        else currentFee = new Balance(balance.currency(), balance.amount() * .5f);
    }

    /**
     * Deletes reservation from a list, gives user back their money
     * @param country string of country to decide, which fee should be used
     * @param day     day in a date
     * @param month   month in a date
     * @param year    year in a date
     * @return boolean is reservation deleted?
     */
    public boolean deleteReservation(String country, int day, int month, int year) {
        setCurrentFee(country);
        LocalDate datum = LocalDate.of(year, month, day);
        if (!reservations.containsKey(datum)) {
            return false;
        }
        reservations.get(datum).remove(user.getId());
        user.removeReservation(datum, currentFee.amount());
        saveReservation();
        return true;
    }

}
