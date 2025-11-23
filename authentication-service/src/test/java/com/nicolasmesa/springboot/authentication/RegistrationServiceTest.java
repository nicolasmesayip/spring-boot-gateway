package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.authentication.service.RegistrationServiceImpl;
import com.nicolasmesa.springboot.authentication.service.UserManagementRegistrationService;
import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UnExpectedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.nicolasmesa.springboot.testcommon.UserAccountDtoGenerator.genUserAccountDetailsDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

public class RegistrationServiceTest extends UserAuthenticationGenerator {
    private UserAuthenticationRepository userAuthenticationRepository;
    private PasswordEncoder passwordEncoder;
    private UserManagementRegistrationService userManagementRegistrationService;
    private RegistrationServiceImpl registrationService;

    @BeforeTry
    void setup() {
        userAuthenticationRepository = Mockito.mock(UserAuthenticationRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userManagementRegistrationService = Mockito.mock(UserManagementRegistrationService.class);
        registrationService = new RegistrationServiceImpl(userAuthenticationRepository, passwordEncoder, userManagementRegistrationService);
    }

    @Property
    public void register(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        UserAccountDetailsDto userAccountDetails = genUserAccountDetailsDto().sample();
        Mockito.when(userAuthenticationRepository.existsById(credentialsDto.emailAddress())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(credentialsDto.password())).thenReturn(credentialsDto.password());
        UserAuthentication userAuthentication = new UserAuthentication(credentialsDto.emailAddress(), credentialsDto.password());
        Mockito.when(userAuthenticationRepository.save(userAuthentication)).thenReturn(userAuthentication);
        Mockito.doNothing().when(userManagementRegistrationService).register(userAccountDetails, credentialsDto.emailAddress());

        registrationService.register(credentialsDto, userAccountDetails);
        Mockito.verify(userAuthenticationRepository, times(1)).save(userAuthentication);
    }

    @Property
    public void failedRegistryUserAlreadyExists(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        UserAccountDetailsDto userAccountDetails = genUserAccountDetailsDto().sample();
        UserAuthentication userAuthentication = new UserAuthentication(credentialsDto.emailAddress(), credentialsDto.password());

        Mockito.when(userAuthenticationRepository.existsById(credentialsDto.emailAddress())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            registrationService.register(credentialsDto, userAccountDetails);
        });
        Mockito.verify(userAuthenticationRepository, times(0)).save(userAuthentication);
    }

    @Property
    public void failedRegistryRollingBack(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        UserAccountDetailsDto userAccountDetails = genUserAccountDetailsDto().sample();
        UserAuthentication userAuthentication = new UserAuthentication(credentialsDto.emailAddress(), credentialsDto.password());

        Mockito.when(userAuthenticationRepository.existsById(credentialsDto.emailAddress())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(credentialsDto.password())).thenReturn(credentialsDto.password());
        Mockito.when(userAuthenticationRepository.save(userAuthentication)).thenReturn(userAuthentication);
        Mockito.doThrow(UnAuthorizedException.class).when(userManagementRegistrationService).register(userAccountDetails, credentialsDto.emailAddress());

        assertThrows(UnExpectedException.class, () -> {
            registrationService.register(credentialsDto, userAccountDetails);
        });

        Mockito.verify(userManagementRegistrationService, times(1)).deleteUser(credentialsDto.emailAddress());
        Mockito.verify(userAuthenticationRepository, times(1)).deleteById(credentialsDto.emailAddress());
    }

    @Property
    public void deleteAccount(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        UserAuthentication userAuthentication = new UserAuthentication(credentialsDto.emailAddress(), credentialsDto.password());
        Mockito.when(userAuthenticationRepository.findById(credentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(credentialsDto.password(), userAuthentication.getHashedPassword())).thenReturn(true);

        registrationService.deleteAccount(credentialsDto);
        Mockito.verify(userAuthenticationRepository, times(1)).deleteById(credentialsDto.emailAddress());
        Mockito.verify(userManagementRegistrationService, times(1)).deleteUser(credentialsDto.emailAddress());
    }

    @Property
    public void failedDeletingInexistentAccount(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        Mockito.when(userAuthenticationRepository.findById(credentialsDto.emailAddress())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            registrationService.deleteAccount(credentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(0)).deleteById(credentialsDto.emailAddress());
        Mockito.verify(userManagementRegistrationService, times(0)).deleteUser(credentialsDto.emailAddress());
    }

    @Property
    public void failedDeletingAccountWrongPassword(@ForAll("genUserCredentialsDto") UserCredentialsDto credentialsDto) {
        UserAuthentication userAuthentication = new UserAuthentication(credentialsDto.emailAddress(), credentialsDto.password());
        Mockito.when(userAuthenticationRepository.findById(credentialsDto.emailAddress())).thenReturn(Optional.of(userAuthentication));
        Mockito.when(passwordEncoder.matches(credentialsDto.password(), userAuthentication.getHashedPassword())).thenReturn(false);

        assertThrows(UnAuthorizedException.class, () -> {
            registrationService.deleteAccount(credentialsDto);
        });

        Mockito.verify(userAuthenticationRepository, times(0)).deleteById(credentialsDto.emailAddress());
        Mockito.verify(userManagementRegistrationService, times(0)).deleteUser(credentialsDto.emailAddress());
    }
}
