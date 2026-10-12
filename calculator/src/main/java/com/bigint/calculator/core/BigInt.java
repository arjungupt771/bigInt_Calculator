package com.bigint.calculator.core;

import java.util.Objects;

/**
 * Immutable arbitrary-precision signed integer.
 *
 * Arithmetic is delegated to the base-10^9 implementation.
 * Division truncates toward zero, and the remainder follows
 * the sign of the dividend.
 */
public class BigInt implements Comparable<BigInt> {

    private final SignedBigIntBase1e9 value;

    /** Creates zero. */
    public BigInt() {
        this("0");
    }

    /** Creates a BigInt from a decimal string. */
    public BigInt(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Number cannot be empty");
        }

        this.value = SignedBigIntBase1e9.fromDecimal(value);
    }

    private BigInt(SignedBigIntBase1e9 value) {
        this.value = Objects.requireNonNull(
                value, "Value cannot be null");
    }

    private boolean isZero() {
        return value.signum() == 0;
    }

    private BigInt abs() {
        return value.signum() < 0
                ? new BigInt(value.negate())
                : this;
    }

    private BigInt negate() {
        return new BigInt(value.negate());
    }

    /** Returns this + other. */
    public BigInt add(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return new BigInt(value.add(other.value));
    }

    /** Returns this - other. */
    public BigInt subtract(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return new BigInt(value.subtract(other.value));
    }

    /** Returns this * other using base-10^9 multiplication. */
    public BigInt multiply(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return new BigInt(value.multiply(other.value));
    }

    /** Returns this * other using recursive Karatsuba multiplication. */
    public BigInt karatsubaMultiply(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return new BigInt(value.karatsubaMultiply(other.value));
    }

    /** Divides with truncation toward zero. */
    public BigInt divide(BigInt other) {
        return divideAndRemainder(other)[0];
    }

    /**
     * Returns the remainder from division by other.
     * The remainder has the same sign as the dividend,
     * unless the remainder is zero.
     */
    public BigInt mod(BigInt other) {
        return divideAndRemainder(other)[1];
    }

    private BigInt[] divideAndRemainder(BigInt other) {
        Objects.requireNonNull(other, "Divisor cannot be null");

        if (other.isZero()) {
            throw new ArithmeticException("Division by zero");
        }

        SignedBigIntBase1e9.SignedDivisionResult result =
                value.divideAndRemainder(other.value);

        return new BigInt[] {
                new BigInt(result.quotient()),
                new BigInt(result.remainder())
        };
    }

    /**
     * Computes this^exponent mod modulus for nonnegative exponents.
     * Uses exponentiation by squaring.
     */
    public BigInt power(BigInt exponent, BigInt modulus) {
        Objects.requireNonNull(exponent, "Exponent cannot be null");
        Objects.requireNonNull(modulus, "Modulus cannot be null");

        if (exponent.value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Exponent must be nonnegative");
        }

        if (modulus.isZero()) {
            throw new ArithmeticException("Modulus cannot be zero");
        }

        BigInt positiveModulus = modulus.abs();

        if (positiveModulus.equals(new BigInt("1"))) {
            return new BigInt("0");
        }

        BigInt result = new BigInt("1");
        BigInt base = this.mod(positiveModulus);
        BigInt remaining = exponent;
        BigInt two = new BigInt("2");

        // Normalize the base into [0, positiveModulus).
        if (base.value.signum() < 0) {
            base = base.add(positiveModulus);
        }

        while (!remaining.isZero()) {
            if (!remaining.mod(two).isZero()) {
                result = result.multiply(base).mod(positiveModulus);

                if (result.value.signum() < 0) {
                    result = result.add(positiveModulus);
                }
            }

            remaining = remaining.divide(two);

            if (!remaining.isZero()) {
                base = base.multiply(base).mod(positiveModulus);

                if (base.value.signum() < 0) {
                    base = base.add(positiveModulus);
                }
            }
        }

        return result;
    }

    @Override
    public int compareTo(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof BigInt other)) {
            return false;
        }

        return value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return value.toDecimalString();
    }
}
