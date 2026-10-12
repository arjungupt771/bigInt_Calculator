# BigInt Calculator Backend

Spring Boot backend for the BigInt Calculator. Its custom `BigInt` API delegates
to a signed integer implementation backed by base-10^9 magnitude chunks.

## Implementation

- **Magnitude representation:** little-endian `int[]` chunks, each storing a
  base-10^9 digit.
- **Signed arithmetic:** a signed wrapper handles sign and magnitude separately.
- **Arithmetic operations:** addition, subtraction, multiplication, division,
  and remainder.
- **Multiplication:** grade-school multiplication and an explicit Karatsuba
  operation.
- **Modular exponentiation:** exponentiation by squaring.
- **Correctness testing:** deterministic unit tests and randomized differential
  tests against `java.math.BigInteger`.

The Java `BigInteger` implementation is used as a test and benchmark reference;
it is not the production arithmetic engine.

## Requirements

- Java 17 or newer
- Maven, or the included Maven wrapper

## Run the application

From this directory:

```bash
./mvnw spring-boot:run
```

## Run the tests

```bash
./mvnw clean test
```

## Run the standalone benchmarks

Compile test classes and generate the test dependency classpath:

```bash
./mvnw test-compile dependency:build-classpath \
  -Dmdep.outputFile=classpath.txt \
  -DincludeScope=test
```

Run the arithmetic benchmark:

```bash
java -cp "target/test-classes:target/classes:$(cat classpath.txt)" \
  com.bigint.calculator.core.BigIntArithmeticBenchmark
```

Run the division benchmark:

```bash
java -cp "target/test-classes:target/classes:$(cat classpath.txt)" \
  com.bigint.calculator.core.BigIntDivisionBenchmark
```

These standalone benchmarks perform correctness checks, warm-up, and repeated
measurements. They are useful for local comparisons, but they are not a
substitute for JMH or a controlled performance study. Results depend on JVM
warm-up, system load, operand sizes, and the selected workloads.

The production `BigInt` and the standalone `SignedBigIntBase1e9` prototype use
the same underlying arithmetic implementation. Their timings should not be
interpreted as a comparison between independent algorithms.

## Arithmetic semantics

- Division truncates toward zero.
- The remainder has the sign of the dividend.
- Division and modulo by zero throw an arithmetic exception.
- Modular exponentiation rejects negative exponents and zero moduli.

## Compatibility

The public `BigInt` class provides the calculator's integer API. The base-10^9
implementation classes support that API internally.
