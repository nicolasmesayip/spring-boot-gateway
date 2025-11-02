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
    public static final Arbitrary<String> genCountryCode = Arbitraries.integers().between(1, 999).map(Object::toString);
    public static final Arbitrary<String> genEmailAddress = Arbitraries.ofSuppliers(() -> {
        String personal = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(15).sample();
        String domain = Arbitraries.strings().alpha().ofLength(7).sample();
        String tld = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(3).sample();

        return personal + "@" + domain + "." + tld;
    });

    @Provide
    public Arbitrary<UserAccountDetailsDto> genUserAccountDetailsDto() {
        return Combinators.combine(genStringLengthBetween1To50, genStringLengthBetween1To100, genEmailAddress, genCountryCode, genPhoneNumber, genStringLengthBetween1To255, genLocalDateTimeInPast).as(UserAccountDetailsDto::new);
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
