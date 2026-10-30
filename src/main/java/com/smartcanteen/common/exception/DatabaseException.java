package com.smartcanteen.common.exception;

import java.sql.SQLException;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(SQLException cause) {
        super(cause != null ? cause.getMessage() : "Database error occurred", cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause != null ? cause.getMessage() : "Database error occurred", cause);
    }
}
