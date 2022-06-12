package horvath.app;

import horvath.gym.Balance;
import horvath.gym.Gym;
import horvath.gym.GymException;
import horvath.gym.GymTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;

/**
 * Gym system, which logs users and saves their reservations,records
 *
 * @author Marcel Horváth
 * @version 1.0 31/5/22
 */
public class GymApp {
    private static final Scanner sc = new Scanner(System.in);
    private static boolean isLoggedIn;
    private static Gym gym;

    private static GymPrompt gymPrompt = GymPrompt.getInstance();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(savingHook);
        if (gym == null) {
            try {
                System.out.print(gymPrompt.getLanguages().getString("input_gym_instance"));
                String gymRoot = sc.nextLine();
                File gymF = new File(gymRoot);
                if (gymF.exists() && !gymF.isDirectory()) {
                    gym = (Gym) GymTools.objectLoader(Path.of(gymRoot));
                } else {
                    throw new GymException("There is no gym instance!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new GymException("Gym file could not be loaded", 104);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new GymException("Gym file was not found", 107);
            }
        }
        boolean programEnd;
        do {
            printMenu();
            int choice = loadChoice();
            programEnd = serviceChoice(choice);
        } while (!programEnd);
        System.out.println(gymPrompt.getLanguages().getString("menu_ending"));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("==="+gym.getGymName()+"===");
        if (!isLoggedIn) {
            System.out.println(gymPrompt.getLanguages().getString("menu_registration"));
            System.out.println(gymPrompt.getLanguages().getString("menu_login"));
            System.out.println(gymPrompt.getLanguages().getString("menu_language"));
        } else {
            System.out.println(gymPrompt.getLanguages().getString("menu_choice1"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice2"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice3"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice4"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice5"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice6"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice7"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice8"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice9"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice10"));
            System.out.println(gymPrompt.getLanguages().getString("menu_choice11"));
        }
        System.out.println(gymPrompt.getLanguages().getString("menu_terminate"));
    }

    private static int loadChoice() {
        int choice;
        try {
            choice = sc.nextInt();
        } catch (InputMismatchException ex) {
            choice = -1;
        }
        sc.nextLine();
        return choice;
    }

    private static boolean serviceChoice(int choice) {
        if (!isLoggedIn) {
            switch (choice) {
                case 0:
                    return true;
                case 1:
                    registration();
                    break;
                case 2:
                    logIn();
                    break;
                case 3:
                    changeLanguage();
                    break;
                default:
                    System.out.println(gymPrompt.getLanguages().getString("menu_choice"));
            }
        } else {
            switch (choice) {
                case 0:
                    return true;
                case 1:
                    reservation();
                    break;
                case 2:
                    addBalance();
                    break;
                case 3:
                    balance();
                    break;
                case 4:
                    myReservations();
                    break;
                case 5:
                    addRecord();
                    break;
                case 6:
                    showRecords();
                    break;
                case 7:
                    printRecords();
                    break;
                case 8:
                    changeLanguage();
                    break;
                case 9:
                    deleteReservation();
                    break;
                case 10:
                    printHelp();
                    break;
                case 11:
                    logOff();
                    break;
                default:
                    System.out.println(gymPrompt.getLanguages().getString("menu_choice"));
            }
        }
        return false;
    }

    private static void reservation() {
        try {
            gym.setCurrentFee(gymPrompt.getCountry());
            System.out.println(gymPrompt.getLanguages().getString("input_date"));
            System.out.println(gymPrompt.getLanguages().getString("prompt_price") + gym.getCurrentFee());
            System.out.print(gymPrompt.getLanguages().getString("prompt_day"));
            int day = sc.nextInt();
            System.out.print(gymPrompt.getLanguages().getString("prompt_month"));
            int month = sc.nextInt();
            System.out.print(gymPrompt.getLanguages().getString("prompt_year"));
            int year = sc.nextInt();
            int result = gym.setReservation(gymPrompt.getCountry(), day, month, year);
            switch (result) {
                case 0:
                    System.out.println(gymPrompt.getLanguages().getString("debug_reserved_people") + "(" + gym.getReservations().get(LocalDate.of(year, month, day)).size() + "/" + gym.getMaxPeople() + ")");
                    break;
                case 1:
                    System.out.println(gymPrompt.getLanguages().getString("user_no_funds"));
                    break;
                case 2:
                    System.out.println(gymPrompt.getLanguages().getString("user_logged"));
                    break;
                case 3:
                    System.out.println(gymPrompt.getLanguages().getString("user_tooManyPeople") + "(" + gym.getReservations().get(LocalDate.of(year, month, day)).size() + "/" + gym.getMaxPeople() + ")");
                    break;
            }

        } catch (InputMismatchException | IllegalArgumentException | DateTimeException ims) {
            throw new GymException("Bad input", 103);
        }
    }

    private static void addBalance() {
        try {
            System.out.println(gymPrompt.getLanguages().getString("user_addBalance"));
            System.out.print(gymPrompt.getLanguages().getString("user_input_balance"));
            gym.getUser().setBalance(sc.nextInt());
            System.out.println(gymPrompt.getLanguages().getString("user_balance_added"));
        } catch (InputMismatchException | IllegalArgumentException ims) {
            throw new GymException("Bad input", 103);
        }
    }

    private static void balance() {
        System.out.print(gymPrompt.getLanguages().getString("user_balance"));
        System.out.printf("%.2f", gym.getUser().getBalance());
    }

    private static void myReservations() {
        System.out.println(gymPrompt.getLanguages().getString("user_reservation") + gym.getUser().getReservationsToString());
    }

    private static void addRecord() {
        try {
            System.out.print(gymPrompt.getLanguages().getString("user_input_record"));
            String exercise = sc.nextLine().toUpperCase(Locale.ROOT);
            System.out.print(gymPrompt.getLanguages().getString("user_input_record2"));
            int weight = sc.nextInt();
            gym.getUser().setRecords(exercise, weight);
            System.out.println(gymPrompt.getLanguages().getString("user_record_added"));
        } catch (InputMismatchException | IllegalArgumentException ims) {
            throw new GymException("Bad input", 103);
        }

    }

    private static void showRecords() {
        System.out.print(gymPrompt.getLanguages().getString("prompt_choose_sort"));
        gym.getUser().sortRecords(sc.nextInt());
        System.out.println(gym.getUser().getRecordsToString());
    }

    private static void printRecords() {
        System.out.println(gymPrompt.getLanguages().getString("user_record_saved"));
        gym.getUser().printRecords(gym.getGymRoot());
    }

    private static void printHelp() {
        System.out.println(gymPrompt.getLanguages().getString("prompt_help"));
    }

    private static void changeLanguage() {
        gymPrompt.changeLanguage();
        if (isLoggedIn) {
            gym.getUser().convertBalance(gymPrompt.getCountry(), Balance.USD_CZK_RATE);
        }
    }

    private static void logOff() {
        gym.logOff();
        isLoggedIn = false;
        System.out.println(gymPrompt.getLanguages().getString("debug_saving"));
        System.out.println(gymPrompt.getLanguages().getString("user_logged_out"));
    }

    private static void deleteReservation() {
        try {
            System.out.println(gymPrompt.getLanguages().getString("user_input_record_removed"));
            System.out.print(gymPrompt.getLanguages().getString("prompt_day"));
            int day = sc.nextInt();
            System.out.print(gymPrompt.getLanguages().getString("prompt_month"));
            int month = sc.nextInt();
            System.out.print(gymPrompt.getLanguages().getString("prompt_year"));
            int year = sc.nextInt();
            boolean deleted = gym.deleteReservation(gymPrompt.getCountry(), day, month, year);
            System.out.println(deleted ? gymPrompt.getLanguages().getString("debug_reservation_removed") : gymPrompt.getLanguages().getString("debug_not_reserved"));
        } catch (InputMismatchException | IllegalArgumentException ims) {
            throw new GymException("Bad input", 103);
        }
    }

    private static void logIn() {
        System.out.println(gymPrompt.getLanguages().getString("input_name"));
        String name = sc.nextLine().toLowerCase(Locale.ROOT);
        if (!gym.loadUser(name)) {
            System.out.println(gymPrompt.getLanguages().getString("debug_user_notFound"));
            isLoggedIn = false;
            return;
        }

        System.out.println(gymPrompt.getLanguages().getString("input_password"));
        String password = sc.nextLine();
        isLoggedIn = gym.checkPassword(password);
        if (!isLoggedIn) {
            System.out.println(gymPrompt.getLanguages().getString("debug_wrong_password"));
            return;
        }
    }

    private static void registration() {
        try {
            System.out.println(gymPrompt.getLanguages().getString("input_info"));
            System.out.println(gymPrompt.getLanguages().getString("prompt_name"));
            String name = sc.nextLine();
            System.out.println(gymPrompt.getLanguages().getString("prompt_lastName"));
            String lastName = sc.nextLine();
            System.out.println(gymPrompt.getLanguages().getString("prompt_gender"));
            GymTools.Gender gender = GymTools.Gender.valueOf(sc.nextLine().toUpperCase(Locale.ROOT));
            System.out.println(gymPrompt.getLanguages().getString("prompt_password"));
            String password = sc.nextLine();
            gym.registerUser(name, lastName, gender, password);
            isLoggedIn = true;
        } catch (InputMismatchException | IllegalArgumentException ims) {
            throw new GymException("Bad input", 103);
        }
    }

    /**
     * logs off the user, if the app is terminated
     */
    public static Thread savingHook = new Thread(() -> {
        if (isLoggedIn) {
            System.out.println(gymPrompt.getLanguages().getString("debug_saving"));
            gym.saveUser();
            isLoggedIn = false;
        }
    });
}
