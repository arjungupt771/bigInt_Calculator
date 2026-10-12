# BigInt Calculator Backend

This directory contains the Spring Boot backend and the custom arbitrary-precision
`BigInt` implementation.

## Run the tests

From this directory, run:

```bash
./mvnw test
```

If Maven is installed locally, you can also run:

```bash
mvn test
```

The division tests compare quotient and remainder behavior against Java
`BigInteger`, including large operands, randomized inputs, and signed edge cases.
Division truncates toward zero, and the remainder has the same sign as the
dividend.

## Run the division benchmark

Compile the project tests first, then run the standalone benchmark:

```bash
./mvnw test-compile dependency:build-classpath -Dmdep.outputFile=classpath.txt -DincludeScope=test
java -cp "target/test-classes:target/classes:$(cat classpath.txt)" com.bigint.calculator.core.BigIntDivisionBenchmark
```

The benchmark uses warm-up rounds, repeated measurements, alternating execution
order, and median per-operation times. It is intended for local comparisons, not
as a substitute for JMH or a controlled performance study.

## Division implementation notes

- Decimal digits remain stored least-significant first in `BigInt`.
- Long division keeps its working remainder most-significant first, so appending
  the next dividend digit does not shift the entire remainder list.
- Multiples of the absolute divisor from 0 through 9 are precomputed once per
  division. Binary search compares against those cached multiples rather than
  allocating a new `BigInt` for every search probe.
- The selected divisor multiple is subtracted in place from the working
  remainder.
