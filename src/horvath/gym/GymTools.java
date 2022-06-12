package horvath.gym;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.*;
import java.util.*;

/**
 * Class which contains other methods
 *
 * @author Marcel Horváth
 */
public class GymTools {
    /**
     * Enum to represent gender
     */
    public enum Gender {M, F}

    /**
     * Saves object to file
     * @param obj object, which will be saved
     * @param path path where to save
     * @throws IOException
     */
    public static void writeObjectToDisk(Object obj, Path path) throws IOException {
        try (ObjectOutputStream objOutStr = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            objOutStr.writeObject(obj);
        }
    }

    /**
     * Loads object from a file
     * @param path path to the object
     * @return loaded object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object objectLoader(Path path) throws IOException, ClassNotFoundException {
        Object obj;
        try (ObjectInputStream objInStr = new ObjectInputStream(new FileInputStream(path.toFile()))){
            obj = objInStr.readObject();
        }
        return obj;
    }

    /**
     * Hashes strings
     *
     * @param input string, which will be hashed
     * @return hashed string in SHA-512 format
     */
    public static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            StringBuilder hashtext = new StringBuilder(no.toString(16));

            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GymException("Password could not be hashed", 106);
        }
    }

    /**
     * Sorts hashmap by first value in natural order
     *
     * @param map - hashmap which has to be sorted
     * @return sorted hashmap by values
     */
    public static HashMap<String, List<Integer>> sortByValueNaturalOrder(HashMap<String, List<Integer>> map) {
        List<Map.Entry<String, List<Integer>>> list = new LinkedList<>(map.entrySet());

        // Sort
        Collections.sort(list, Comparator.comparing(o -> o.getValue().get(0)));

        // put data from sorted list to hashmap
        HashMap<String, List<Integer>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> aa : list) {
            sortedMap.put(aa.getKey(), aa.getValue());
        }
        return sortedMap;
    }

    /**
     * Sorts hashmap by first value in reverse order
     *
     * @param map - hashmap which has to be sorted
     * @return sorted hashmap by values
     */
    public static HashMap<String, List<Integer>> sortByValueReverseOrder(HashMap<String, List<Integer>> map) {
        List<Map.Entry<String, List<Integer>>> list = new LinkedList<>(map.entrySet());

        // Sort
        Collections.sort(list, (o1, o2) -> (o2.getValue().get(0).compareTo(o1.getValue().get(0))));

        // put data from sorted list to hashmap
        HashMap<String, List<Integer>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> aa : list) {
            sortedMap.put(aa.getKey(), aa.getValue());
        }
        return sortedMap;
    }

}
