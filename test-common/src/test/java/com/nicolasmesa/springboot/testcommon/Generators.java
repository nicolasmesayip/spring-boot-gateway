package com.nicolasmesa.springboot.testcommon;

import com.nicolasmesa.springboot.common.model.Currency;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.time.api.DateTimes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Generators {
    public static Arbitrary<String> genStringLengthBetween1To50 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
    public static Arbitrary<String> genStringLengthBetween1To100 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100);
    public static Arbitrary<String> genStringLengthBetween5To15 = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(15);
    public static Arbitrary<String> genStringLengthBetween1To255 = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(255);
    public static Arbitrary<String> genStringLengthBetween2To255 = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(255);

    public static Arbitrary<Boolean> genBoolean = Arbitraries.of(true, false);

    public static Arbitrary<Integer> genInteger = Arbitraries.integers().between(0, 10);
    public static Arbitrary<Double> genPositiveDouble = Arbitraries.doubles().greaterOrEqual(0.0);
    public static Arbitrary<BigDecimal> genPositiveBigDecimal = Arbitraries.bigDecimals().between(BigDecimal.valueOf(0.00), BigDecimal.valueOf(10000000000000L)).map(i -> i.setScale(2, RoundingMode.HALF_UP));
    public static Arbitrary<Integer> genPositiveInteger = Arbitraries.integers().greaterOrEqual(0);

    public static Arbitrary<Currency> genCurrency = Arbitraries.of(Currency.class);

    public static Arbitrary<LocalDateTime> genLocalDateTimeInPresentOrFuture = DateTimes.dateTimes().between(LocalDateTime.now(), LocalDateTime.now().plusYears(5));
    public static Arbitrary<LocalDateTime> genLocalDateTimeInFuture = DateTimes.dateTimes().between(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusYears(5));
    public static Arbitrary<Date> genDateTimeInPast = DateTimes.dateTimes().between(LocalDateTime.now().minusYears(100), LocalDateTime.now().minusDays(1)).map(localDateTime -> Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
}
