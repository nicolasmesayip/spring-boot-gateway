package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.repository.EmailVerificationRepository;
import com.nicolasmesa.springboot.common.email.EmailConfiguration;
import com.nicolasmesa.springboot.common.email.EmailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailVerificationServiceImpl extends EmailSender implements EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;

    public EmailVerificationServiceImpl(EmailVerificationRepository emailVerificationRepository, EmailConfiguration emailConfiguration) throws MessagingException {
        super(emailConfiguration);

        this.emailVerificationRepository = emailVerificationRepository;
    }

    public void sendEmail(String emailRecipient) {
        EmailVerification verificationCode = generateVerificationCode(emailRecipient);

        try {
            String subject = "Your Verification Code";
            String emailBody = "Hi,\n\n" +
                    "Your verification code is " + verificationCode.getVerificationOtpCode() + "\n\n" +
                    "This code is valid for the next 30 minutes.\n\n" +
                    "If you did not request this code, please ignore this email.";

            sendEmail(emailRecipient, subject, emailBody);
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
