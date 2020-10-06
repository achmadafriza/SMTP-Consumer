package com.scheduler;

import com.scheduler.error.GetError;
import com.scheduler.error.NothingToProcessException;
import com.scheduler.error.UpdateError;
import com.scheduler.model.TemporaryOTP;
import com.scheduler.model.TemporaryOTPJDBC;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.sql.SQLException;
import java.util.Date;
import java.util.TimerTask;

public class TimedEmail extends TimerTask {
    private Session session;
    private TemporaryOTPJDBC otpDB;
    private String name;

    public TimedEmail(TemporaryOTPJDBC otpDB, Session session, String name) {
        this.session = session;
        this.otpDB = otpDB;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private TemporaryOTP getData() throws GetError, UpdateError, NothingToProcessException {
        TemporaryOTP otp = null;
        try {
            otp = otpDB.getProcess();
            otpDB.updateProcess(otp);

            return otp;
        } catch (UpdateError e) {
            System.out.println(e.getMessage());
            otpDB.cleanup(otp);
            throw e;
        }
    }

    private void sendEmail(TemporaryOTP otp) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(Controller.getUsername()));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(otp.getEmail())
        );
        message.setSubject("OTP for Portal Registration");
        message.setText("Dear " + otp.getUsername() + ","
                + "\n\nThis is your OTP Number. DO NOT give it to anyone else." +
                "\n" + otp.getOtpNumber());

        Transport.send(message);
        System.out.println("Done sending OTP for Username = " + otp.getUsername());
    }

    @Override
    public void run() {
        System.out.println(new Date() + " " + Thread.currentThread().getName()+" "+name+" the task is processing");
        TemporaryOTP otp = null;
        try {
            otp = getData();
            sendEmail(otp);
            otpDB.cleanup(otp);
        } catch (NothingToProcessException e) {
            System.out.println(new Date() + " " + Thread.currentThread().getName()+" "+name+" the task got nothing to process");
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
            otpDB.rollback(otp);
        }
    }
}
