package com.scheduler.error;

import java.sql.SQLException;

public class GetError extends SQLException {
    public GetError(Throwable e) {
        super("Error while GET Database", e);
    }
}
