package com.nicolasmesa.springboot.productservices.categories;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

public class Generators {
    Arbitrary<String> genStringLengthBetween1To50 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
    Arbitrary<String> genStringLengthBetween1To255 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(255);
    Arbitrary<String> genStringLengthBetween2To255 = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);

    Arbitrary<Boolean> genBoolean = Arbitraries.of(true, false);

}
