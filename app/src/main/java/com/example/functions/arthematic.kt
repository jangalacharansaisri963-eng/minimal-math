package com.example.functions

import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Arithmetic math operations library.
 * Provides standard and extended arithmetic functions using [kotlin.math].
 */
object Arithmetic {

  /**
   * Computes the sum of [a] and [b].
   */
  fun add(a: Double, b: Double): Double = a + b

  /**
   * Computes the difference of [a] minus [b].
   */
  fun subtract(a: Double, b: Double): Double = a - b

  /**
   * Computes the product of [a] and [b].
   */
  fun multiply(a: Double, b: Double): Double = a * b

  /**
   * Computes the division of [a] by [b].
   * @throws IllegalArgumentException if [b] is zero.
   */
  fun divide(a: Double, b: Double): Double {
    if (b == 0.0) {
      throw IllegalArgumentException("Division by zero is undefined")
    }
    return a / b
  }

  /**
   * Computes [base] raised to the power of [exponent].
   */
  fun power(base: Double, exponent: Double): Double = base.pow(exponent)

  /**
   * Computes the square root of [x].
   * @throws IllegalArgumentException if [x] is negative.
   */
  fun squareRoot(x: Double): Double {
    if (x < 0.0) {
      throw IllegalArgumentException("Square root of a negative number is undefined in real numbers")
    }
    return sqrt(x)
  }

  /**
   * Computes the cube root of [x].
   */
  fun cubeRoot(x: Double): Double = cbrt(x)

  /**
   * Computes the absolute value of [x].
   */
  fun absoluteValue(x: Double): Double = abs(x)

  /**
   * Computes the remainder of [a] divided by [b] (modulo).
   * @throws IllegalArgumentException if [b] is zero.
   */
  fun modulo(a: Double, b: Double): Double {
    if (b == 0.0) {
      throw IllegalArgumentException("Modulo by zero is undefined")
    }
    return a % b
  }

  /**
   * Computes percentage: returns ([value] / 100.0).
   */
  fun percentage(value: Double): Double = value / 100.0

  /**
   * Computes the factorial of a non-negative integer [n] (n!).
   * Supports up to n = 20 without 64-bit Long overflow.
   * @throws IllegalArgumentException if [n] is negative or greater than 20.
   */
  fun factorial(n: Long): Long {
    if (n < 0) {
      throw IllegalArgumentException("Factorial is not defined for negative numbers")
    }
    if (n > 20) {
      throw IllegalArgumentException("Factorial of $n exceeds 64-bit integer capacity (max 20)")
    }
    var result = 1L
    for (i in 2..n) {
      result *= i
    }
    return result
  }

  /**
   * Computes the Greatest Common Divisor (GCD) of [a] and [b] using the Euclidean algorithm.
   */
  fun gcd(a: Long, b: Long): Long {
    var x = kotlin.math.abs(a)
    var y = kotlin.math.abs(b)
    while (y != 0L) {
      val temp = y
      y = x % y
      x = temp
    }
    return x
  }

  /**
   * Computes the Least Common Multiple (LCM) of [a] and [b].
   */
  fun lcm(a: Long, b: Long): Long {
    if (a == 0L || b == 0L) return 0L
    val gcdVal = gcd(a, b)
    return (kotlin.math.abs(a) / gcdVal) * kotlin.math.abs(b)
  }
}
