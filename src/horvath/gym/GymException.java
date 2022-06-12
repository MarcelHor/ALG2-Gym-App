package horvath.gym;
/**
 * Class for throwing exceptions
 * <p>
 * Type of errors
 * <ul>
 * <li> 100 - Error not specified.
 * <li> 102 - User file was not found, bad input or account does not exits.
 * <li> 103 - Bad input.
 * <li> 104 - File could not be loaded.
 * <li> 105 - Files could not be saved.
 * <li> 106 - Password could not be hashed.
 * <li> 107 - File was not found.
 * </ul>
 * @author Marcel Horváth
 */
public class GymException extends RuntimeException {
    private final int errCode;
    private final String dataInfo;

    public GymException(String message) {
        this(message, 100, "");
    }

    /**
     * Creates object of exception with information about the error and error code
     * @param message text information of the error
     * @param errCode error code
     */
    public GymException(String message, int errCode) {
        this(message, errCode, "");
    }

    /**
     * Creates object of exception with information about the error and error code
     * @param message text information of the error
     * @param errCode error code
     * @param dataInfo better info
     */
    public GymException(String message, int errCode, String dataInfo) {
        super(message);
        this.errCode = errCode;
        this.dataInfo = dataInfo;
    }

    /**
     * Gives the error code
     * @return error code
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     * Gives better info about the error
     * @return better information about the error
     */
    public String getDataInfo() {
        return dataInfo;
    }
}
