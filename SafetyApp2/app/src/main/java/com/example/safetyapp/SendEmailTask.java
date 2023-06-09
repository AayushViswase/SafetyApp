package com.example.safetyapp;

import android.location.Location;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailTask {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static CompletableFuture<Boolean> sendEmail(String username, String password, String recipientEmail, String subject, Location location, String mobile) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            SmsManager smsManager = SmsManager.getDefault();
            String msg="Please help me...\n" + "Check your mail for location";
            smsManager.sendTextMessage(mobile, null, msg, null, null);


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("aayushviswase008@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            String messageText = "I need help. Please contact me as soon as possible.\n" +
                    "Please help me! I am in danger.\n" +
                    "My current location is:\n" +
                    "Latitude: " + location.getLatitude() + "\n" +
                    "Longitude: " + location.getLongitude()+"\n"+ "URL: " + "https://www.google.com/maps/search/?api=1&query="+location.getLatitude()+","+location.getLongitude()+"&zoom=15";
            message.setText(messageText);
            Transport.send(message);

            result.complete(true);

        } catch (MessagingException e) {
            e.printStackTrace();
            result.complete(false);
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}
