package edu.neu.foodrescue.util;

/** Unchecked wrapper for SQL errors so the UI can catch one exception type and show a message. */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
