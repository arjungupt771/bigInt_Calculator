package com.bigint.calculator.core;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BigIntDifferentialTest {

    private static final Random RANDOM = new Random(20261012L);

    private static BigInteger randomInteger(int maxBits) {
        BigInteger value = new BigInteger(
                1 + RANDOM.nextInt(maxBits), RANDOM);

        return RANDOM.nextBoolean() ? value : value.negate();
    }

    private static BigInt custom(BigInteger value) {
        return new BigInt(value.toString());
    }

    @Test
    void randomizedArithmeticMatchesBigInteger() {
        for (int i = 0; i < 250; i++) {
            BigInteger a = randomInteger(2048);
            BigInteger b = randomInteger(2048);

            BigInt customA = custom(a);
            BigInt customB = custom(b);

            assertEquals(a.add(b).toString(),
                    customA.add(customB).toString(),
                    "Addition, iteration " + i);

            assertEquals(a.subtract(b).toString(),
                    customA.subtract(customB).toString(),
                    "Subtraction, iteration " + i);

            assertEquals(a.multiply(b).toString(),
                    customA.multiply(customB).toString(),
                    "Multiplication, iteration " + i);

            assertEquals(a.multiply(b).toString(),
                    customA.karatsubaMultiply(customB).toString(),
                    "Karatsuba, iteration " + i);

            assertEquals(a.compareTo(b),
                    customA.compareTo(customB),
                    "Comparison, iteration " + i);

            assertEquals(a.equals(b),
                    customA.equals(customB),
                    "Equality, iteration " + i);

            if (b.signum() != 0) {
                assertEquals(a.divide(b).toString(),
                        customA.divide(customB).toString(),
                        "Division, iteration " + i);

                assertEquals(a.remainder(b).toString(),
                        customA.mod(customB).toString(),
                        "Remainder, iteration " + i);
            }
        }
    }

    @Test
    void randomizedModularExponentiationMatchesBigInteger() {
        for (int i = 0; i < 75; i++) {
            BigInteger base = randomInteger(512);
            BigInteger exponent = BigInteger.valueOf(
                    RANDOM.nextInt(100));
            BigInteger modulus = new BigInteger(
                    1 + RANDOM.nextInt(512), RANDOM).add(BigInteger.ONE);

            BigInt actual = custom(base).power(
                    custom(exponent), custom(modulus));

            assertEquals(
                    base.modPow(exponent, modulus).toString(),
                    actual.toString(),
                    "Modular exponentiation, iteration " + i);
        }
    }

    @Test
    void zeroAndOneRemainCanonical() {
        BigInt negativeZero = new BigInt("-000000");
        BigInt positiveZero = new BigInt("+0");

        assertEquals("0", negativeZero.toString());
        assertEquals(positiveZero, negativeZero);
        assertEquals(positiveZero.hashCode(), negativeZero.hashCode());

        assertEquals("0", new BigInt("-12").multiply(
                new BigInt("0")).toString());
    }
}
