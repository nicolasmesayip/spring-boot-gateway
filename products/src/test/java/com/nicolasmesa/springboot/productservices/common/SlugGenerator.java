package com.nicolasmesa.springboot.productservices.common;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

public class SlugGenerator extends Generators {
    @Provide
    public static Arbitrary<String> genSlug() {
        return Combinators.combine(genStringLengthBetween5To15, genStringLengthBetween5To15, genInteger)
                .as((str1, str2, integer) -> str1.toLowerCase() + "-" + str2.toLowerCase() + "-" + integer);
    }

    @Provide
    public static Arbitrary<String> genName() {
        return genStringLengthBetween1To50;
    }
}
