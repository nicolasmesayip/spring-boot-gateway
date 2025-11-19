package com.nicolasmesa.springboot.productservices.common;

import com.nicolasmesa.springboot.testcommon.Generators;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class SlugGenerator extends Generators {
    @Provide
    public static Arbitrary<String> genSlug() {
        return Combinators.combine(genStringLengthBetween5To15, genStringLengthBetween5To15, genInteger)
                .as((str1, str2, integer) -> str1.toLowerCase() + "-" + str2.toLowerCase() + "-" + integer);
    }

    @Provide
    public static Arbitrary<List<String>> genListOfSlugs() {
        return genSlug().list().ofMinSize(5).ofMaxSize(10);
    }

    @Provide
    public static Arbitrary<String> genName() {
        return genStringLengthBetween1To50;
    }
}
