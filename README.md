# BigInt Calculator

A full-stack arbitrary-precision integer calculator built with React,
TypeScript, Vite, and a Spring Boot backend. The backend implements custom
integer arithmetic rather than relying on `java.math.BigInteger` for its
production calculations.

## Features

- Addition, subtraction, multiplication, division, and modulo.
- Signed integer arithmetic with arbitrary-length decimal inputs.
- Base-10^9 chunk representation for the integer magnitude.
- Karatsuba multiplication available as a separate operation.
- Modular exponentiation.
- Automated tests, randomized differential testing, and standalone benchmarks.

## Architecture

- **Frontend:** React, TypeScript, Vite, and Tailwind CSS.
- **Backend:** Java 17-compatible Spring Boot application.
- **Arithmetic:** Custom signed-integer wrapper and base-10^9 magnitude
  implementation.
- **Reference testing:** `java.math.BigInteger` is used in tests and benchmarks
  to check correctness and compare performance.

## Requirements

- Node.js and npm
- Java 17 or newer
- Maven, or the included Maven wrapper

## Run locally

### 1. Start the backend

```bash
cd calculator
./mvnw spring-boot:run
```

The backend uses Spring Boot's configured default port unless overridden.

Run the backend tests with:

```bash
./mvnw clean test
```

### 2. Start the frontend

In a separate terminal:

```bash
cd ~/Desktop/bigInt_Calculator
npm install
npm run dev
```

Open the local URL printed by Vite in your terminal.

The backend's calculator endpoint is `POST /api/calculate` and accepts JSON
containing `num1`, `num2`, and `operation`. Supported operation names are
`add`, `subtract`, `multiply`, `karatsuba`, `divide`, and `modulo`.

Example request:

```json
{
  "num1": "12345678901234567890",
  "num2": "987654321",
  "operation": "multiply"
}
```

The response contains the result as a decimal string, or an error message if
the request cannot be processed.

## Testing and benchmarks

The backend test suite covers arithmetic, signed behavior, division, and
randomized comparisons against Java's `BigInteger`.

Standalone benchmarks are located in
`calculator/src/test/java/com/bigInt/calculator/core/`. Compile the test
classes before running them. See `calculator/README.md` for commands.

Benchmark results depend on JVM warm-up, hardware, and workload. Treat the
included benchmarks as local comparisons, not as a formal performance claim.

## Integer semantics

Division truncates toward zero. The remainder follows the sign of the dividend,
matching Java `BigInteger` division and remainder semantics. Modular
exponentiation requires a nonnegative exponent and a nonzero modulus.

## License

No license has been specified yet. All rights remain with the repository
owner unless a license is added.
