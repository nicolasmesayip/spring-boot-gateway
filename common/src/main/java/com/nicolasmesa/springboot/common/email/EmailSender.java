package com.nicolasmesa.springboot.common.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private final MimeMessage message;

    public EmailSender(EmailConfiguration emailConfiguration) throws MessagingException {

        Properties properties = new Properties();

        properties.put("mail.smtp.host", emailConfiguration.host());
        properties.put("mail.smtp.port", emailConfiguration.port());
        properties.put("mail.smtp.auth", emailConfiguration.auth());
        properties.put("mail.smtp.starttls.enable", emailConfiguration.enableTLS());

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfiguration.senderEmail(), emailConfiguration.senderPassword());
            }
        });

        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailConfiguration.senderEmail()));
    }

    public void sendEmail(String recipient, String subject, String messageBody) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setText(messageBody);
        Transport.send(message);
    }
}
