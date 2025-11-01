package com.nicolasmesa.springboot.usermanagement;

import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import com.nicolasmesa.springboot.usermanagement.repository.UserAccountRepository;
import com.nicolasmesa.springboot.usermanagement.service.UserAccountServiceImpl;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

public class UserAccountServiceTest extends UserAccountGenerator {
    private UserAccountRepository userAccountRepository;
    private UserAccountServiceImpl userAccountService;

    @BeforeTry
    void setup() {
        userAccountRepository = Mockito.mock(UserAccountRepository.class);
        userAccountService = new UserAccountServiceImpl(userAccountRepository);
    }

    @Property
    public void getUsers(@ForAll("genListOfAccountDetails") List<UserAccountDetails> userAccountDetails) {
        for (UserAccountDetails userAccountDetail : userAccountDetails) {
            setOnCreationValues(userAccountDetail);
        }
        Mockito.when(userAccountRepository.findAll()).thenReturn(userAccountDetails);
        List<UserAccountDetails> results = userAccountService.getUsers();

        for (int i = 0; i < userAccountDetails.size(); i++) {
            verifyUserAccountDetails(userAccountDetails.get(i), results.get(i));
        }
    }

    @Property
    public void getUserByEmail(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        setOnCreationValues(userAccountDetails);
        Mockito.when(userAccountRepository.findById(userAccountDetails.getEmailAddress())).thenReturn(Optional.of(userAccountDetails));

        UserAccountDetails result = userAccountService.getUserByEmail(userAccountDetails.getEmailAddress(), userAccountDetails.getEmailAddress());
        verifyUserAccountDetails(userAccountDetails, result);
    }

    @Property
    public void failedGettingUserByEmailUnauthorized(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        String authenticatedUserEmail = "testemail@test.com";

        assertThrows(UnAuthorizedException.class, () -> {
            userAccountService.getUserByEmail(userAccountDetails.getEmailAddress(), authenticatedUserEmail);
        });

        Mockito.verifyNoInteractions(userAccountRepository);
    }

    @Property
    public void failedGettingInvalidUserByEmail(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        String email = userAccountDetails.getEmailAddress();
        Mockito.when(userAccountRepository.findById(email)).thenThrow(new UserNotFoundException(email));

        assertThrows(UserNotFoundException.class, () -> {
            userAccountService.getUserByEmail(email, email);
        });
    }

    @Property
    public void updateUserAccountDetails(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.findById(userAccountDetails.getEmailAddress())).thenReturn(Optional.of(userAccountDetails));
        userAccountService.updateUserAccountDetails(userAccountDetails.getEmailAddress(), userAccountDetails);

        Mockito.verify(userAccountRepository, times(1)).save(userAccountDetails);
    }

    @Property
    public void failedUpdatingUserAccountDetails(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.findById(userAccountDetails.getEmailAddress())).thenThrow(new UserNotFoundException(userAccountDetails.getEmailAddress()));

        assertThrows(UserNotFoundException.class, () -> {
            userAccountService.updateUserAccountDetails(userAccountDetails.getEmailAddress(), userAccountDetails);
        });

        Mockito.verify(userAccountRepository, times(0)).save(userAccountDetails);
    }

    @Property
    public void deleteUser(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.findById(userAccountDetails.getEmailAddress())).thenReturn(Optional.of(userAccountDetails));

        userAccountService.deleteUser(userAccountDetails.getEmailAddress());
        Mockito.verify(userAccountRepository, times(1)).deleteById(userAccountDetails.getEmailAddress());
    }

    @Property
    public void failedDeletingUser(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.findById(userAccountDetails.getEmailAddress())).thenThrow(new UserNotFoundException(userAccountDetails.getEmailAddress()));

        assertThrows(UserNotFoundException.class, () -> {
            userAccountService.deleteUser(userAccountDetails.getEmailAddress());
        });

        Mockito.verify(userAccountRepository, times(0)).deleteById(userAccountDetails.getEmailAddress());
    }

    @Property
    public void register(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.existsById(userAccountDetails.getEmailAddress())).thenReturn(false);

        userAccountService.register(userAccountDetails, userAccountDetails.getEmailAddress());
        Mockito.verify(userAccountRepository, times(1)).save(userAccountDetails);
    }

    @Property
    public void failedRegisteringUnauthorized(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        assertThrows(UnAuthorizedException.class, () -> {
            userAccountService.register(userAccountDetails, "");
        });

        Mockito.verify(userAccountRepository, times(0)).save(userAccountDetails);
    }

    @Property
    public void failedRegisterAlreadyExists(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) {
        Mockito.when(userAccountRepository.existsById(userAccountDetails.getEmailAddress())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userAccountService.register(userAccountDetails, userAccountDetails.getEmailAddress());
        });

        Mockito.verify(userAccountRepository, times(0)).save(userAccountDetails);
    }

    public void verifyUserAccountDetails(UserAccountDetails expected, UserAccountDetails result) {
        assertEquals(expected.getFirstName(), result.getFirstName());
        assertEquals(expected.getLastName(), result.getLastName());
        assertEquals(expected.getEmailAddress(), result.getEmailAddress());
        assertEquals(expected.getCountryCode(), result.getCountryCode());
        assertEquals(expected.getMobileNumber(), result.getMobileNumber());
        assertEquals(expected.getHomeAddress(), result.getHomeAddress());
        assertEquals(expected.getDateOfBirth(), result.getDateOfBirth());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    public void setOnCreationValues(UserAccountDetails userAccountDetails) {
        LocalDateTime now = LocalDateTime.now();

        userAccountDetails.setCreatedAt(now);
        userAccountDetails.setUpdatedAt(now);
    }
}
