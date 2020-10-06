package com.scheduler.error;

import java.sql.SQLException;

public class UpdateError extends SQLException {
    public UpdateError(Throwable e) {
        super("Error while updating Database", e);
    }
}
