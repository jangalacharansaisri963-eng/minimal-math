package com.example.functions

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.cosh
import kotlin.math.sin
import kotlin.math.sinh
import kotlin.math.tan
import kotlin.math.tanh

/**
 * Trigonometric and Hyperbolic functions library.
 * Provides circular and hyperbolic functions and degree/radian angle conversions using [kotlin.math].
 */
object Trigonometry {

  /**
   * The mathematical constant Pi (π ≈ 3.141592653589793).
   */
  const val PI_VALUE: Double = PI

  /**
   * Converts an angle measured in degrees to radians.
   */
  fun toRadians(degrees: Double): Double = Math.toRadians(degrees)

  /**
   * Converts an angle measured in radians to degrees.
   */
  fun toDegrees(radians: Double): Double = Math.toDegrees(radians)

  /**
   * Computes the sine of an angle given in [radians].
   */
  fun sin(radians: Double): Double = kotlin.math.sin(radians)

  /**
   * Computes the cosine of an angle given in [radians].
   */
  fun cos(radians: Double): Double = kotlin.math.cos(radians)

  /**
   * Computes the tangent of an angle given in [radians].
   */
  fun tan(radians: Double): Double = kotlin.math.tan(radians)

  /**
   * Computes the arc sine (inverse sine) of [x] in radians.
   * Returns a value in the range -π/2 through π/2.
   * @throws IllegalArgumentException if [x] is outside [-1.0, 1.0].
   */
  fun asin(x: Double): Double {
    if (x < -1.0 || x > 1.0) {
      throw IllegalArgumentException("asin domain error: argument must be between -1.0 and 1.0")
    }
    return kotlin.math.asin(x)
  }

  /**
   * Computes the arc cosine (inverse cosine) of [x] in radians.
   * Returns a value in the range 0.0 through π.
   * @throws IllegalArgumentException if [x] is outside [-1.0, 1.0].
   */
  fun acos(x: Double): Double {
    if (x < -1.0 || x > 1.0) {
      throw IllegalArgumentException("acos domain error: argument must be between -1.0 and 1.0")
    }
    return kotlin.math.acos(x)
  }

  /**
   * Computes the arc tangent (inverse tangent) of [x] in radians.
   * Returns a value in the range -π/2 through π/2.
   */
  fun atan(x: Double): Double = kotlin.math.atan(x)

  /**
   * Computes the angle theta from the conversion of rectangular coordinates ([x], [y]) to polar coordinates.
   */
  fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)

  /**
   * Computes the hyperbolic sine of [x].
   */
  fun sinh(x: Double): Double = kotlin.math.sinh(x)

  /**
   * Computes the hyperbolic cosine of [x].
   */
  fun cosh(x: Double): Double = kotlin.math.cosh(x)

  /**
   * Computes the hyperbolic tangent of [x].
   */
  fun tanh(x: Double): Double = kotlin.math.tanh(x)

  /**
   * Convenience function: computes sine with input angle specified in [degrees].
   */
  fun sinDegrees(degrees: Double): Double = kotlin.math.sin(toRadians(degrees))

  /**
   * Convenience function: computes cosine with input angle specified in [degrees].
   */
  fun cosDegrees(degrees: Double): Double = kotlin.math.cos(toRadians(degrees))

  /**
   * Convenience function: computes tangent with input angle specified in [degrees].
   */
  fun tanDegrees(degrees: Double): Double = kotlin.math.tan(toRadians(degrees))
}
