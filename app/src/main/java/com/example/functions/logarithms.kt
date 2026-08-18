package com.example.functions

import kotlin.math.log
import kotlin.math.log10
import kotlin.math.log2
import kotlin.math.pow

/**
 * Logarithmic functions library (General and common bases).
 * Provides base-10, base-2, and arbitrary base logarithms and anti-logarithms using [kotlin.math].
 */
object Logarithms {

  /**
   * Computes the common logarithm (base 10) of [x].
   * @throws IllegalArgumentException if [x] is non-positive.
   */
  fun log10(x: Double): Double {
    if (x <= 0.0) {
      throw IllegalArgumentException("Logarithm base 10 is only defined for strictly positive numbers")
    }
    return kotlin.math.log10(x)
  }

  /**
   * Computes the binary logarithm (base 2) of [x].
   * @throws IllegalArgumentException if [x] is non-positive.
   */
  fun log2(x: Double): Double {
    if (x <= 0.0) {
      throw IllegalArgumentException("Logarithm base 2 is only defined for strictly positive numbers")
    }
    return kotlin.math.log2(x)
  }

  /**
   * Computes the logarithm of [x] with an arbitrary [base].
   * @throws IllegalArgumentException if [x] is non-positive, or [base] is <= 0 or equal to 1.
   */
  fun logBase(x: Double, base: Double): Double {
    if (x <= 0.0) {
      throw IllegalArgumentException("Logarithm argument must be strictly positive")
    }
    if (base <= 0.0 || base == 1.0) {
      throw IllegalArgumentException("Logarithm base must be positive and not equal to 1")
    }
    return log(x, base)
  }

  /**
   * Computes 10 raised to the power of [x] (anti-logarithm base 10).
   */
  fun pow10(x: Double): Double = 10.0.pow(x)

  /**
   * Computes 2 raised to the power of [x] (anti-logarithm base 2).
   */
  fun pow2(x: Double): Double = 2.0.pow(x)
}
