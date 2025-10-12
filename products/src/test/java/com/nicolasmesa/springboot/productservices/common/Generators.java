package com.nicolasmesa.springboot.productservices.common;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

public class Generators {
    public static Arbitrary<String> genStringLengthBetween1To50 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
    public static Arbitrary<String> genStringLengthBetween5To15 = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(15);
    public static Arbitrary<String> genStringLengthBetween1To255 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(255);
    public static Arbitrary<String> genStringLengthBetween2To255 = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);

    public static Arbitrary<Boolean> genBoolean = Arbitraries.of(true, false);

    public static Arbitrary<Integer> genInteger = Arbitraries.integers().between(0, 10);

}
