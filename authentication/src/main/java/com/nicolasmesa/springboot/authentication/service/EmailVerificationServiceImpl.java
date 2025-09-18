package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.config.EmailConfiguration;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.repository.EmailVerificationRepository;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final EmailConfiguration emailConfiguration;
    private final EmailVerificationRepository emailVerificationRepository;
    private final Session session;

    public EmailVerificationServiceImpl(EmailVerificationRepository emailVerificationRepository, EmailConfiguration emailConfiguration) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailConfiguration = emailConfiguration;

        Properties properties = new Properties();

        properties.put("mail.smtp.host", emailConfiguration.host());
        properties.put("mail.smtp.port", emailConfiguration.port());
        properties.put("mail.smtp.auth", emailConfiguration.auth());
        properties.put("mail.smtp.starttls.enable", emailConfiguration.enableTLS());

        this.session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfiguration.senderEmail(), emailConfiguration.senderPassword());
            }
        });
    }

    public void sendEmail(String emailRecipient) {
        EmailVerification verificationCode = generateVerificationCode(emailRecipient);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfiguration.senderEmail()));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
            message.setSubject("Your Verification Code");

            String emailBody = "Hi,\n\n" +
                    "Your verification code is " + verificationCode.getVerificationOtpCode() + "\n\n" +
                    "This code is valid for the next 30 minutes.\n\n" +
                    "If you did not request this code, please ignore this email.";

            message.setText(emailBody);

            Transport.send(message);
            emailVerificationRepository.save(verificationCode);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOTPCodeValid(EmailVerification emailVerificationDetails) {
        Optional<EmailVerification> otpFromDb = emailVerificationRepository.findById(emailVerificationDetails.getEmail());
        if (otpFromDb.isEmpty()) return false;
        if (otpFromDb.get().getVerificationOtpCode() != emailVerificationDetails.getVerificationOtpCode()) return false;

        Duration duration = Duration.between(otpFromDb.get().getRequestTimestamp(), LocalDateTime.now());
        return duration.toMinutes() <= 30;
    }

    @Override
    public void deleteValidatedOTPCode(EmailVerification emailVerificationDetails) {
        emailVerificationRepository.deleteById(emailVerificationDetails.getEmail());
    }

    public static EmailVerification generateVerificationCode(String email) {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // Generates a random number between 100000 and 999999
        return new EmailVerification(email, code, LocalDateTime.now());
    }
}
