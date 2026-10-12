
package com.bigint.calculator.core;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.jupiter.api.Test;

class SignedBigIntBase1e9Test {

    @Test
    void normalizesSignsAndZero() {
        assertEquals("0",
                SignedBigIntBase1e9.fromDecimal("-0000")
                        .toDecimalString());

        assertEquals("42",
                SignedBigIntBase1e9.fromDecimal("+00042")
                        .toDecimalString());

        assertEquals("-42",
                SignedBigIntBase1e9.fromDecimal("-00042")
                        .toDecimalString());
    }

    @Test
    void signedArithmeticMatchesBigInteger() {
        Random random = new Random(20261015);

        for (int i = 0; i < 500; i++) {
            BigInteger a = new BigInteger(
                    random.nextInt(2001), random);

            BigInteger b = new BigInteger(
                    random.nextInt(2001), random);

            if (random.nextBoolean()) {
                a = a.negate();
            }

            if (random.nextBoolean()) {
                b = b.negate();
            }

            SignedBigIntBase1e9 left =
                    SignedBigIntBase1e9.fromDecimal(a.toString());

            SignedBigIntBase1e9 right =
                    SignedBigIntBase1e9.fromDecimal(b.toString());

            assertEquals(
                    a.add(b).toString(),
                    left.add(right).toDecimalString());

            assertEquals(
                    a.subtract(b).toString(),
                    left.subtract(right).toDecimalString());

            assertEquals(
                    a.multiply(b).toString(),
                    left.multiply(right).toDecimalString());

            assertEquals(
                    Integer.signum(a.compareTo(b)),
                    Integer.signum(left.compareTo(right)));

            if (b.signum() != 0) {
                var actual = left.divideAndRemainder(right);
                BigInteger[] expected = a.divideAndRemainder(b);

                assertEquals(
                        expected[0].toString(),
                        actual.quotient().toDecimalString());

                assertEquals(
                        expected[1].toString(),
                        actual.remainder().toDecimalString());
            }
        }
    }

    @Test
    void divisionTruncatesTowardZero() {
        var result = SignedBigIntBase1e9
                .fromDecimal("-17")
                .divideAndRemainder(
                        SignedBigIntBase1e9.fromDecimal("5"));

        assertEquals("-3", result.quotient().toDecimalString());
        assertEquals("-2", result.remainder().toDecimalString());
    }

    @Test
    void divisionByZeroThrows() {
        var value = SignedBigIntBase1e9.fromDecimal("10");
        var zero = SignedBigIntBase1e9.fromDecimal("0");

        assertThrows(
                ArithmeticException.class,
                () -> value.divideAndRemainder(zero));
    }
}
