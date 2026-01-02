package com.nicolasmesa.springboot.productservices.common;

import com.nicolasmesa.springboot.testcommon.Generators;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

import java.util.List;

public class SlugGenerator extends Generators {

    @Provide
    public static Arbitrary<String> genSlug() {
        return Generators.genSlug;
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
