
package com.bigint.calculator.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Compares division performance across:
 * 1. The existing BigInt implementation.
 * 2. The signed base-10^9 prototype.
 * 3. Java BigInteger.
 *
 * Includes warm-up, repeated measurements, median timings,
 * correctness verification, and an observable result sink.
 *
 * Note: SignedBigIntBase1e9 currently exposes divideAndRemainder(),
 * so its measurement includes remainder calculation as well.
 * This benchmark is useful for initial comparison, but is not
 * a substitute for a JMH benchmark.
 */
public final class BigIntDivisionBenchmark {

    private static volatile long blackhole;

    private BigIntDivisionBenchmark() {
    }

    public static void main(String[] args) {
        Random random = new Random(42);

        int[] bitLengths = {256, 512, 1024, 2048, 4096, 8192};
        int warmupRounds = 3;
        int measurementRounds = 7;

        System.out.printf(
                "%-8s %-17s %-17s %-17s %-12s %-12s%n",
                "Bits",
                "BigInt (ms)",
                "Base1e9 (ms)",
                "BigInteger (ms)",
                "Base1e9/BI",
                "BigInt/BI"
        );

        for (int bits : bitLengths) {
            BigInteger dividend = new BigInteger(bits, random)
                    .setBit(bits - 1);

            BigInteger divisor = new BigInteger(
                    Math.max(1, bits / 2), random)
                    .setBit(Math.max(0, bits / 2 - 1));

            BigInt customDividend =
                    new BigInt(dividend.toString());

            BigInt customDivisor =
                    new BigInt(divisor.toString());

            SignedBigIntBase1e9 baseDividend =
                    SignedBigIntBase1e9.fromDecimal(
                            dividend.toString());

            SignedBigIntBase1e9 baseDivisor =
                    SignedBigIntBase1e9.fromDecimal(
                            divisor.toString());

            verifyResults(
                    dividend,
                    divisor,
                    customDividend,
                    customDivisor,
                    baseDividend,
                    baseDivisor
            );

            int iterations = Math.max(100, 20_000 / bits);

            for (int i = 0; i < warmupRounds; i++) {
                runBigInt(
                        customDividend, customDivisor, iterations);

                runBase1e9(
                        baseDividend, baseDivisor, iterations);

                runBigInteger(
                        dividend, divisor, iterations);
            }

            double[] bigIntTimes =
                    new double[measurementRounds];

            double[] base1e9Times =
                    new double[measurementRounds];

            double[] bigIntegerTimes =
                    new double[measurementRounds];

            for (int round = 0;
                    round < measurementRounds;
                    round++) {

                // Rotate execution order to reduce systematic bias.
                switch (round % 3) {
                    case 0 -> {
                        bigIntTimes[round] = measureBigInt(
                                customDividend,
                                customDivisor,
                                iterations);

                        base1e9Times[round] = measureBase1e9(
                                baseDividend,
                                baseDivisor,
                                iterations);

                        bigIntegerTimes[round] = measureBigInteger(
                                dividend,
                                divisor,
                                iterations);
                    }

                    case 1 -> {
                        base1e9Times[round] = measureBase1e9(
                                baseDividend,
                                baseDivisor,
                                iterations);

                        bigIntegerTimes[round] = measureBigInteger(
                                dividend,
                                divisor,
                                iterations);

                        bigIntTimes[round] = measureBigInt(
                                customDividend,
                                customDivisor,
                                iterations);
                    }

                    default -> {
                        bigIntegerTimes[round] = measureBigInteger(
                                dividend,
                                divisor,
                                iterations);

                        bigIntTimes[round] = measureBigInt(
                                customDividend,
                                customDivisor,
                                iterations);

                        base1e9Times[round] = measureBase1e9(
                                baseDividend,
                                baseDivisor,
                                iterations);
                    }
                }
            }

            double bigIntMedian = median(bigIntTimes);
            double base1e9Median = median(base1e9Times);
            double bigIntegerMedian = median(bigIntegerTimes);

            System.out.printf(
                    "%-8d %-17.5f %-17.5f %-17.5f %-12.1fx %-12.1fx%n",
                    bits,
                    bigIntMedian,
                    base1e9Median,
                    bigIntegerMedian,
                    slowdown(base1e9Median, bigIntegerMedian),
                    slowdown(bigIntMedian, bigIntegerMedian)
            );
        }

        System.out.println("Benchmark sink: " + blackhole);
    }

    
private static void verifyResults(
        BigInteger dividend,
        BigInteger divisor,
        BigInt customDividend,
        BigInt customDivisor,
        SignedBigIntBase1e9 baseDividend,
        SignedBigIntBase1e9 baseDivisor) {

    BigInteger[] expected =
            dividend.divideAndRemainder(divisor);

    // BigInt exposes divide(), but its divideAndRemainder()
    // method is private. Obtain the quotient through the public API.
    BigInt actualQuotient =
            customDividend.divide(customDivisor);

    BigInteger actualCustomQuotient =
            new BigInteger(actualQuotient.toString());

    if (!expected[0].equals(actualCustomQuotient)) {
        throw new AssertionError(
                "BigInt quotient mismatch for "
                        + dividend.bitLength() + " bits");
    }

    // Verify the remainder using the identity:
    // dividend = quotient * divisor + remainder.
    BigInteger actualCustomRemainder =
            dividend.subtract(
                    actualCustomQuotient.multiply(divisor));

    var baseResult =
            baseDividend.divideAndRemainder(baseDivisor);

    if (!expected[0].toString().equals(
                baseResult.quotient().toDecimalString())
            || !expected[1].toString().equals(
                baseResult.remainder().toDecimalString())) {

        throw new AssertionError(
                "Base-1e9 division mismatch for "
                        + dividend.bitLength() + " bits");
    }

    if (!expected[1].equals(actualCustomRemainder)) {
        throw new AssertionError(
                "BigInt remainder mismatch for "
                        + dividend.bitLength() + " bits");
    }
}


    private static void runBigInt(
            BigInt dividend, BigInt divisor, int iterations) {

        long accumulator = blackhole;

        for (int i = 0; i < iterations; i++) {
            accumulator = 31L * accumulator
                    + dividend.divide(divisor).toString().hashCode();
        }

        blackhole = accumulator;
    }

    
private static void runBase1e9(
        SignedBigIntBase1e9 dividend,
        SignedBigIntBase1e9 divisor,
        int iterations) {

    long accumulator = blackhole;

    for (int i = 0; i < iterations; i++) {
        accumulator = 31L * accumulator
                + dividend.divide(divisor)
                        .toDecimalString()
                        .hashCode();
    }

    blackhole = accumulator;
}


    private static void runBigInteger(
            BigInteger dividend, BigInteger divisor, int iterations) {

        long accumulator = blackhole;

        for (int i = 0; i < iterations; i++) {
            accumulator = 31L * accumulator
                    + dividend.divide(divisor).toString().hashCode();
        }

        blackhole = accumulator;
    }

    private static double measureBigInt(
            BigInt dividend, BigInt divisor, int iterations) {

        long start = System.nanoTime();
        runBigInt(dividend, divisor, iterations);

        return (System.nanoTime() - start)
                / 1_000_000.0 / iterations;
    }

    private static double measureBase1e9(
            SignedBigIntBase1e9 dividend,
            SignedBigIntBase1e9 divisor,
            int iterations) {

        long start = System.nanoTime();
        runBase1e9(dividend, divisor, iterations);

        return (System.nanoTime() - start)
                / 1_000_000.0 / iterations;
    }

    private static double measureBigInteger(
            BigInteger dividend, BigInteger divisor, int iterations) {

        long start = System.nanoTime();
        runBigInteger(dividend, divisor, iterations);

        return (System.nanoTime() - start)
                / 1_000_000.0 / iterations;
    }

    private static double slowdown(
            double customMedian, double referenceMedian) {

        return referenceMedian == 0
                ? Double.POSITIVE_INFINITY
                : customMedian / referenceMedian;
    }

    private static double median(double[] values) {
        double[] sorted = Arrays.copyOf(values, values.length);
        Arrays.sort(sorted);

        int middle = sorted.length / 2;

        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        }

        return sorted[middle];
    }
}
