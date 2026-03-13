// import java.util.*;
// import java.math.*;

// public class BigInt {

//     private ArrayList<Integer> digits = new ArrayList<>();
//     private boolean isNegative = false;

//     // Constructors
//     public BigInt() {}

//     public BigInt(String num) {
//         if (num.charAt(0) == '-') {
//             isNegative = true;
//             num = num.substring(1);
//         }
//         for (int i = num.length() - 1; i >= 0; i--) {
//             digits.add(num.charAt(i) - '0');
//         }
//         removeLeadingZeros();
//     }

//     // Remove leading zeros
//     private void removeLeadingZeros() {
//         while (digits.size() > 1 && digits.get(digits.size() - 1) == 0) {
//             digits.remove(digits.size() - 1);
//         }
//         if (digits.size() == 1 && digits.get(0) == 0) {
//             isNegative = false;
//         }
//     }

//     // ADD
//     public BigInt add(BigInt other) {
//         BigInt res = new BigInt();
//         res.isNegative = this.isNegative;

//         int carry = 0;
//         int n = Math.max(this.digits.size(), other.digits.size());

//         for (int i = 0; i < n || carry != 0; i++) {
//             int x = i < this.digits.size() ? this.digits.get(i) : 0;
//             int y = i < other.digits.size() ? other.digits.get(i) : 0;
//             int sum = x + y + carry;
//             res.digits.add(sum % 10);
//             carry = sum / 10;
//         }
//         return res;
//     }

//     // SUBTRACT (assumes this >= other)
//     public BigInt subtract(BigInt other) {
//         BigInt res = new BigInt();
//         res.isNegative = this.isNegative;

//         int borrow = 0;
//         for (int i = 0; i < digits.size(); i++) {
//             int x = digits.get(i) - borrow;
//             int y = i < other.digits.size() ? other.digits.get(i) : 0;
//             int diff = x - y;
//             borrow = 0;
//             if (diff < 0) {
//                 diff += 10;
//                 borrow = 1;
//             }
//             res.digits.add(diff);
//         }
//         res.removeLeadingZeros();
//         return res;
//     }

//     // MULTIPLY (Normal)
//     public BigInt multiply(BigInt other) {
//         BigInt res = new BigInt();
//         for (int i = 0; i < digits.size() + other.digits.size(); i++)
//             res.digits.add(0);

//         for (int i = 0; i < digits.size(); i++) {
//             int carry = 0;
//             for (int j = 0; j < other.digits.size() || carry != 0; j++) {
//                 int curr = res.digits.get(i + j)
//                         + digits.get(i) * (j < other.digits.size() ? other.digits.get(j) : 0)
//                         + carry;
//                 res.digits.set(i + j, curr % 10);
//                 carry = curr / 10;
//             }
//         }
//         res.removeLeadingZeros();
//         return res;
//     }

//     // KARATSUBA MULTIPLY
//     public BigInt karatsubaMultiply(BigInt other) {
//         int n = Math.max(this.digits.size(), other.digits.size());
//         if (n <= 32) return multiply(other);

//         int half = (n + 1) / 2;

//         BigInt low1 = new BigInt(), high1 = new BigInt();
//         BigInt low2 = new BigInt(), high2 = new BigInt();

//         low1.digits = new ArrayList<>(digits.subList(0, Math.min(half, digits.size())));
//         high1.digits = new ArrayList<>(digits.subList(Math.min(half, digits.size()), digits.size()));

//         low2.digits = new ArrayList<>(other.digits.subList(0, Math.min(half, other.digits.size())));
//         high2.digits = new ArrayList<>(other.digits.subList(Math.min(half, other.digits.size()), other.digits.size()));

//         BigInt z0 = low1.karatsubaMultiply(low2);
//         BigInt z1 = (low1.add(high1)).karatsubaMultiply(low2.add(high2));
//         BigInt z2 = high1.karatsubaMultiply(high2);

//         BigInt res = new BigInt();
//         for (int i = 0; i < z0.digits.size() + 2 * half; i++)
//             res.digits.add(0);

//         for (int i = 0; i < z0.digits.size(); i++)
//             res.digits.set(i, z0.digits.get(i));

//         for (int i = 0; i < z1.digits.size(); i++)
//             res.digits.set(i + half, res.digits.get(i + half) + z1.digits.get(i));

//         for (int i = 0; i < z2.digits.size(); i++)
//             res.digits.set(i + 2 * half, res.digits.get(i + 2 * half) + z2.digits.get(i));

//         res.removeLeadingZeros();
//         return res;
//     }

//     // DIVIDE + MOD
//     private Pair divideAndRemainder(BigInt divisor) {
//         if (divisor.equals(new BigInt("0")))
//             throw new ArithmeticException("Division by zero");

//         BigInt dividend = new BigInt(this.toString());
//         BigInt quotient = new BigInt("0");
//         BigInt remainder = dividend;

//         if (dividend.compareTo(divisor) < 0)
//             return new Pair(new BigInt("0"), dividend);

//         int shift = dividend.digits.size() - divisor.digits.size();
//         for (int i = 0; i < shift; i++) divisor.digits.add(0, 0);

//         for (int i = 0; i <= shift; i++) {
//             int q = 0;
//             while (remainder.compareTo(divisor) >= 0) {
//                 remainder = remainder.subtract(divisor);
//                 q++;
//             }
//             quotient.digits.add(0, q);
//             divisor.digits.remove(0);
//         }

//         quotient.removeLeadingZeros();
//         remainder.removeLeadingZeros();
//         return new Pair(quotient, remainder);
//     }

//     public BigInt divide(BigInt other) {
//         return divideAndRemainder(other).quotient;
//     }

//     public BigInt mod(BigInt other) {
//         return divideAndRemainder(other).remainder;
//     }

//     // POWER (modular exponentiation)
//     public BigInt power(BigInt exp, BigInt mod) {
//         if (exp.equals(new BigInt("0"))) return new BigInt("1");
//         BigInt half = power(exp.divide(new BigInt("2")), mod);
//         BigInt halfMod = half.multiply(half).mod(mod);
//         return exp.digits.get(0) % 2 == 0 ? halfMod : halfMod.multiply(this).mod(mod);
//     }

//     // COMPARISON
//     public int compareTo(BigInt other) {
//         if (digits.size() != other.digits.size())
//             return digits.size() - other.digits.size();
//         for (int i = digits.size() - 1; i >= 0; i--) {
//             if (!digits.get(i).equals(other.digits.get(i)))
//                 return digits.get(i) - other.digits.get(i);
//         }
//         return 0;
//     }

//     @Override
//     public boolean equals(Object o) {
//         if (!(o instanceof BigInt)) return false;
//         BigInt b = (BigInt) o;
//         return digits.equals(b.digits);
//     }

//     @Override
//     public String toString() {
//         StringBuilder sb = new StringBuilder();
//         if (isNegative) sb.append("-");
//         for (int i = digits.size() - 1; i >= 0; i--)
//             sb.append(digits.get(i));
//         return sb.toString();
//     }

//     // Helper class
//     private static class Pair {
//         BigInt quotient, remainder;
//         Pair(BigInt q, BigInt r) {
//             quotient = q;
//             remainder = r;
//         }
//     }
// }


package com.bigint.calculator.core;

import java.util.ArrayList;

public class BigInt {

    private ArrayList<Integer> digits = new ArrayList<>();
    private boolean isNegative = false;

    public BigInt() {}

    public BigInt(String num) {
        if (num == null || num.isEmpty()) {
            throw new IllegalArgumentException("Invalid number");
        }

        if (num.charAt(0) == '-') {
            isNegative = true;
            num = num.substring(1);
        }

        for (int i = num.length() - 1; i >= 0; i--) {
            if (!Character.isDigit(num.charAt(i))) {
                throw new IllegalArgumentException("Invalid number");
            }
            digits.add(num.charAt(i) - '0');
        }
        removeLeadingZeros();
    }

    /* ---------------- UTILS ---------------- */

    private void removeLeadingZeros() {
        while (digits.size() > 1 && digits.get(digits.size() - 1) == 0) {
            digits.remove(digits.size() - 1);
        }
        if (digits.size() == 1 && digits.get(0) == 0) {
            isNegative = false;
        }
    }

    private BigInt shiftLeft(int n) {
        BigInt res = new BigInt();
        for (int i = 0; i < n; i++) res.digits.add(0);
        res.digits.addAll(this.digits);
        return res;
    }

    /* ---------------- ADD ---------------- */

    public BigInt add(BigInt other) {
        BigInt res = new BigInt();
        int carry = 0;
        int n = Math.max(digits.size(), other.digits.size());

        for (int i = 0; i < n || carry > 0; i++) {
            int a = i < digits.size() ? digits.get(i) : 0;
            int b = i < other.digits.size() ? other.digits.get(i) : 0;
            int sum = a + b + carry;
            res.digits.add(sum % 10);
            carry = sum / 10;
        }
        return res;
    }

    /* ---------------- SUBTRACT (assumes this >= other) ---------------- */

    public BigInt subtract(BigInt other) {
        BigInt res = new BigInt();
        int borrow = 0;

        for (int i = 0; i < digits.size(); i++) {
            int a = digits.get(i) - borrow;
            int b = i < other.digits.size() ? other.digits.get(i) : 0;
            int diff = a - b;
            borrow = 0;
            if (diff < 0) {
                diff += 10;
                borrow = 1;
            }
            res.digits.add(diff);
        }
        res.removeLeadingZeros();
        return res;
    }

    /* ---------------- NORMAL MULTIPLY ---------------- */

    public BigInt multiply(BigInt other) {
        BigInt res = new BigInt();
        for (int i = 0; i < digits.size() + other.digits.size(); i++) {
            res.digits.add(0);
        }

        for (int i = 0; i < digits.size(); i++) {
            int carry = 0;
            for (int j = 0; j < other.digits.size() || carry > 0; j++) {
                int val = res.digits.get(i + j)
                        + digits.get(i) * (j < other.digits.size() ? other.digits.get(j) : 0)
                        + carry;
                res.digits.set(i + j, val % 10);
                carry = val / 10;
            }
        }
        res.removeLeadingZeros();
        return res;
    }

    /* ---------------- KARATSUBA MULTIPLY ---------------- */

    public BigInt karatsubaMultiply(BigInt other) {
        int n = Math.max(this.digits.size(), other.digits.size());
        if (n <= 32) {
            return multiply(other);
        }

        int half = n / 2;

        BigInt low1 = new BigInt();
        BigInt high1 = new BigInt();
        BigInt low2 = new BigInt();
        BigInt high2 = new BigInt();

        low1.digits = new ArrayList<>(digits.subList(0, Math.min(half, digits.size())));
        high1.digits = new ArrayList<>(digits.subList(Math.min(half, digits.size()), digits.size()));

        low2.digits = new ArrayList<>(other.digits.subList(0, Math.min(half, other.digits.size())));
        high2.digits = new ArrayList<>(other.digits.subList(Math.min(half, other.digits.size()), other.digits.size()));

        BigInt z0 = low1.karatsubaMultiply(low2);
        BigInt z1 = (low1.add(high1)).karatsubaMultiply(low2.add(high2));
        BigInt z2 = high1.karatsubaMultiply(high2);

        BigInt middle = z1.subtract(z2).subtract(z0);
        BigInt result = z2.shiftLeft(2 * half)
                .add(middle.shiftLeft(half))
                .add(z0);

        result.removeLeadingZeros();
        return result;
    }

    /* ---------------- DIVIDE & MOD (naive) ---------------- */

    public BigInt divide(BigInt other) {
        if (other.toString().equals("0")) {
            throw new ArithmeticException("Division by zero");
        }

        BigInt temp = new BigInt(this.toString());
        BigInt one = new BigInt("1");
        BigInt count = new BigInt("0");

        while (temp.compareTo(other) >= 0) {
            temp = temp.subtract(other);
            count = count.add(one);
        }
        return count;
    }

    public BigInt mod(BigInt other) {
        if (other.toString().equals("0")) {
            throw new ArithmeticException("Modulo by zero");
        }

        BigInt temp = new BigInt(this.toString());
        while (temp.compareTo(other) >= 0) {
            temp = temp.subtract(other);
        }
        return temp;
    }

    /* ---------------- COMPARE ---------------- */

    public int compareTo(BigInt other) {
        if (digits.size() != other.digits.size()) {
            return digits.size() - other.digits.size();
        }
        for (int i = digits.size() - 1; i >= 0; i--) {
            if (!digits.get(i).equals(other.digits.get(i))) {
                return digits.get(i) - other.digits.get(i);
            }
        }
        return 0;
    }

    /* ---------------- STRING ---------------- */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isNegative) sb.append("-");
        for (int i = digits.size() - 1; i >= 0; i--) {
            sb.append(digits.get(i));
        }
        return sb.toString();
    }
}

