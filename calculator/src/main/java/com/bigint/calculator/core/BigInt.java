
package com.bigint.calculator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Arbitrary-precision integer implemented using decimal digits.
 * Digits are stored in reverse order: 123 -> [3, 2, 1].
 */
public class BigInt implements Comparable<BigInt> {

    private static final int KARATSUBA_THRESHOLD = 32;

    private ArrayList<Integer> digits = new ArrayList<>();
    private boolean isNegative;

    /** Creates zero. */
    public BigInt() {
        digits.add(0);
    }

    /** Creates a BigInt from a decimal string. */
    public BigInt(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Number cannot be empty");
        }

        int start = 0;

        if (value.charAt(0) == '-' || value.charAt(0) == '+') {
            isNegative = value.charAt(0) == '-';
            start = 1;
        }

        if (start == value.length()) {
            throw new IllegalArgumentException("Invalid integer: " + value);
        }

        for (int i = value.length() - 1; i >= start; i--) {
            char c = value.charAt(i);

            if (c < '0' || c > '9') {
                throw new IllegalArgumentException(
                        "Invalid integer: " + value
                );
            }

            digits.add(c - '0');
        }

        normalize();
    }

    private BigInt(List<Integer> digits, boolean negative) {
        this.digits = new ArrayList<>(digits);
        this.isNegative = negative;
        normalize();
    }

    private void normalize() {
        while (digits.size() > 1
                && digits.get(digits.size() - 1) == 0) {
            digits.remove(digits.size() - 1);
        }

        if (digits.isEmpty()) {
            digits.add(0);
        }

        if (isZero()) {
            isNegative = false;
        }
    }

    private boolean isZero() {
        return digits.size() == 1 && digits.get(0) == 0;
    }

    private BigInt abs() {
        return new BigInt(digits, false);
    }

    private BigInt negate() {
        return isZero()
                ? new BigInt("0")
                : new BigInt(digits, !isNegative);
    }

    private static int compareAbs(BigInt a, BigInt b) {
        if (a.digits.size() != b.digits.size()) {
            return Integer.compare(a.digits.size(), b.digits.size());
        }

        for (int i = a.digits.size() - 1; i >= 0; i--) {
            int comparison = Integer.compare(
                    a.digits.get(i),
                    b.digits.get(i)
            );

            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }

    private static BigInt addAbs(BigInt a, BigInt b) {
        ArrayList<Integer> result = new ArrayList<>();
        int carry = 0;
        int length = Math.max(a.digits.size(), b.digits.size());

        for (int i = 0; i < length || carry > 0; i++) {
            int x = i < a.digits.size() ? a.digits.get(i) : 0;
            int y = i < b.digits.size() ? b.digits.get(i) : 0;

            int sum = x + y + carry;

            result.add(sum % 10);
            carry = sum / 10;
        }

        return new BigInt(result, false);
    }

    /** Subtracts absolute values. Requires |a| >= |b|. */
    private static BigInt subtractAbs(BigInt a, BigInt b) {
        if (compareAbs(a, b) < 0) {
            throw new IllegalArgumentException(
                    "Absolute subtraction requires |a| >= |b|"
            );
        }

        ArrayList<Integer> result = new ArrayList<>();
        int borrow = 0;

        for (int i = 0; i < a.digits.size(); i++) {
            int x = a.digits.get(i) - borrow;
            int y = i < b.digits.size() ? b.digits.get(i) : 0;

            if (x < y) {
                x += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }

            result.add(x - y);
        }

        return new BigInt(result, false);
    }

    /**
     * Multiplies a nonnegative BigInt by a single decimal digit.
     */
    private static BigInt multiplyAbsByDigit(BigInt value, int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException(
                    "Digit must be between 0 and 9"
            );
        }

        if (digit == 0 || value.isZero()) {
            return new BigInt("0");
        }

        ArrayList<Integer> result = new ArrayList<>();
        int carry = 0;

        for (int i = 0; i < value.digits.size(); i++) {
            int product = value.digits.get(i) * digit + carry;

            result.add(product % 10);
            carry = product / 10;
        }

        while (carry > 0) {
            result.add(carry % 10);
            carry /= 10;
        }

        return new BigInt(result, false);
    }

    /** Returns this + other. */
    public BigInt add(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");

        if (isNegative == other.isNegative) {
            BigInt result = addAbs(this, other);
            result.isNegative = isNegative;
            result.normalize();
            return result;
        }

        int comparison = compareAbs(this, other);

        if (comparison == 0) {
            return new BigInt("0");
        }

        BigInt result;

        if (comparison > 0) {
            result = subtractAbs(this, other);
            result.isNegative = isNegative;
        } else {
            result = subtractAbs(other, this);
            result.isNegative = other.isNegative;
        }

        result.normalize();
        return result;
    }

    /** Returns this - other. */
    public BigInt subtract(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        return add(other.negate());
    }

    /** Returns this * other using standard multiplication. */
    public BigInt multiply(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");

        if (isZero() || other.isZero()) {
            return new BigInt("0");
        }

        ArrayList<Integer> result = new ArrayList<>(
                Collections.nCopies(
                        digits.size() + other.digits.size(), 0
                )
        );

        for (int i = 0; i < digits.size(); i++) {
            int carry = 0;

            for (int j = 0;
                    j < other.digits.size() || carry > 0;
                    j++) {

                int index = i + j;

                int y = j < other.digits.size()
                        ? other.digits.get(j)
                        : 0;

                int value = result.get(index)
                        + digits.get(i) * y + carry;

                result.set(index, value % 10);
                carry = value / 10;
            }
        }

        return new BigInt(
                result,
                isNegative != other.isNegative
        );
    }

    /** Multiplies this number by 10^n. */
    private BigInt shiftLeft(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "Shift must be nonnegative"
            );
        }

        if (isZero()) {
            return new BigInt("0");
        }

        ArrayList<Integer> result = new ArrayList<>(
                Collections.nCopies(n, 0)
        );

        result.addAll(digits);

        return new BigInt(result, isNegative);
    }

    /** Returns this * other using Karatsuba multiplication. */
    public BigInt karatsubaMultiply(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");

        if (isZero() || other.isZero()) {
            return new BigInt("0");
        }

        boolean negative = isNegative != other.isNegative;
        BigInt result = karatsubaAbs(this.abs(), other.abs());

        result.isNegative = negative;
        result.normalize();

        return result;
    }

    private static BigInt karatsubaAbs(BigInt a, BigInt b) {
        int n = Math.max(a.digits.size(), b.digits.size());

        if (n <= KARATSUBA_THRESHOLD) {
            return a.multiply(b);
        }

        int half = n / 2;

        BigInt lowA = new BigInt(
                a.digits.subList(
                        0,
                        Math.min(half, a.digits.size())
                ),
                false
        );

        BigInt highA = new BigInt(
                a.digits.subList(
                        Math.min(half, a.digits.size()),
                        a.digits.size()
                ),
                false
        );

        BigInt lowB = new BigInt(
                b.digits.subList(
                        0,
                        Math.min(half, b.digits.size())
                ),
                false
        );

        BigInt highB = new BigInt(
                b.digits.subList(
                        Math.min(half, b.digits.size()),
                        b.digits.size()
                ),
                false
        );

        BigInt z0 = karatsubaAbs(lowA, lowB);
        BigInt z2 = karatsubaAbs(highA, highB);

        BigInt z1 = karatsubaAbs(
                lowA.add(highA),
                lowB.add(highB)
        ).subtract(z2).subtract(z0);

        return z2.shiftLeft(2 * half)
                .add(z1.shiftLeft(half))
                .add(z0);
    }

    /**
     * Divides with truncation toward zero.
     * The remainder has the same sign as the dividend.
     */
    public BigInt divide(BigInt other) {
        return divideAndRemainder(other)[0];
    }

    /** Returns the remainder from division by other. */
    public BigInt mod(BigInt other) {
        return divideAndRemainder(other)[1];
    }

    /**
     * Performs decimal long division.
     *
     * Binary search selects each quotient digit from 0 through 9,
     * avoiding repeated subtraction of the divisor.
     */
    
    private BigInt[] divideAndRemainder(BigInt other) {
        Objects.requireNonNull(other, "Divisor cannot be null");

        if (other.isZero()) {
            throw new ArithmeticException("Division by zero");
        }

        BigInt dividend = abs();
        BigInt divisor = other.abs();

        if (compareAbs(dividend, divisor) < 0) {
            BigInt remainder = abs();
            remainder.isNegative = isNegative && !remainder.isZero();
            return new BigInt[] { new BigInt("0"), remainder };
        }

        /*
         * Keep the working remainder in most-significant-first order.
         * Appending a digit then performs remainder * 10 + digit in O(1),
         * rather than inserting at index zero and shifting the whole list.
         */
        ArrayList<Integer> remainderDigits = new ArrayList<>();
        remainderDigits.add(0);

        // Quotient digits are collected most-significant first.
        ArrayList<Integer> quotientDigits =
                new ArrayList<>(dividend.digits.size());

        /*
         * Precompute divisor * digit once per possible quotient digit.
         * This avoids allocating a BigInt for every binary-search probe.
         */
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] multiples = new ArrayList[10];

        for (int digit = 0; digit <= 9; digit++) {
            multiples[digit] = toMostSignificantFirstDigits(
                    multiplyAbsByDigit(divisor, digit));
        }
        
        for (int i = dividend.digits.size() - 1; i >= 0; i--) {
            int nextDigit = dividend.digits.get(i);
            if (remainderDigits.size() == 1 && remainderDigits.get(0) == 0) {
                remainderDigits.set(0, nextDigit);
                } 
            else {
                remainderDigits.add(nextDigit);
                }
            int low = 0;
            int high = 9;
            int quotientDigit = 0;

            while (low <= high) {
                
                int mid = low + (high - low) / 2;
                if (compareMostSignificantFirst(
                    multiples[mid], remainderDigits) <= 0) {
                        quotientDigit = mid;
                        low = mid + 1;
                        }
                else {
                    high = mid - 1;
                }
            }

            if (quotientDigit != 0) {
                subtractMostSignificantFirstInPlace(
                    remainderDigits, multiples[quotientDigit]);
            }
            quotientDigits.add(quotientDigit);
            }


        Collections.reverse(quotientDigits);
        BigInt quotient = new BigInt(
                quotientDigits, isNegative != other.isNegative);
        quotient.normalize();

        Collections.reverse(remainderDigits);
        BigInt remainder = new BigInt(remainderDigits, isNegative);
        remainder.normalize();

        return new BigInt[] { quotient, remainder };
    }

    /** Converts a BigInt to a most-significant-first digit list. */
    private static ArrayList<Integer> toMostSignificantFirstDigits(
            BigInt value) {
        ArrayList<Integer> result = new ArrayList<>(value.digits);
        Collections.reverse(result);
        return result;
    }

    /** Removes redundant zeroes from the start of an MSF-first digit list. */
    private static void trimLeadingZeros(ArrayList<Integer> value) {
        int firstNonZero = 0;
        while (firstNonZero < value.size() - 1
                && value.get(firstNonZero) == 0) {
            firstNonZero++;
        }

        if (firstNonZero > 0) {
            value.subList(0, firstNonZero).clear();
        }

        if (value.isEmpty()) {
            value.add(0);
        }
    }

    /** Compares nonnegative MSF-first decimal digit lists. */
    private static int compareMostSignificantFirst(
            List<Integer> a, List<Integer> b) {
        if (a.size() != b.size()) {
            return Integer.compare(a.size(), b.size());
        }

        for (int i = 0; i < a.size(); i++) {
            int comparison = Integer.compare(a.get(i), b.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }
    
    
    private static void subtractMostSignificantFirstInPlace(
        ArrayList<Integer> a, List<Integer> b) {
            
            int i = a.size() - 1;
            int j = b.size() - 1;
            int borrow = 0;

            while (i >= 0) {
                
                int x = a.get(i) - borrow;
                int y = j >= 0 ? b.get(j) : 0;
                
                if (x < y) {
                    x += 10;
                    borrow = 1;
                    } 
                else {
                    borrow = 0;
                    }
                a.set(i, x - y);
                i--;
                j--;
            }
            trimLeadingZeros(a);
        }


    /**
     * Computes this^exponent mod modulus for nonnegative exponents.
     * Uses exponentiation by squaring.
     */
    public BigInt power(BigInt exponent, BigInt modulus) {
        Objects.requireNonNull(exponent, "Exponent cannot be null");
        Objects.requireNonNull(modulus, "Modulus cannot be null");

        if (exponent.isNegative) {
            throw new IllegalArgumentException(
                    "Exponent must be nonnegative"
            );
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

        // Normalize the base into [0, modulus).
        if (base.isNegative) {
            base = base.add(positiveModulus);
        }

        while (!remaining.isZero()) {
            if (!remaining.mod(two).isZero()) {
                result = result.multiply(base).mod(positiveModulus);

                if (result.isNegative) {
                    result = result.add(positiveModulus);
                }
            }

            remaining = remaining.divide(two);

            if (!remaining.isZero()) {
                base = base.multiply(base).mod(positiveModulus);

                if (base.isNegative) {
                    base = base.add(positiveModulus);
                }
            }
        }

        return result;
    }

    @Override
    public int compareTo(BigInt other) {
        Objects.requireNonNull(other, "Operand cannot be null");

        if (isNegative != other.isNegative) {
            return isNegative ? -1 : 1;
        }

        int comparison = compareAbs(this, other);
        return isNegative ? -comparison : comparison;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof BigInt)) {
            return false;
        }

        BigInt other = (BigInt) object;

        return isNegative == other.isNegative
                && digits.equals(other.digits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(digits, isNegative);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        if (isNegative) {
            result.append('-');
        }

        for (int i = digits.size() - 1; i >= 0; i--) {
            result.append(digits.get(i));
        }

        return result.toString();
    }
}
