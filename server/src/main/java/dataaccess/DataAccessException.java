package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    int statusCode;
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
