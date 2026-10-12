# BigInt Calculator

A full-stack arbitrary-precision integer calculator built with **React, TypeScript, Vite, and Spring Boot**. The backend implements custom integer arithmetic using a base-\(10^9\) chunk representation instead of relying on Java's `BigInteger` for production calculations.

## Features

- **Arbitrary-precision arithmetic:** Addition, subtraction, multiplication, division, and modulo.
- **Signed integer support:** Handles positive numbers, negative numbers, and zero.
- **Base-\(10^9\) representation:** Stores integer magnitudes as chunks for arithmetic operations.
- **Karatsuba multiplication:** Provides an alternative multiplication algorithm for larger operands.
- **Modular exponentiation:** Computes powers under a modulus using exponentiation by squaring.
- **Correctness testing:** Includes unit tests, regression tests, and randomized differential tests.
- **Performance benchmarks:** Compares custom arithmetic implementations with Java's `BigInteger`.

## Technology Stack

| Component | Technologies |
|---|---|
| Frontend | React, TypeScript, Vite, Tailwind CSS |
| Backend | Java 17+, Spring Boot |
| Arithmetic engine | Custom signed integer and base-\(10^9\) magnitude implementations |
| Testing | JUnit, differential testing against `java.math.BigInteger` |
| Build tools | npm, Maven Wrapper |

## Architecture

The application separates the user interface, API, and arithmetic implementation.

- **Frontend:** Provides the calculator interface and sends operations to the backend.
- **REST API:** Exposes the `/api/calculate` endpoint.
- **`BigInt`:** Provides the public interface for arbitrary-precision integer operations.
- **`SignedBigIntBase1e9`:** Handles signs and delegates magnitude operations.
- **`DecimalMagnitudeBase1e9`:** Implements arithmetic on base-\(10^9\) chunks.
- **Tests and benchmarks:** Verify correctness and measure arithmetic performance.

Java's `BigInteger` is used as a reference in tests and benchmarks, not as the production arithmetic engine.

## Requirements

- Node.js and npm
- Java 17 or newer
- Git
- Maven, or the included Maven Wrapper

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/arjungupt771/bigInt_Calculator.git
cd bigInt_Calculator
```

### 2. Start the backend

Open a terminal and run:

```bash
cd calculator
./mvnw spring-boot:run
```

On Linux or macOS, if the wrapper is not executable, run:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The backend starts on the port configured by the Spring Boot application.

### 3. Start the frontend

Open a second terminal in the repository root:

```bash
npm ci
npm run dev
```

Open the local URL displayed by Vite in your terminal.

## API Reference

### `POST /api/calculate`

The endpoint accepts two integer operands as decimal strings and an operation name.

**Example request:**

```json
{
  "num1": "12345678901234567890",
  "num2": "987654321",
  "operation": "multiply"
}
```

**Supported operations:**

| Operation | Description |
|---|---|
| `add` | Adds the two operands |
| `subtract` | Subtracts the second operand from the first |
| `multiply` | Multiplies the operands |
| `karatsuba` | Performs multiplication using the Karatsuba operation |
| `divide` | Divides the first operand by the second |
| `modulo` | Calculates the remainder |

The response contains the calculated result as a decimal string, or an error message if the request cannot be processed.

## Testing

Run the complete backend test suite:

```bash
cd calculator
./mvnw clean test
```

The tests cover arithmetic operations, signed values, division behavior, regression cases, and randomized comparisons against Java's `BigInteger`.

## Benchmarks

Standalone benchmark classes are located in:

```text
calculator/src/test/java/com/bigInt/calculator/core/
```

Available benchmark classes include:

- `BigIntArithmeticBenchmark`
- `BigIntDivisionBenchmark`

First compile the test classes:

```bash
cd calculator
./mvnw test-compile
```

Then run a benchmark class from the project root using the compiled test classpath. For example:

```bash
cd calculator
java -cp "target/test-classes:target/classes:$(cat classpath.txt)" \
  com.bigInt.calculator.core.BigIntArithmeticBenchmark
```

The classpath file must first be generated if the project does not already contain it. You can generate it with:

```bash
./mvnw dependency:build-classpath \
  -Dmdep.outputFile=classpath.txt \
  -Dmdep.includeScope=test
```

For the division benchmark, replace `BigIntArithmeticBenchmark` with `BigIntDivisionBenchmark`.

**Benchmark note:** These are standalone benchmarks, not a formal JMH benchmark suite. Results depend on JVM warm-up, hardware, operand size, and iteration count. Use them as local comparisons rather than universal performance claims.

## Integer Semantics

- Division truncates toward zero.
- The remainder has the same sign as the dividend, consistent with Java `BigInteger` semantics.
- Division and modulo by zero are invalid.
- Modular exponentiation requires a nonnegative exponent and a nonzero modulus.

## Project Structure

```text
bigInt_Calculator/
├── calculator/
│   ├── src/
│   │   ├── main/java/       # Spring Boot API and arithmetic engine
│   │   └── test/java/       # Unit tests and benchmarks
│   ├── pom.xml
│   └── mvnw
├── src/                     # Frontend source
├── package.json
├── README.md
└── ...
```

## License

No license has been specified yet. Unless a license is added, all rights remain with the repository owner.
