package com.scheduler;

import com.scheduler.model.TemporaryOTPJDBC;

import javax.mail.*;
import java.sql.SQLException;
import java.util.Properties;

import java.util.Timer;

public class Controller {
    private static final String username = "smtpspeproject@gmail.com";
    private static final String password = "ztrnhttqmtsqieio";
    private static final int MAX_THREADS = 5;

    public static String getUsername() {
        return username;
    }

    public static void main(String[] args) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        Timer timer = new Timer();
        for(int i = 1; i <= MAX_THREADS; i ++) {
            try {
                TemporaryOTPJDBC otpDB = new TemporaryOTPJDBC();
                TimedEmail task = new TimedEmail(otpDB, session, "task"+i);
                System.out.println("Created Thread: task" + i);
                timer.schedule(task, 0, 1000 * 60);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}