package com.scheduler.model;

import com.scheduler.error.GetError;
import com.scheduler.error.NothingToProcessException;
import com.scheduler.error.UpdateError;

import java.sql.*;
import java.util.Date;

public class TemporaryOTPJDBC {
    private Connection connection;
    private Statement statement;
    private static String url = "jdbc:mysql://127.0.0.1:3306/challengespe?serverTimezone=Asia/Jakarta";
    private static String username = "root";
    private static String password = "";

    public TemporaryOTPJDBC() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        statement = connection.createStatement();
    }

    public TemporaryOTP getProcess() throws GetError, NothingToProcessException {
        try {
            String sql = "select * " +
                    "from temporaryotp " +
                    "where status = " + SendingStatus.NOT_PROCESSED.ordinal() + " " +
                    "order by id " +
                    "limit 1";
            ResultSet rs = statement.executeQuery(sql);
            TemporaryOTPMapper mapper = new TemporaryOTPMapper(rs);

            if(mapper.getRowCount() == 0) {
                throw new NothingToProcessException();
            }

            return mapper.getTemporaryOTP().get(0);
        } catch (SQLException e) {
            throw new GetError(e);
        }
    }

    public void updateProcess(TemporaryOTP temporaryOTP) throws UpdateError {
        try {
            String update = "update temporaryotp " +
                    "set status = " + SendingStatus.PENDING.ordinal() + " " +
                    "where id = " + temporaryOTP.getId();
            statement.executeUpdate(update);
        } catch (SQLException e) {
            throw new UpdateError(e);
        }
    }

    public void cleanup(TemporaryOTP temporaryOTP) {
        try {
            String update = "update temporaryotp " +
                    "set status = " + SendingStatus.SENT.ordinal() + " " +
                    "where id = " + temporaryOTP.getId();
            statement.executeUpdate(update);
        } catch (SQLException e) {
            System.out.println("Error while Clean Up ID = " + temporaryOTP.getId() + " at = " + new Date());
        }
    }

    public void rollback(TemporaryOTP temporaryOTP) {
        try {
            String update = "update temporaryotp " +
                    "set status = " + SendingStatus.FAILED.ordinal() + " " +
                    "where id = " + temporaryOTP.getId();
            statement.executeUpdate(update);
        } catch (SQLException e) {
            System.out.println("Error while Rollback ID = " + temporaryOTP.getId() + " at = " + new Date());
        }
    }

    public void close() throws SQLException {
        statement.close();
        connection.close();
    }
}
