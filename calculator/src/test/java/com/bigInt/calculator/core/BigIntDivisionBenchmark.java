package com.bigint.calculator.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/**
 * Lightweight repeatable division benchmark.
 *
 * For publication-quality measurements, use JMH. This standalone benchmark
 * includes warm-up, multiple measurement rounds, alternating execution order,
 * a volatile result sink, and reports the median per-operation time.
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
                "%-10s %-18s %-20s %-12s%n",
                "Bits", "BigInt median (ms)", "BigInteger median (ms)",
                "Slowdown"
        );

        for (int bits : bitLengths) {
            BigInteger dividend = new BigInteger(bits, random)
                    .setBit(bits - 1);
            BigInteger divisor = new BigInteger(Math.max(1, bits / 2), random)
                    .setBit(Math.max(0, bits / 2 - 1));

            BigInt customDividend = new BigInt(dividend.toString());
            BigInt customDivisor = new BigInt(divisor.toString());

            int iterations = Math.max(2, 4000 / bits);

            for (int i = 0; i < warmupRounds; i++) {
                runCustom(customDividend, customDivisor, iterations);
                runReference(dividend, divisor, iterations);
            }

            double[] customTimes = new double[measurementRounds];
            double[] referenceTimes = new double[measurementRounds];

            for (int round = 0; round < measurementRounds; round++) {
                // Alternate order to reduce consistent order-related bias.
                if (round % 2 == 0) {
                    customTimes[round] = measureCustom(
                            customDividend, customDivisor, iterations);
                    referenceTimes[round] = measureReference(
                            dividend, divisor, iterations);
                } else {
                    referenceTimes[round] = measureReference(
                            dividend, divisor, iterations);
                    customTimes[round] = measureCustom(
                            customDividend, customDivisor, iterations);
                }
            }

            double customMedian = median(customTimes);
            double referenceMedian = median(referenceTimes);
            double slowdown = referenceMedian == 0
                    ? Double.POSITIVE_INFINITY
                    : customMedian / referenceMedian;

            System.out.printf(
                    "%-10d %-18.5f %-20.5f %-12.1fx%n",
                    bits, customMedian, referenceMedian, slowdown
            );
        }

        // Make the sink observable after all benchmark loops.
        System.out.println("Benchmark sink: " + blackhole);
    }

    
    private static void runCustom(
            BigInt dividend, BigInt divisor, int iterations) {
                long accumulator = blackhole;
                for (int i = 0; i < iterations; i++) {
                    
                    accumulator = 31L * accumulator + dividend.divide(divisor).hashCode();
                    }
                    blackhole = accumulator;
                    }

    private static void runReference(
        BigInteger dividend, BigInteger divisor, int iterations) {
            long accumulator = blackhole;
            
            for (int i = 0; i < iterations; i++) {
                accumulator = 31L * accumulator + dividend.divide(divisor).hashCode();
                }
                blackhole = accumulator;
            }


    private static double measureCustom(
            BigInt dividend, BigInt divisor, int iterations) {
        long start = System.nanoTime();
        runCustom(dividend, divisor, iterations);
        return (System.nanoTime() - start) / 1_000_000.0 / iterations;
    }

    private static double measureReference(
            BigInteger dividend, BigInteger divisor, int iterations) {
        long start = System.nanoTime();
        runReference(dividend, divisor, iterations);
        return (System.nanoTime() - start) / 1_000_000.0 / iterations;
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
