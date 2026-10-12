
package com.bigint.calculator.core;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public final class BigIntArithmeticBenchmark {

    private static volatile long blackhole;

    private BigIntArithmeticBenchmark() {
    }

    public static void main(String[] args) {
        int[] bitLengths = {256, 512, 1024, 2048, 4096, 8192};
        Random random = new Random(20261012);

        System.out.printf(
                "%-8s %-14s %-14s %-14s %-14s%n",
                "Bits", "Operation", "BigInt (ms)",
                "Base1e9 (ms)", "BigInteger (ms)");

        for (int bits : bitLengths) {
            BigInteger a = new BigInteger(bits, random).setBit(bits - 1);
            BigInteger b = new BigInteger(bits, random).setBit(bits - 1);

            BigInt customA = new BigInt(a.toString());
            BigInt customB = new BigInt(b.toString());

            SignedBigIntBase1e9 baseA =
                    SignedBigIntBase1e9.fromDecimal(a.toString());
            SignedBigIntBase1e9 baseB =
                    SignedBigIntBase1e9.fromDecimal(b.toString());

            int iterations = Math.max(100, 20_000 / bits);

            benchmark(bits, "add", iterations,
                    () -> customA.add(customB).toString(),
                    () -> baseA.add(baseB).toDecimalString(),
                    () -> a.add(b).toString());

            benchmark(bits, "subtract", iterations,
                    () -> customA.subtract(customB).toString(),
                    () -> baseA.subtract(baseB).toDecimalString(),
                    () -> a.subtract(b).toString());

            benchmark(bits, "multiply", iterations,
                    () -> customA.multiply(customB).toString(),
                    () -> baseA.multiply(baseB).toDecimalString(),
                    () -> a.multiply(b).toString());
        }

        System.out.println("Benchmark sink: " + blackhole);
    }

    private static void benchmark(
            int bits,
            String operation,
            int iterations,
            Operation custom,
            Operation base1e9,
            Operation reference) {

        String expected = reference.execute();

        if (!expected.equals(custom.execute())
                || !expected.equals(base1e9.execute())) {
            throw new AssertionError(
                    operation + " mismatch at " + bits + " bits");
        }

        for (int i = 0; i < 3; i++) {
            run(custom, iterations);
            run(base1e9, iterations);
            run(reference, iterations);
        }

        double[] customTimes = new double[7];
        double[] baseTimes = new double[7];
        double[] referenceTimes = new double[7];

        for (int i = 0; i < 7; i++) {
            switch (i % 3) {
                case 0 -> {
                    customTimes[i] = measure(custom, iterations);
                    baseTimes[i] = measure(base1e9, iterations);
                    referenceTimes[i] = measure(reference, iterations);
                }
                case 1 -> {
                    baseTimes[i] = measure(base1e9, iterations);
                    referenceTimes[i] = measure(reference, iterations);
                    customTimes[i] = measure(custom, iterations);
                }
                default -> {
                    referenceTimes[i] = measure(reference, iterations);
                    customTimes[i] = measure(custom, iterations);
                    baseTimes[i] = measure(base1e9, iterations);
                }
            }
        }

        System.out.printf(
                "%-8d %-14s %-14.5f %-14.5f %-14.5f%n",
                bits, operation,
                median(customTimes),
                median(baseTimes),
                median(referenceTimes));
    }

    private static void run(Operation operation, int iterations) {
        long accumulator = blackhole;

        for (int i = 0; i < iterations; i++) {
            accumulator = 31L * accumulator
                    + operation.execute().hashCode();
        }

        blackhole = accumulator;
    }

    private static double measure(
            Operation operation, int iterations) {

        long start = System.nanoTime();
        run(operation, iterations);

        return (System.nanoTime() - start)
                / 1_000_000.0 / iterations;
    }

    private static double median(double[] values) {
        double[] sorted = Arrays.copyOf(values, values.length);
        Arrays.sort(sorted);

        int middle = sorted.length / 2;

        return sorted.length % 2 == 0
                ? (sorted[middle - 1] + sorted[middle]) / 2.0
                : sorted[middle];
    }

    @FunctionalInterface
    private interface Operation {
        String execute();
    }
}
