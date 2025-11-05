package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.exception.AccountLockedException;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.authentication.service.EmailVerificationServiceImpl;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationServiceImpl;
import com.nicolasmesa.springboot.common.JwtTokenUtil;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import io.jsonwebtoken.security.InvalidKeyException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class UserAuthenticationServiceTest extends UserAuthenticationGenerator {
    private PasswordEncoder passwordEncoder;
    private UserAuthenticationRepository userAuthenticationRepository;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private EmailVerificationServiceImpl emailVerificationServiceImpl;
    private UserAuthenticationServiceImpl userAuthenticationService;

    @BeforeTry
    void setup() {
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userAuthenticationRepository = Mockito.mock(UserAuthenticationRepository.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtTokenUtil = Mockito.mock(JwtTokenUtil.class);
        emailVerificationServiceImpl = Mockito.mock(EmailVerificationServiceImpl.class);
        userAuthenticationService = new UserAuthenticationServiceImpl(passwordEncoder, userAuthenticationRepository, authenticationManager, jwtTokenUtil, emailVerificationServiceImpl);
    }

    @Property
    public void login(@ForAll("genUserAuthentication") UserAuthentication userAuthentication, @ForAll("genJwtToken") String jwtToken) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(false);
        userAuthentication.setLastLoginAt(LocalDateTime.now());
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());

        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(userCredentialsDto.password(), userCredentialsDto.password())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(mockAuth)).thenReturn(mockAuth);
        Mockito.when(jwtTokenUtil.generateToken(mockAuth)).thenReturn(jwtToken);

        AuthResponse response = userAuthenticationService.login(userCredentialsDto);

        assertEquals(jwtToken, response.token());
        assertEquals("Logged in successfully", response.message());

        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void failedLoginUserNotFound(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        Mockito.when(userAuthenticationRepository.findById(userAuthentication.getEmailAddress())).thenThrow(new UserNotFoundException(userAuthentication.getEmailAddress()));

        assertThrows(UserNotFoundException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });


        Mockito.verify(userAuthenticationRepository, times(0)).save(userAuthentication);
    }

    @Property
    public void failedLoginLockedAccount(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(true);
        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));

        assertThrows(AccountLockedException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });


        Mockito.verify(userAuthenticationRepository, times(0)).save(userAuthentication);
    }

    @Property
    public void failedLoginLockedAccount2(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(false);
        userAuthentication.setFailedLoginAttempts(userAuthentication.MAXIMUM_LOGIN_ATTEMPTS);

        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(userAuthenticationRepository.existsById(userCredentialsDto.emailAddress())).thenReturn(true);
        Mockito.doNothing().when(emailVerificationServiceImpl).sendEmail(userCredentialsDto.emailAddress());
        Mockito.when(userAuthenticationRepository.save(userAuthentication)).thenReturn(userAuthentication);

        assertThrows(UnAuthorizedException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void failedLoginInvalidCredentials(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(false);
        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(userCredentialsDto.password(), userCredentialsDto.password())).thenReturn(false);

        assertThrows(UnAuthorizedException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void failedLoginAuthenticationException(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(false);
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());

        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(userCredentialsDto.password(), userCredentialsDto.password())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(mockAuth)).thenThrow(new UsernameNotFoundException(""));

        assertThrows(UsernameNotFoundException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void failedLoginInvalidJwtKey(@ForAll("genUserAuthentication") UserAuthentication userAuthentication) {
        UserCredentialsDto userCredentialsDto = new UserCredentialsDto(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());
        userAuthentication.setAccountLocked(false);
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(userAuthentication.getEmailAddress(), userAuthentication.getHashedPassword());

        Mockito.when(userAuthenticationRepository.findById(userCredentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(userCredentialsDto.password(), userCredentialsDto.password())).thenReturn(true);
        Mockito.when(authenticationManager.authenticate(mockAuth)).thenReturn(mockAuth);
        Mockito.when(jwtTokenUtil.generateToken(mockAuth)).thenThrow(new InvalidKeyException(""));

        assertThrows(InvalidKeyException.class, () -> {
            userAuthenticationService.login(userCredentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void resetPassword(@ForAll("genEmailAddress") String emailAddress) {
        Mockito.when(userAuthenticationRepository.existsById(emailAddress)).thenReturn(true);
        Mockito.doNothing().when(emailVerificationServiceImpl).sendEmail(emailAddress);

        AuthResponse response = userAuthenticationService.resetPassword(emailAddress);
        assertNull(response.token());
        assertEquals("Email verification sent.", response.message());
    }

    @Property
    public void failedResettingPasswordUserNotFound(@ForAll("genEmailAddress") String emailAddress) {
        Mockito.when(userAuthenticationRepository.existsById(emailAddress)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userAuthenticationService.resetPassword(emailAddress);
        });
    }

    @Property
    public void verifyOTPCode(@ForAll("genEmailVerification") EmailVerification emailVerification) {
        Mockito.when(userAuthenticationRepository.existsById(emailVerification.getEmail())).thenReturn(true);
        Mockito.when(emailVerificationServiceImpl.isOTPCodeValid(emailVerification)).thenReturn(true);

        AuthResponse response = userAuthenticationService.verifyOTPCode(emailVerification);
        Mockito.verify(emailVerificationServiceImpl, times(1)).deleteValidatedOTPCode(emailVerification);

        assertNull(response.token());
        assertEquals("Email verified successfully.", response.message());
    }

    @Property
    public void failedVerifyingOTPCodeUserNotFound(@ForAll("genEmailVerification") EmailVerification emailVerification) {
        Mockito.when(userAuthenticationRepository.existsById(emailVerification.getEmail())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userAuthenticationService.verifyOTPCode(emailVerification);
        });

        Mockito.verify(emailVerificationServiceImpl, times(0)).deleteValidatedOTPCode(emailVerification);
    }

    @Property
    public void failedVerifyingOTPCodeInvalidOTP(@ForAll("genEmailVerification") EmailVerification emailVerification) {
        Mockito.when(userAuthenticationRepository.existsById(emailVerification.getEmail())).thenReturn(true);
        Mockito.when(emailVerificationServiceImpl.isOTPCodeValid(emailVerification)).thenReturn(false);

        assertThrows(UnAuthorizedException.class, () -> {
            userAuthenticationService.verifyOTPCode(emailVerification);
        });

        Mockito.verify(emailVerificationServiceImpl, times(0)).deleteValidatedOTPCode(emailVerification);
    }

    @Property
    public void updatePassword(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) {
        LocalDateTime now = LocalDateTime.now();
        Mockito.when(userAuthenticationRepository.existsById(userCredentialsDto.emailAddress())).thenReturn(true);
        Mockito.when(passwordEncoder.encode(userCredentialsDto.password())).thenReturn("ENCRYPTED_PASSWORD");
        Mockito.doNothing().when(userAuthenticationRepository).updatePassword(userCredentialsDto.emailAddress(), "ENCRYPTED_PASSWORD", now);
        Mockito.doNothing().when(userAuthenticationRepository).unlockAccount(userCredentialsDto.emailAddress());

        userAuthenticationService.updatePasswordRequest(userCredentialsDto);
        Mockito.verify(userAuthenticationRepository, times(1)).unlockAccount(userCredentialsDto.emailAddress());
    }

    @Property
    public void failedUpdatingPasswordUserNotFound(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) {
        Mockito.when(userAuthenticationRepository.existsById(userCredentialsDto.emailAddress())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userAuthenticationService.updatePasswordRequest(userCredentialsDto);
        });
    }
}
