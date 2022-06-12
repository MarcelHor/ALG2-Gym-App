package horvath.app;
import horvath.gym.Gym;
import horvath.gym.GymException;
import horvath.gym.GymTools;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Creates new instances of Gym
 * @author Marcel Horváth
 */
public class CreateGym {
    private static final Scanner sc = new Scanner(System.in);
    private static Gym gym;
    private static GymPrompt gymPrompt = GymPrompt.getInstance();

    public static void main(String[] args) {
        System.out.print(gymPrompt.getLanguages().getString("input_gym_name"));
        String gymName = sc.nextLine();

        System.out.print(gymPrompt.getLanguages().getString("input_gym_max"));
        int maxPeople = sc.nextInt();

        System.out.println(gymPrompt.getLanguages().getString("input_gym_feecz"));
        float feeCzk = sc.nextFloat();

        System.out.println(gymPrompt.getLanguages().getString("input_gym_feeus"));
        float feeUs = sc.nextFloat();

        sc.nextLine();
        System.out.print(gymPrompt.getLanguages().getString("input_gym_path"));
        String path = sc.nextLine();

        gym = new Gym(gymName, maxPeople, Path.of(path),feeCzk,feeUs);
    }

}
