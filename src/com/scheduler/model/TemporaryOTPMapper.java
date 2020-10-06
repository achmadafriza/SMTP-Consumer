package com.scheduler.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemporaryOTPMapper {
    private int rowCount = 0;
    private List<TemporaryOTP> temporaryOTP;

    public TemporaryOTPMapper(ResultSet resultSet) throws SQLException {
        temporaryOTP = new ArrayList<>();
        while (resultSet.next()) {
            TemporaryOTP otp = new TemporaryOTP();
            otp.setId(resultSet.getInt("id"));
            otp.setUsername(resultSet.getString("username"));
            otp.setEmail(resultSet.getString("email"));
            otp.setOtpNumber(resultSet.getString("otpNumber"));
            otp.setStatus(resultSet.getInt("status"));

            temporaryOTP.add(otp);
            rowCount++;
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<TemporaryOTP> getTemporaryOTP() {
        return temporaryOTP;
    }
}
