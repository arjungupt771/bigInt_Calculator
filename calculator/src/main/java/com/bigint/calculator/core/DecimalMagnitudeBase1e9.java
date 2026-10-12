
package com.bigint.calculator.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable, nonnegative integer magnitude stored in base 10^9.
 *
 * Chunks are stored least-significant first.
 *
 * Example:
 * 1234567890123456789
 * becomes:
 * [123456789, 1234567890]
 *
 * The most-significant chunk is always nonzero, except for zero itself.
 *
 * This class is an isolated prototype. It does not replace BigInt yet.
 */
public final class DecimalMagnitudeBase1e9
        implements Comparable<DecimalMagnitudeBase1e9> {

    public static final int BASE = 1_000_000_000;

    private final int[] chunks;

    private DecimalMagnitudeBase1e9(int[] chunks) {
        this.chunks = normalize(chunks);
    }

    /**
     * Parses a nonnegative decimal magnitude.
     *
     * Accepted examples: "0", "00042", "1234567890123".
     * Signs and non-decimal characters are rejected.
     */
    public static DecimalMagnitudeBase1e9 fromDecimal(String value) {
        Objects.requireNonNull(value, "Value cannot be null");

        if (value.isEmpty()) {
            throw new NumberFormatException("Value cannot be empty");
        }

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                throw new NumberFormatException(
                        "Expected a nonnegative decimal integer");
            }
        }

        int firstNonZero = 0;
        while (firstNonZero < value.length() - 1
                && value.charAt(firstNonZero) == '0') {
            firstNonZero++;
        }

        String normalized = value.substring(firstNonZero);
        int chunkCount = (normalized.length() + 8) / 9;
        int[] chunks = new int[chunkCount];

        int end = normalized.length();
        int chunkIndex = 0;

        while (end > 0) {
            int start = Math.max(0, end - 9);
            chunks[chunkIndex++] = Integer.parseInt(
                    normalized.substring(start, end));
            end = start;
        }

        return new DecimalMagnitudeBase1e9(chunks);
    }

    /**
     * Returns the exact decimal representation without leading zeros.
     */
    public String toDecimalString() {
        StringBuilder result = new StringBuilder(chunks.length * 9);

        int mostSignificant = chunks.length - 1;
        result.append(chunks[mostSignificant]);

        for (int i = mostSignificant - 1; i >= 0; i--) {
            String chunk = Integer.toString(chunks[i]);

            for (int padding = chunk.length(); padding < 9; padding++) {
                result.append('0');
            }

            result.append(chunk);
        }

        return result.toString();
    }

    /**
     * Number of base-10^9 chunks used by this magnitude.
     */
    public int chunkCount() {
        return chunks.length;
    }

    @Override
    public int compareTo(DecimalMagnitudeBase1e9 other) {
        Objects.requireNonNull(other, "Other magnitude cannot be null");

        if (chunks.length != other.chunks.length) {
            return Integer.compare(chunks.length, other.chunks.length);
        }

        for (int i = chunks.length - 1; i >= 0; i--) {
            int comparison = Integer.compare(chunks[i], other.chunks[i]);

            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }

    private static int[] normalize(int[] input) {
        if (input == null || input.length == 0) {
            return new int[] {0};
        }

        int last = input.length - 1;

        while (last > 0 && input[last] == 0) {
            last--;
        }

        int[] result = Arrays.copyOf(input, last + 1);

        for (int chunk : result) {
            if (chunk < 0 || chunk >= BASE) {
                throw new IllegalArgumentException(
                        "Chunk outside the base-10^9 range");
            }
        }

        return result;
    }


/**
 * Divides this nonnegative magnitude by another magnitude.
 *
 * Uses normalized long division with quotient estimation.
 *
 * @throws ArithmeticException if the divisor is zero
 */
public MagnitudeDivisionResult divideAndRemainder(
        DecimalMagnitudeBase1e9 divisor) {

    Objects.requireNonNull(divisor, "Divisor cannot be null");

    if (divisor.isZero()) {
        throw new ArithmeticException("Division by zero");
    }

    int comparison = compareTo(divisor);

    if (comparison < 0) {
        return new MagnitudeDivisionResult(
                fromDecimal("0"), this);
    }

    if (comparison == 0) {
        return new MagnitudeDivisionResult(
                fromDecimal("1"), fromDecimal("0"));
    }

    if (divisor.chunks.length == 1) {
        return divideBySingleChunk(divisor.chunks[0]);
    }

    int n = divisor.chunks.length;
    int m = chunks.length - n;

    long normalizationFactor =
            BASE / ((long) divisor.chunks[n - 1] + 1);

    int[] normalizedDivisor =
            multiplyBySmallFactor(divisor.chunks, normalizationFactor);

    int[] normalizedDividend =
            multiplyBySmallFactor(chunks, normalizationFactor);

    // The dividend needs one extra high chunk for the division loop.
    int[] u = Arrays.copyOf(normalizedDividend, chunks.length + 1);
    int[] v = normalizedDivisor;

    int[] quotient = new int[m + 1];

    for (int j = m; j >= 0; j--) {
        long numerator =
                (long) u[j + n] * BASE + u[j + n - 1];

        long qHat = numerator / v[n - 1];
        long rHat = numerator % v[n - 1];

        if (qHat >= BASE) {
            qHat = BASE - 1;
            rHat = numerator - qHat * v[n - 1];
        }

        while (n > 1
                && qHat * v[n - 2] > rHat * BASE + u[j + n - 2]) {
            qHat--;
            rHat += v[n - 1];

            if (rHat >= BASE) {
                break;
            }
        }

        long borrow = 0;

        for (int i = 0; i < n; i++) {
            long product = qHat * v[i] + borrow;
            long lowProduct = product % BASE;
            borrow = product / BASE;

            long difference = (long) u[j + i] - lowProduct;

            if (difference < 0) {
                difference += BASE;
                borrow++;
            }

            u[j + i] = (int) difference;
        }

        long topDifference = (long) u[j + n] - borrow;
        boolean negative = topDifference < 0;

        u[j + n] = (int) (negative
                ? topDifference + BASE
                : topDifference);

        if (negative) {
            qHat--;

            long carry = 0;

            for (int i = 0; i < n; i++) {
                long sum = (long) u[j + i] + v[i] + carry;

                if (sum >= BASE) {
                    sum -= BASE;
                    carry = 1;
                } else {
                    carry = 0;
                }

                u[j + i] = (int) sum;
            }

            u[j + n] = (int) (u[j + n] + carry);
        }

        quotient[j] = (int) qHat;
    }

    int[] normalizedRemainder = Arrays.copyOf(u, n);
    int[] remainder = divideBySmallFactor(
            normalizedRemainder, normalizationFactor);

    return new MagnitudeDivisionResult(
            new DecimalMagnitudeBase1e9(quotient),
            new DecimalMagnitudeBase1e9(remainder));
}

private MagnitudeDivisionResult divideBySingleChunk(int divisor) {
    int[] quotient = new int[chunks.length];
    long remainder = 0;

    for (int i = chunks.length - 1; i >= 0; i--) {
        long current = remainder * BASE + chunks[i];

        quotient[i] = (int) (current / divisor);
        remainder = current % divisor;
    }

    return new MagnitudeDivisionResult(
            new DecimalMagnitudeBase1e9(quotient),
            fromDecimal(Long.toString(remainder)));
}

private static int[] multiplyBySmallFactor(
        int[] value, long factor) {

    int[] result = new int[value.length + 1];
    long carry = 0;

    for (int i = 0; i < value.length; i++) {
        long product = (long) value[i] * factor + carry;

        result[i] = (int) (product % BASE);
        carry = product / BASE;
    }

    result[value.length] = (int) carry;
    return normalize(result);
}

private static int[] divideBySmallFactor(
        int[] value, long divisor) {

    int[] result = new int[value.length];
    long remainder = 0;

    for (int i = value.length - 1; i >= 0; i--) {
        long current = remainder * BASE + value[i];

        result[i] = (int) (current / divisor);
        remainder = current % divisor;
    }

    return normalize(result);
}



public DecimalMagnitudeBase1e9 multiply(DecimalMagnitudeBase1e9 other) {
    Objects.requireNonNull(other, "Other magnitude cannot be null");

    if (isZero() || other.isZero()) {
        return new DecimalMagnitudeBase1e9(new int[]{0});
    }

    int[] result = new int[chunks.length + other.chunks.length];

    for (int i = 0; i < chunks.length; i++) {
        long carry = 0;

        for (int j = 0; j < other.chunks.length; j++) {
            int position = i + j;

            long current =
                    (long) chunks[i] * other.chunks[j]
                    + result[position]
                    + carry;

            result[position] = (int) (current % BASE);
            carry = current / BASE;
        }

        int position = i + other.chunks.length;

        while (carry > 0) {
            long current = result[position] + carry;

            result[position] = (int) (current % BASE);
            carry = current / BASE;
            position++;
        }
    }

    return new DecimalMagnitudeBase1e9(result);
}

/**
 * Multiplies two nonnegative magnitudes using Karatsuba multiplication.
 *
 * The algorithm recursively splits operands by base-10^9 chunks.
 * Small operands use grade-school multiplication.
 */
public DecimalMagnitudeBase1e9 karatsubaMultiply(
        DecimalMagnitudeBase1e9 other) {

    Objects.requireNonNull(other, "Other magnitude cannot be null");

    return karatsubaMultiplyRecursive(this, other);
}

private static DecimalMagnitudeBase1e9 karatsubaMultiplyRecursive(
        DecimalMagnitudeBase1e9 a,
        DecimalMagnitudeBase1e9 b) {

    // Avoid recursion overhead for small operands.
    final int threshold = 4;

    if (a.isZero() || b.isZero()) {
        return fromDecimal("0");
    }

    if (Math.min(a.chunks.length, b.chunks.length) <= threshold) {
        return a.multiply(b);
    }

    int split = Math.max(a.chunks.length, b.chunks.length) / 2;

    DecimalMagnitudeBase1e9 aLow = slice(a.chunks, 0, split);
    DecimalMagnitudeBase1e9 aHigh = slice(
            a.chunks, Math.min(split, a.chunks.length), a.chunks.length);

    DecimalMagnitudeBase1e9 bLow = slice(b.chunks, 0, split);
    DecimalMagnitudeBase1e9 bHigh = slice(
            b.chunks, Math.min(split, b.chunks.length), b.chunks.length);

    DecimalMagnitudeBase1e9 z0 =
            karatsubaMultiplyRecursive(aLow, bLow);

    DecimalMagnitudeBase1e9 z2 =
            karatsubaMultiplyRecursive(aHigh, bHigh);

    DecimalMagnitudeBase1e9 z1 =
            karatsubaMultiplyRecursive(
                    aLow.add(aHigh),
                    bLow.add(bHigh))
                    .subtract(z0)
                    .subtract(z2);

    return z2.shiftChunks(2 * split)
            .add(z1.shiftChunks(split))
            .add(z0);
}

/**
 * Returns the chunk slice [from, to), normalized as a magnitude.
 */
private static DecimalMagnitudeBase1e9 slice(
        int[] source, int from, int to) {

    if (from >= to || from >= source.length) {
        return fromDecimal("0");
    }

    int safeTo = Math.min(to, source.length);

    return new DecimalMagnitudeBase1e9(
            Arrays.copyOfRange(source, from, safeTo));
}

/**
 * Multiplies this magnitude by BASE^count.
 */
private DecimalMagnitudeBase1e9 shiftChunks(int count) {
    if (count < 0) {
        throw new IllegalArgumentException(
                "Chunk shift must be nonnegative");
    }

    if (count == 0 || isZero()) {
        return this;
    }

    int[] shifted = new int[chunks.length + count];
    System.arraycopy(chunks, 0, shifted, count, chunks.length);

    return new DecimalMagnitudeBase1e9(shifted);
}

private boolean isZero() {
    return chunks.length == 1 && chunks[0] == 0;
}



/**
 * Adds two nonnegative magnitudes.
 */
public DecimalMagnitudeBase1e9 add(DecimalMagnitudeBase1e9 other) {
    Objects.requireNonNull(other, "Other magnitude cannot be null");

    int size = Math.max(chunks.length, other.chunks.length);
    int[] result = new int[size + 1];

    long carry = 0;

    for (int i = 0; i < size; i++) {
        long sum = carry;

        if (i < chunks.length) {
            sum += chunks[i];
        }

        if (i < other.chunks.length) {
            sum += other.chunks[i];
        }

        result[i] = (int) (sum % BASE);
        carry = sum / BASE;
    }

    result[size] = (int) carry;
    return new DecimalMagnitudeBase1e9(result);
}

/**
 * Subtracts other from this magnitude.
 *
 * @throws ArithmeticException if other is larger than this magnitude
 */
public DecimalMagnitudeBase1e9 subtract(
        DecimalMagnitudeBase1e9 other) {
    Objects.requireNonNull(other, "Other magnitude cannot be null");

    if (compareTo(other) < 0) {
        throw new ArithmeticException(
                "Subtraction would produce a negative magnitude");
    }

    int[] result = new int[chunks.length];
    long borrow = 0;

    for (int i = 0; i < chunks.length; i++) {
        long difference = (long) chunks[i] - borrow;

        if (i < other.chunks.length) {
            difference -= other.chunks[i];
        }

        if (difference < 0) {
            difference += BASE;
            borrow = 1;
        } else {
            borrow = 0;
        }

        result[i] = (int) difference;
    }

    if (borrow != 0) {
        throw new IllegalStateException(
                "Unexpected borrow after magnitude subtraction");
    }

    return new DecimalMagnitudeBase1e9(result);
}

}
