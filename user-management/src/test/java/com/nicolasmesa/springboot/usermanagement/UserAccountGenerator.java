package com.nicolasmesa.springboot.usermanagement;

import com.nicolasmesa.springboot.testcommon.Generators;
import com.nicolasmesa.springboot.usermanagement.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class UserAccountGenerator extends Generators {

    public static final Arbitrary<String> genPhoneNumber = Arbitraries.strings().withCharRange('0', '9').ofMinLength(4).ofMaxLength(14);
    public static final Arbitrary<String> genCountryCode = Arbitraries.strings().withCharRange('0', '9').ofLength(2);

    @Provide
    public Arbitrary<UserAccountDetailsDto> genUserAccountDetailsDto() {
        return Combinators.combine(genStringLengthBetween1To50, genStringLengthBetween1To100, genStringLengthBetween1To100, genPhoneNumber, genCountryCode, genStringLengthBetween1To255, genDateTimeInPast).as(UserAccountDetailsDto::new);
    }

    @Provide
    public Arbitrary<UserAccountDetails> genUserAccountDetails() {
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
