
package com.bigint.calculator.core;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BigIntTest {

    @Test
    void constructorNormalizesSignsAndZeros() {
        assertEquals("0", new BigInt("-0000").toString());
        assertEquals("123", new BigInt("+00123").toString());
        assertEquals("-123", new BigInt("-00123").toString());

        assertThrows(
                IllegalArgumentException.class,
                () -> new BigInt("12a3")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new BigInt("-")
        );
    }

    @Test
    void additionHandlesSigns() {
        assertEquals("1000",
                new BigInt("999").add(new BigInt("1")).toString());

        assertEquals("-2",
                new BigInt("-5").add(new BigInt("3")).toString());

        assertEquals("2",
                new BigInt("-3").add(new BigInt("5")).toString());

        assertEquals("0",
                new BigInt("-42").add(new BigInt("42")).toString());
    }

    @Test
    void subtractionHandlesSigns() {
        assertEquals("-2",
                new BigInt("3").subtract(new BigInt("5")).toString());

        assertEquals("-8",
                new BigInt("-5").subtract(new BigInt("3")).toString());

        assertEquals("8",
                new BigInt("5").subtract(new BigInt("-3")).toString());

        assertEquals("0",
                new BigInt("123").subtract(new BigInt("123")).toString());
    }

    @Test
    void multiplicationHandlesSigns() {
        assertEquals("-144",
                new BigInt("-12").multiply(new BigInt("12")).toString());

        assertEquals("144",
                new BigInt("-12").multiply(new BigInt("-12")).toString());

        assertEquals("0",
                new BigInt("0").multiply(new BigInt("-999")).toString());
    }

    @Test
    void divisionAndModuloHandleSigns() {
        assertEquals("14",
                new BigInt("100").divide(new BigInt("7")).toString());

        assertEquals("2",
                new BigInt("100").mod(new BigInt("7")).toString());

        assertEquals("-14",
                new BigInt("-100").divide(new BigInt("7")).toString());

        assertEquals("-2",
                new BigInt("-100").mod(new BigInt("7")).toString());

        assertEquals("14",
                new BigInt("-100").divide(new BigInt("-7")).toString());

        assertThrows(
                ArithmeticException.class,
                () -> new BigInt("10").divide(new BigInt("0"))
        );
    }

    @Test
    void comparisonHandlesNegativeNumbers() {
        assertTrue(new BigInt("-10").compareTo(new BigInt("-2")) < 0);
        assertTrue(new BigInt("-2").compareTo(new BigInt("0")) < 0);
        assertTrue(new BigInt("10").compareTo(new BigInt("-10")) > 0);

        assertEquals(
                0,
                new BigInt("-0012").compareTo(new BigInt("-12"))
        );
    }

    @Test
    void equalityAndHashCodeAreConsistent() {
        BigInt first = new BigInt("000123");
        BigInt second = new BigInt("123");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(new BigInt("-123"), second);
    }

    @Test
    void karatsubaMatchesStandardMultiplication() {
        String a = "123456789012345678901234567890123456789";
        String b = "987654321098765432109876543210987654321";

        BigInt first = new BigInt(a);
        BigInt second = new BigInt(b);

        assertEquals(
                first.multiply(second).toString(),
                first.karatsubaMultiply(second).toString()
        );

        assertEquals(
                new BigInt("-" + a).multiply(second).toString(),
                new BigInt("-" + a).karatsubaMultiply(second).toString()
        );
    }

    @Test
    void karatsubaMatchesReferenceAroundThreshold() {
        int[] lengths = {
                1, 2, 31, 32, 33, 34, 63, 64, 65, 100, 128, 256
        };

        for (int length : lengths) {
            String a = "9".repeat(length);
            String b = "7".repeat(length);

            BigInteger expected = new BigInteger(a)
                    .multiply(new BigInteger(b));

            BigInt first = new BigInt(a);
            BigInt second = new BigInt(b);

            assertEquals(
                    expected.toString(),
                    first.karatsubaMultiply(second).toString(),
                    "Karatsuba failed for operand length " + length
            );
        }
    }

    @Test
    void karatsubaHandlesUnequalOperandLengths() {
        String[][] operands = {
                {"1", "9".repeat(100)},
                {"9".repeat(31), "7".repeat(80)},
                {"8".repeat(33), "6".repeat(257)},
                {"9".repeat(128), "7".repeat(32)}
        };

        for (String[] pair : operands) {
            BigInteger a = new BigInteger(pair[0]);
            BigInteger b = new BigInteger(pair[1]);

            BigInt first = new BigInt(pair[0]);
            BigInt second = new BigInt(pair[1]);

            assertEquals(
                    a.multiply(b).toString(),
                    first.karatsubaMultiply(second).toString(),
                    "Failed for operand lengths "
                            + pair[0].length()
                            + " and "
                            + pair[1].length()
            );
        }
    }

    @Test
    void karatsubaHandlesNegativeOperandsAndZero() {
        String a = "98765432101234567890".repeat(5);
        String b = "12345678909876543210".repeat(4);

        BigInt positiveA = new BigInt(a);
        BigInt positiveB = new BigInt(b);

        BigInteger expected = new BigInteger(a).multiply(new BigInteger(b));

        assertEquals(
                expected.toString(),
                positiveA.karatsubaMultiply(positiveB).toString()
        );

        assertEquals(
                expected.negate().toString(),
                new BigInt("-" + a)
                        .karatsubaMultiply(positiveB).toString()
        );

        assertEquals(
                expected.negate().toString(),
                positiveA.karatsubaMultiply(new BigInt("-" + b)).toString()
        );

        assertEquals(
                expected.toString(),
                new BigInt("-" + a)
                        .karatsubaMultiply(new BigInt("-" + b)).toString()
        );

        assertEquals(
                "0",
                new BigInt("0")
                        .karatsubaMultiply(new BigInt("-" + b)).toString()
        );
    }


    @Test
    void randomizedDivisionPreservesReferenceResultsAndInvariants() {
        Random random = new Random(20261012);

        for (int trial = 0; trial < 200; trial++) {
            // Vary operand sizes to exercise short and long divisions.
            int dividendBits = 1 + random.nextInt(1200);
            int divisorBits = 1 + random.nextInt(800);

            BigInteger dividend = new BigInteger(dividendBits, random);
            BigInteger divisor = new BigInteger(divisorBits, random);

            // Ensure the divisor is never zero.
            if (divisor.signum() == 0) {
                divisor = BigInteger.ONE;
            }

            // Exercise all sign combinations deterministically.
            if ((trial & 1) != 0) {
                dividend = dividend.negate();
            }
            if ((trial & 2) != 0) {
                divisor = divisor.negate();
            }

            BigInt actualDividend = new BigInt(dividend.toString());
            BigInt actualDivisor = new BigInt(divisor.toString());

            BigInt quotient = actualDividend.divide(actualDivisor);
            BigInt remainder = actualDividend.mod(actualDivisor);

            BigInteger expectedQuotient = dividend.divide(divisor);
            BigInteger expectedRemainder = dividend.remainder(divisor);

            assertEquals(
                    expectedQuotient.toString(),
                    quotient.toString(),
                    "Quotient mismatch at trial " + trial
            );

            assertEquals(
                    expectedRemainder.toString(),
                    remainder.toString(),
                    "Remainder mismatch at trial " + trial
            );

            // Fundamental division identity:
            // dividend = quotient * divisor + remainder
            assertEquals(
                    dividend.toString(),
                    quotient.multiply(actualDivisor)
                            .add(remainder)
                            .toString(),
                    "Division identity failed at trial " + trial
            );
            assertTrue(
        expectedRemainder.abs().compareTo(divisor.abs()) < 0,
        "Remainder bound failed at trial " + trial
);

        }
    }


    @Test
    void karatsubaMatchesReferenceForLargeRandomNumbers() {
        Random random = new Random(12345);

        int[] bitLengths = {33, 64, 100, 256, 512, 1000, 2000};

        for (int bitLength : bitLengths) {
            for (int trial = 0; trial < 3; trial++) {
                BigInteger a = new BigInteger(bitLength * 4, random);
                BigInteger b = new BigInteger(bitLength * 4, random);

                if (random.nextBoolean()) {
                    a = a.negate();
                }

                if (random.nextBoolean()) {
                    b = b.negate();
                }

                BigInt first = new BigInt(a.toString());
                BigInt second = new BigInt(b.toString());

                assertEquals(
                        a.multiply(b).toString(),
                        first.karatsubaMultiply(second).toString(),
                        "Karatsuba mismatch at bit length " + bitLength
                );
            }
        }
    }



    @Test
    void largeDivisionMatchesJavaBigInteger() {
    Random random = new Random(42);

    int[] bitLengths = {1000, 2000, 4000, 8000};

    for (int bits : bitLengths) {
        for (int i = 0; i < 4; i++) {
            BigInteger dividend = new BigInteger(bits, random);
            BigInteger divisor = new BigInteger(Math.max(1, bits / 2), random)
                    .add(BigInteger.ONE);

            // Exercise every sign combination.
            if (i % 2 == 0) {
                dividend = dividend.negate();
            }
            if (i >= 2) {
                divisor = divisor.negate();
            }

            BigInt actualDividend = new BigInt(dividend.toString());
            BigInt actualDivisor = new BigInt(divisor.toString());

            assertEquals(
                    dividend.divide(divisor).toString(),
                    actualDividend.divide(actualDivisor).toString(),
                    "Quotient mismatch for " + bits + "-bit dividend"
            );

            assertEquals(
                    dividend.remainder(divisor).toString(),
                    actualDividend.mod(actualDivisor).toString(),
                    "Remainder mismatch for " + bits + "-bit dividend"
            );
        }
    }
}


    @Test
    void divisionCoversBoundaryAndSignCases() {
        String[][] cases = {
                {"0", "7"},
                {"1", "99"},
                {"99", "99"},
                {"1000", "10"},
                {"1001", "10"},
                {"123456789", "1"},
                {"-100", "7"},
                {"100", "-7"},
                {"-100", "-7"},
                {"-1", "2"},
                {"1", "-2"},
                {"-1", "-2"}
        };

        for (String[] pair : cases) {
            BigInteger dividend = new BigInteger(pair[0]);
            BigInteger divisor = new BigInteger(pair[1]);
            BigInt actualDividend = new BigInt(pair[0]);
            BigInt actualDivisor = new BigInt(pair[1]);

            assertEquals(
                    dividend.divide(divisor).toString(),
                    actualDividend.divide(actualDivisor).toString(),
                    "Quotient mismatch for " + pair[0] + " / " + pair[1]
            );
            assertEquals(
                    dividend.remainder(divisor).toString(),
                    actualDividend.mod(actualDivisor).toString(),
                    "Remainder mismatch for " + pair[0] + " % " + pair[1]
            );
        }
    }

    @Test
    void randomizedArithmeticMatchesJavaBigInteger() {
        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            BigInteger a = new BigInteger(200, random);
            BigInteger b = new BigInteger(200, random);

            if (random.nextBoolean()) {
                a = a.negate();
            }

            if (random.nextBoolean()) {
                b = b.negate();
            }

            BigInt first = new BigInt(a.toString());
            BigInt second = new BigInt(b.toString());

            assertEquals(
                    a.add(b).toString(),
                    first.add(second).toString()
            );

            assertEquals(
                    a.subtract(b).toString(),
                    first.subtract(second).toString()
            );

            assertEquals(
                    a.multiply(b).toString(),
                    first.multiply(second).toString()
            );

            assertEquals(
                    Integer.signum(a.compareTo(b)),
                    Integer.signum(first.compareTo(second))
            );

            if (b.signum() != 0) {
                assertEquals(
                        a.divide(b).toString(),
                        first.divide(second).toString()
                );

                assertEquals(
                        a.remainder(b).toString(),
                        first.mod(second).toString()
                );
            }
        }
    }

    @Test
    void powerHandlesBasicCases() {
        assertEquals(
                "1",
                new BigInt("7")
                        .power(new BigInt("0"), new BigInt("13"))
                        .toString()
        );

        assertEquals(
                "8",
                new BigInt("2")
                        .power(new BigInt("3"), new BigInt("13"))
                        .toString()
        );

        assertEquals(
                "5",
                new BigInt("-2")
                        .power(new BigInt("3"), new BigInt("13"))
                        .toString()
        );

        assertEquals(
                "10",
                new BigInt("2")
                        .power(new BigInt("10"), new BigInt("13"))
                        .toString()
        );
    }

    @Test
    void powerMatchesBigIntegerForRandomCases() {
        Random random = new Random(2026);

        for (int i = 0; i < 100; i++) {
            BigInteger base = new BigInteger(100, random);

            if (random.nextBoolean()) {
                base = base.negate();
            }

            int exponent = random.nextInt(101);

            BigInteger modulus = BigInteger.valueOf(
                    random.nextInt(999) + 2L
            );

            BigInt actual = new BigInt(base.toString())
                    .power(
                            new BigInt(String.valueOf(exponent)),
                            new BigInt(modulus.toString())
                    );

            BigInteger expected = base.modPow(
                    BigInteger.valueOf(exponent),
                    modulus
            );

            assertEquals(
                    expected.toString(),
                    actual.toString(),
                    "Modular exponentiation mismatch at iteration " + i
            );
        }
    }

    @Test
    void powerRejectsInvalidArguments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BigInt("2")
                        .power(new BigInt("-1"), new BigInt("7"))
        );

        assertThrows(
                ArithmeticException.class,
                () -> new BigInt("2")
                        .power(new BigInt("5"), new BigInt("0"))
        );
    }
}
