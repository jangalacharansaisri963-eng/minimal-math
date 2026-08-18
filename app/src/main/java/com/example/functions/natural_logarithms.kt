package com.example.functions

import kotlin.math.E
import kotlin.math.exp
import kotlin.math.expm1
import kotlin.math.ln
import kotlin.math.ln1p

/**
 * Natural Logarithm (base e) and Exponential functions library.
 * Provides natural logarithm and Euler's constant operations using [kotlin.math].
 */
object NaturalLogarithms {

  /**
   * Euler's number constant (e ≈ 2.718281828459045).
   */
  const val EULER_E: Double = E

  /**
   * Computes the natural logarithm (base e) of [x], ln(x).
   * @throws IllegalArgumentException if [x] is non-positive.
   */
  fun ln(x: Double): Double {
    if (x <= 0.0) {
      throw IllegalArgumentException("Natural logarithm is only defined for strictly positive numbers")
    }
    return kotlin.math.ln(x)
  }

  /**
   * Computes the natural logarithm of (1 + [x]), ln(1 + x), accurately even for small [x].
   * @throws IllegalArgumentException if [x] <= -1.0.
   */
  fun ln1p(x: Double): Double {
    if (x <= -1.0) {
      throw IllegalArgumentException("ln1p argument must be strictly greater than -1.0")
    }
    return kotlin.math.ln1p(x)
  }

  /**
   * Computes Euler's number raised to the power of [x] (e^x).
   */
  fun exp(x: Double): Double = kotlin.math.exp(x)

  /**
   * Computes (e^[x] - 1), accurately even for values of [x] close to zero.
   */
  fun expm1(x: Double): Double = kotlin.math.expm1(x)
}
