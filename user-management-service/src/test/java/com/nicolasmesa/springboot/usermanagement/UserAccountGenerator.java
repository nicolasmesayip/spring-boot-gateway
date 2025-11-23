package com.nicolasmesa.springboot.usermanagement;

import com.nicolasmesa.springboot.testcommon.UserAccountDtoGenerator;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

import java.util.List;

public class UserAccountGenerator extends UserAccountDtoGenerator {
    @Provide
    public static Arbitrary<UserAccountDetails> genUserAccountDetails() {
        return genUserAccountDetailsDto().map(dto -> {
            UserAccountDetails userAccountDetails = new UserAccountDetails();

            userAccountDetails.setFirstName(dto.firstName());
            userAccountDetails.setLastName(dto.lastName());
            userAccountDetails.setEmailAddress(dto.emailAddress());
            userAccountDetails.setCountryCode(dto.countryCode());
            userAccountDetails.setMobileNumber(dto.mobileNumber());
            userAccountDetails.setHomeAddress(dto.homeAddress());
            userAccountDetails.setDateOfBirth(dto.dateOfBirth());

            return userAccountDetails;
        });
    }

    @Provide
    public Arbitrary<List<UserAccountDetails>> genListOfAccountDetails() {
        return genUserAccountDetails().list().ofMaxSize(10);
    }
}
