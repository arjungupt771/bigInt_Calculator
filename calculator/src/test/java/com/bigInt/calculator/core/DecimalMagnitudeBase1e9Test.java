
package com.bigint.calculator.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DecimalMagnitudeBase1e9Test {

    @Test
    void roundTripsValuesAcrossChunkBoundaries() {
        String[] values = {
                "0",
                "0000",
                "1",
                "999999999",
                "1000000000",
                "1000000001",
                "1234567890123456789",
                "999999999999999999999999999999999999"
        };

        for (String value : values) {
            DecimalMagnitudeBase1e9 magnitude =
                    DecimalMagnitudeBase1e9.fromDecimal(value);

            assertEquals(
                    new BigInteger(value).toString(),
                    magnitude.toDecimalString(),
                    "Round-trip failed for " + value
            );
        }
    }

    @Test
void karatsubaMatchesGradeSchoolForLargeMagnitudes() {
    String a = "123456789".repeat(12);
    String b = "987654321".repeat(10);

    DecimalMagnitudeBase1e9 first =
            DecimalMagnitudeBase1e9.fromDecimal(a);
    DecimalMagnitudeBase1e9 second =
            DecimalMagnitudeBase1e9.fromDecimal(b);

    assertEquals(
            first.multiply(second).toDecimalString(),
            first.karatsubaMultiply(second).toDecimalString());
}

@Test
void karatsubaHandlesUnequalOperandLengths() {
    String a = "12345678901234567890123456789012345678901234567890";
    String b = "987654321";

    DecimalMagnitudeBase1e9 first =
            DecimalMagnitudeBase1e9.fromDecimal(a);
    DecimalMagnitudeBase1e9 second =
            DecimalMagnitudeBase1e9.fromDecimal(b);

    assertEquals(
            first.multiply(second).toDecimalString(),
            first.karatsubaMultiply(second).toDecimalString());
}

@Test
void karatsubaHandlesZero() {
    DecimalMagnitudeBase1e9 zero =
            DecimalMagnitudeBase1e9.fromDecimal("0");
    DecimalMagnitudeBase1e9 value =
            DecimalMagnitudeBase1e9.fromDecimal(
                    "123456789012345678901234567890123456789");

    assertEquals(
            "0",
            zero.karatsubaMultiply(value).toDecimalString());
}


@Test
void divisionMatchesJavaBigInteger() {
    Random random = new Random(20261014);

    for (int i = 0; i < 500; i++) {
        BigInteger dividend = new BigInteger(
                random.nextInt(2001), random);

        BigInteger divisor;

        do {
            divisor = new BigInteger(
                    random.nextInt(1000) + 1, random);
        } while (divisor.signum() == 0);

        DecimalMagnitudeBase1e9 left =
                DecimalMagnitudeBase1e9.fromDecimal(
                        dividend.toString());

        DecimalMagnitudeBase1e9 right =
                DecimalMagnitudeBase1e9.fromDecimal(
                        divisor.toString());

        MagnitudeDivisionResult actual =
                left.divideAndRemainder(right);

        BigInteger[] expected =
                dividend.divideAndRemainder(divisor);

        assertEquals(
                expected[0].toString(),
                actual.quotient().toDecimalString());

        assertEquals(
                expected[1].toString(),
                actual.remainder().toDecimalString());
    }
}

@Test
void divisionHandlesImportantBoundaries() {
    DecimalMagnitudeBase1e9 one =
            DecimalMagnitudeBase1e9.fromDecimal("1");

    DecimalMagnitudeBase1e9 divisor =
            DecimalMagnitudeBase1e9.fromDecimal(
                    "1000000000000000001");

    MagnitudeDivisionResult smaller =
            one.divideAndRemainder(divisor);

    assertEquals("0", smaller.quotient().toDecimalString());
    assertEquals("1", smaller.remainder().toDecimalString());

    DecimalMagnitudeBase1e9 dividend =
            DecimalMagnitudeBase1e9.fromDecimal(
                    "999999999999999999999999999999");

    DecimalMagnitudeBase1e9 exactDivisor =
            DecimalMagnitudeBase1e9.fromDecimal("999999999");

    MagnitudeDivisionResult result =
            dividend.divideAndRemainder(exactDivisor);

    BigInteger[] expected =
            new BigInteger(dividend.toDecimalString())
                    .divideAndRemainder(
                            new BigInteger(exactDivisor.toDecimalString()));

    assertEquals(expected[0].toString(),
            result.quotient().toDecimalString());

    assertEquals(expected[1].toString(),
            result.remainder().toDecimalString());
}

@Test
void divisionByZeroThrows() {
    DecimalMagnitudeBase1e9 value =
            DecimalMagnitudeBase1e9.fromDecimal("123456789");

    DecimalMagnitudeBase1e9 zero =
            DecimalMagnitudeBase1e9.fromDecimal("0");

    assertThrows(
            ArithmeticException.class,
            () -> value.divideAndRemainder(zero));
}


    @Test
    void usesFewerChunksThanDecimalDigitsForLargeValues() {
        String value = "1234567890123456789";

        DecimalMagnitudeBase1e9 magnitude =
                DecimalMagnitudeBase1e9.fromDecimal(value);

        assertEquals(3, magnitude.chunkCount());
        assertTrue(magnitude.chunkCount() < value.length());
    }

    @Test
    void comparesMagnitudesCorrectly() {
        assertTrue(
                DecimalMagnitudeBase1e9.fromDecimal("999999999")
                        .compareTo(
                                DecimalMagnitudeBase1e9.fromDecimal("1000000000"))
                        < 0
        );

        assertTrue(
                DecimalMagnitudeBase1e9.fromDecimal("123456790")
                        .compareTo(
                                DecimalMagnitudeBase1e9.fromDecimal("123456789"))
                        > 0
        );

        assertEquals(
                0,
                DecimalMagnitudeBase1e9.fromDecimal("000123")
                        .compareTo(
                                DecimalMagnitudeBase1e9.fromDecimal("123"))
        );
    }

    @Test
    void randomizedRoundTripsMatchJavaBigInteger() {
        Random random = new Random(20261012);

        for (int trial = 0; trial < 500; trial++) {
            BigInteger expected =
                    new BigInteger(1 + random.nextInt(2000), random);

            String decimal = expected.toString();

            DecimalMagnitudeBase1e9 magnitude =
                    DecimalMagnitudeBase1e9.fromDecimal(decimal);

            assertEquals(
                    expected.toString(),
                    magnitude.toDecimalString(),
                    "Mismatch at trial " + trial
            );
        }
    }

    @Test
    void rejectsInvalidInput() {
        assertThrows(
                NumberFormatException.class,
                () -> DecimalMagnitudeBase1e9.fromDecimal("")
        );

        assertThrows(
                NumberFormatException.class,
                () -> DecimalMagnitudeBase1e9.fromDecimal("-123")
        );

        assertThrows(
                NumberFormatException.class,
                () -> DecimalMagnitudeBase1e9.fromDecimal("12a3")
        );

        assertThrows(
                NullPointerException.class,
                () -> DecimalMagnitudeBase1e9.fromDecimal(null)
        );
    }


@Test
void multiplicationMatchesJavaBigInteger() {
    Random random = new Random(20261013);

    for (int i = 0; i < 500; i++) {
        BigInteger a = new BigInteger(
                random.nextInt(2001), random
        );
        BigInteger b = new BigInteger(
                random.nextInt(2001), random
        );

        DecimalMagnitudeBase1e9 left =
                DecimalMagnitudeBase1e9.fromDecimal(a.toString());

        DecimalMagnitudeBase1e9 right =
                DecimalMagnitudeBase1e9.fromDecimal(b.toString());

        assertEquals(
                a.multiply(b).toString(),
                left.multiply(right).toDecimalString(),
                "Multiplication mismatch for " + a + " * " + b
        );
    }
}

@Test
void multiplicationHandlesChunkBoundaries() {
    DecimalMagnitudeBase1e9 left =
            DecimalMagnitudeBase1e9.fromDecimal("999999999");

    DecimalMagnitudeBase1e9 right =
            DecimalMagnitudeBase1e9.fromDecimal("999999999");

    assertEquals(
            "999999998000000001",
            left.multiply(right).toDecimalString()
    );

    assertEquals(
            "1000000000000000000",
            DecimalMagnitudeBase1e9.fromDecimal("1000000000")
                    .multiply(
                            DecimalMagnitudeBase1e9.fromDecimal("1000000000")
                    )
                    .toDecimalString()
    );
}

@Test
void multiplicationByZeroReturnsZero() {
    DecimalMagnitudeBase1e9 zero =
            DecimalMagnitudeBase1e9.fromDecimal("0");

    DecimalMagnitudeBase1e9 value =
            DecimalMagnitudeBase1e9.fromDecimal(
                    "123456789012345678901234567890"
            );

    assertEquals("0", zero.multiply(value).toDecimalString());
    assertEquals("0", value.multiply(zero).toDecimalString());
}



@Test
void additionAndSubtractionMatchJavaBigInteger() {
    Random random = new Random(20261013);

    for (int trial = 0; trial < 500; trial++) {
        BigInteger a = new BigInteger(1 + random.nextInt(2000), random);
        BigInteger b = new BigInteger(1 + random.nextInt(2000), random);

        DecimalMagnitudeBase1e9 left =
                DecimalMagnitudeBase1e9.fromDecimal(a.toString());

        DecimalMagnitudeBase1e9 right =
                DecimalMagnitudeBase1e9.fromDecimal(b.toString());

        assertEquals(
                a.add(b).toString(),
                left.add(right).toDecimalString()
        );

        BigInteger larger = a.max(b);
        BigInteger smaller = a.min(b);

        DecimalMagnitudeBase1e9 largeMagnitude =
                DecimalMagnitudeBase1e9.fromDecimal(larger.toString());

        DecimalMagnitudeBase1e9 smallMagnitude =
                DecimalMagnitudeBase1e9.fromDecimal(smaller.toString());

        assertEquals(
                larger.subtract(smaller).toString(),
                largeMagnitude.subtract(smallMagnitude).toDecimalString()
        );
    }
}

@Test
void subtractionRejectsNegativeResults() {
    DecimalMagnitudeBase1e9 smaller =
            DecimalMagnitudeBase1e9.fromDecimal("99");

    DecimalMagnitudeBase1e9 larger =
            DecimalMagnitudeBase1e9.fromDecimal("100");

    assertThrows(
            ArithmeticException.class,
            () -> smaller.subtract(larger)
    );
}

@Test
void arithmeticHandlesChunkBoundaries() {
    DecimalMagnitudeBase1e9 allNines =
            DecimalMagnitudeBase1e9.fromDecimal("999999999");

    DecimalMagnitudeBase1e9 one =
            DecimalMagnitudeBase1e9.fromDecimal("1");

    assertEquals(
            "1000000000",
            allNines.add(one).toDecimalString()
    );

    assertEquals(
            "999999999",
            DecimalMagnitudeBase1e9.fromDecimal("1000000000")
                    .subtract(one)
                    .toDecimalString()
    );
}

}
