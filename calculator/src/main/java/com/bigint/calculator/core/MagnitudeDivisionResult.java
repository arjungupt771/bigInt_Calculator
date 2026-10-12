
package com.bigint.calculator.core;

import java.util.Objects;

/**
 * Quotient and remainder for nonnegative magnitude division.
 */
public record MagnitudeDivisionResult(
        DecimalMagnitudeBase1e9 quotient,
        DecimalMagnitudeBase1e9 remainder
) {
    public MagnitudeDivisionResult {
        Objects.requireNonNull(quotient, "Quotient cannot be null");
        Objects.requireNonNull(remainder, "Remainder cannot be null");
    }
}
