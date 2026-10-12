
package com.bigint.calculator.core;

import java.util.Objects;

/**
 * Signed arbitrary-precision integer using base-10^9 magnitudes.
 *
 * Division truncates toward zero, and the remainder follows
 * the sign of the dividend.
 */
public final class SignedBigIntBase1e9
        implements Comparable<SignedBigIntBase1e9> {

    private final int signum;
    private final DecimalMagnitudeBase1e9 magnitude;

    private SignedBigIntBase1e9(
            int signum,
            DecimalMagnitudeBase1e9 magnitude) {

        Objects.requireNonNull(magnitude, "Magnitude cannot be null");

        boolean zero = magnitude.toDecimalString().equals("0");

        this.signum = zero ? 0 : Integer.signum(signum);
        this.magnitude = magnitude;

        if (!zero && this.signum == 0) {
            throw new IllegalArgumentException(
                    "Nonzero magnitude requires a nonzero sign");
        }
    }

    public static SignedBigIntBase1e9 fromDecimal(String value) {
        Objects.requireNonNull(value, "Value cannot be null");

        if (value.isEmpty()) {
            throw new NumberFormatException("Value cannot be empty");
        }

        int sign = 1;
        int start = 0;

        if (value.charAt(0) == '-') {
            sign = -1;
            start = 1;
        } else if (value.charAt(0) == '+') {
            start = 1;
        }

        if (start == value.length()) {
            throw new NumberFormatException("Missing digits");
        }

        DecimalMagnitudeBase1e9 magnitude =
                DecimalMagnitudeBase1e9.fromDecimal(
                        value.substring(start));

        return new SignedBigIntBase1e9(sign, magnitude);
    }

    public String toDecimalString() {
        String value = magnitude.toDecimalString();

        return signum < 0 ? "-" + value : value;
    }

    public int signum() {
        return signum;
    }

    public SignedBigIntBase1e9 negate() {
        return new SignedBigIntBase1e9(-signum, magnitude);
    }

    public SignedBigIntBase1e9 add(SignedBigIntBase1e9 other) {
        Objects.requireNonNull(other, "Other value cannot be null");

        if (signum == 0) {
            return other;
        }

        if (other.signum == 0) {
            return this;
        }

        if (signum == other.signum) {
            return new SignedBigIntBase1e9(
                    signum, magnitude.add(other.magnitude));
        }

        int comparison = magnitude.compareTo(other.magnitude);

        if (comparison == 0) {
            return fromDecimal("0");
        }

        if (comparison > 0) {
            return new SignedBigIntBase1e9(
                    signum, magnitude.subtract(other.magnitude));
        }

        return new SignedBigIntBase1e9(
                other.signum, other.magnitude.subtract(magnitude));
    }

    public SignedBigIntBase1e9 subtract(
            SignedBigIntBase1e9 other) {
        Objects.requireNonNull(other, "Other value cannot be null");
        return add(other.negate());
    }

    public SignedBigIntBase1e9 multiply(
            SignedBigIntBase1e9 other) {
        Objects.requireNonNull(other, "Other value cannot be null");

        return new SignedBigIntBase1e9(
                signum * other.signum,
                magnitude.multiply(other.magnitude));
    }

    /**
 * Multiplies two signed integers using Karatsuba multiplication.
 */
public SignedBigIntBase1e9 karatsubaMultiply(
        SignedBigIntBase1e9 other) {

    Objects.requireNonNull(other, "Other value cannot be null");

    return new SignedBigIntBase1e9(
            signum * other.signum,
            magnitude.karatsubaMultiply(other.magnitude));
}

    
/**
 * Divides this signed integer by another integer.
 *
 * Division truncates toward zero.
 *
 * @throws ArithmeticException if the divisor is zero
 */
public SignedBigIntBase1e9 divide(
        SignedBigIntBase1e9 divisor) {

    Objects.requireNonNull(divisor, "Divisor cannot be null");

    return divideAndRemainder(divisor).quotient();
}


    public SignedDivisionResult divideAndRemainder(
            SignedBigIntBase1e9 divisor) {

        Objects.requireNonNull(divisor, "Divisor cannot be null");

        if (divisor.signum == 0) {
            throw new ArithmeticException("Division by zero");
        }

        MagnitudeDivisionResult result =
                magnitude.divideAndRemainder(divisor.magnitude);

        SignedBigIntBase1e9 quotient =
                new SignedBigIntBase1e9(
                        signum * divisor.signum,
                        result.quotient());

        SignedBigIntBase1e9 remainder =
                new SignedBigIntBase1e9(
                        signum,
                        result.remainder());

        return new SignedDivisionResult(quotient, remainder);
    }

    @Override
    public int compareTo(SignedBigIntBase1e9 other) {
        Objects.requireNonNull(other, "Other value cannot be null");

        int signComparison = Integer.compare(signum, other.signum);

        if (signComparison != 0) {
            return signComparison;
        }

        return signum < 0
                ? other.magnitude.compareTo(magnitude)
                : magnitude.compareTo(other.magnitude);
    }

    public record SignedDivisionResult(
            SignedBigIntBase1e9 quotient,
            SignedBigIntBase1e9 remainder) {

        public SignedDivisionResult {
            Objects.requireNonNull(quotient);
            Objects.requireNonNull(remainder);
        }
    }
}
