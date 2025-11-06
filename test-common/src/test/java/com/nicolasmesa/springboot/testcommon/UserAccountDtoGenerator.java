package com.nicolasmesa.springboot.testcommon;

import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public class UserAccountDtoGenerator extends Generators {

    public static final Arbitrary<String> genPhoneNumber = Arbitraries.strings().withCharRange('0', '9').ofMinLength(4).ofMaxLength(14);
    public static final Arbitrary<String> genCountryCode = Arbitraries.integers().between(1, 999).map(Object::toString);

    @Provide
    public static Arbitrary<UserAccountDetailsDto> genUserAccountDetailsDto() {
        return Combinators.combine(genStringLengthBetween1To50, genStringLengthBetween1To100, genEmailAddress, genCountryCode, genPhoneNumber, genStringLengthBetween1To255, genLocalDateInPast).as(UserAccountDetailsDto::new);
    }
}
