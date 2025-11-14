package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.dto.UserRegisterRequest;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.testcommon.Generators;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.testcommon.UserAccountDtoGenerator.genUserAccountDetailsDto;

public class UserAuthenticationGenerator extends Generators {

    @Provide
    public Arbitrary<UserAuthentication> genUserAuthentication() {
        return genUserCredentialsDto().map(userCredentials -> {
            UserAuthentication userAuthentication = new UserAuthentication();

            userAuthentication.setEmailAddress(userCredentials.emailAddress());
            userAuthentication.setHashedPassword(userCredentials.password());
            userAuthentication.setFailedLoginAttempts(0);
            userAuthentication.setAccountLocked(false);
            userAuthentication.setPasswordUpdatedAt(genLocalDateTimeInPast.sample());
            userAuthentication.setLastLoginAt(genLocalDateTimeInPast.sample());
            userAuthentication.setRegisteredAt(genLocalDateTimeInPast.sample());

            return userAuthentication;
        });
    }

    @Provide
    public Arbitrary<UserCredentialsDto> genUserCredentialsDto() {
        return Combinators.combine(genEmailAddress, genStringLengthBetween1To50).as(UserCredentialsDto::new);
    }

    @Provide
    public Arbitrary<String> genJwtToken() {
        return Arbitraries.ofSuppliers(() -> {
            String header = Arbitraries.strings().alpha().ofLength(20).sample();
            String payload = Arbitraries.strings().alpha().ofLength(83).sample();
            String signature = Arbitraries.strings().alpha().ofLength(43).sample();

            return header + "." + payload + "." + signature;
        });
    }

    @Provide
    public Arbitrary<Integer> genVerificationCode() {
        return Arbitraries.integers().between(100000, 999999);
    }

    @Provide
    public Arbitrary<EmailVerification> genEmailVerification() {
        return Arbitraries.ofSuppliers(() -> {
            String emailAddress = genEmailAddress.sample();
            Integer verificationCode = genVerificationCode().sample();
            LocalDateTime requestTimestamp = LocalDateTime.now();

            return new EmailVerification(emailAddress, verificationCode, requestTimestamp);
        });
    }

    @Provide
    public Arbitrary<EmailVerificationDto> genEmailVerificationDto() {
        return Combinators.combine(genEmailAddress, genVerificationCode()).as(EmailVerificationDto::new);
    }

    @Provide
    public Arbitrary<String> genEmailAddress() {
        return genEmailAddress;
    }

    @Provide
    public Arbitrary<UserRegisterRequest> genUserRegisterRequest() {
        return Combinators.combine(genUserCredentialsDto(), genUserAccountDetailsDto()).as(UserRegisterRequest::new);
    }
}
