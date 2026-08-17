package com.example

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

object CalculatorFormatter {
  private val symbols = DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = ','
    decimalSeparator = '.'
  }

  fun formatNumber(numberStr: String): String {
    if (numberStr.isEmpty() || numberStr == "-" || numberStr == ".") return numberStr

    val isNegative = numberStr.startsWith("-")
    val absStr = if (isNegative) numberStr.substring(1) else numberStr
    val parts = absStr.split(".")

    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else null
    val hasTrailingDot = numberStr.endsWith(".")

    val formattedInteger = try {
      if (integerPart.isEmpty()) "0"
      else {
        val bd = BigDecimal(integerPart)
        val formatter = DecimalFormat("#,##0", symbols)
        formatter.format(bd)
      }
    } catch (_: Exception) {
      integerPart
    }

    return buildString {
      if (isNegative) append("-")
      append(formattedInteger)
      if (hasTrailingDot) {
        append(".")
      } else if (decimalPart != null) {
        append(".")
        append(decimalPart)
      }
    }
  }

  fun formatBigDecimal(bd: BigDecimal): String {
    val stripped = bd.stripTrailingZeros()
    val plain = stripped.toPlainString()

    if (plain.length > 15 || plain.contains("E") || plain.contains("e")) {
      return DecimalFormat("0.######E0", symbols).format(bd)
    }

    return formatNumber(plain)
  }
}

object CalculatorEvaluator {
  private const val SCALE = 12

  fun evaluate(expression: String, angleMode: AngleMode = AngleMode.DEG): Result<BigDecimal> = runCatching {
    val cleanExpr = cleanExpression(expression)
    if (cleanExpr.isBlank()) throw IllegalArgumentException("Empty expression")

    val tokens = tokenize(cleanExpr)
    if (tokens.isEmpty()) throw IllegalArgumentException("No tokens")

    // Shunting-yard algorithm
    val output = mutableListOf<String>()
    val stack = mutableListOf<String>()

    for (token in tokens) {
      when {
        isNumber(token) -> output.add(token)
        token == "π" -> output.add(Math.PI.toString())
        token == "e" -> output.add(Math.E.toString())
        isFunction(token) -> stack.add(token)
        token == "%" -> output.add("%")
        token == "(" -> stack.add(token)
        token == ")" -> {
          while (stack.isNotEmpty() && stack.last() != "(") {
            output.add(stack.removeAt(stack.size - 1))
          }
          if (stack.isNotEmpty() && stack.last() == "(") {
            stack.removeAt(stack.size - 1)
          }
          if (stack.isNotEmpty() && isFunction(stack.last())) {
            output.add(stack.removeAt(stack.size - 1))
          }
        }
        else -> {
          val op = CalculatorOp.fromSymbol(token)
          if (op != null) {
            while (stack.isNotEmpty() && (isFunction(stack.last()) || hasHigherOrEqualPrecedence(stack.last(), op))) {
              output.add(stack.removeAt(stack.size - 1))
            }
            stack.add(token)
          }
        }
      }
    }

    while (stack.isNotEmpty()) {
      val top = stack.removeAt(stack.size - 1)
      if (top != "(" && top != ")") output.add(top)
    }

    if (output.isEmpty()) throw IllegalArgumentException("Nothing to evaluate")

    // RPN Evaluation
    val evalStack = mutableListOf<Double>()
    for (token in output) {
      when {
        isNumber(token) -> evalStack.add(token.toDouble())
        token == "%" -> {
          if (evalStack.isEmpty()) throw IllegalArgumentException("Invalid %")
          val a = evalStack.removeAt(evalStack.size - 1)
          evalStack.add(a / 100.0)
        }
        isFunction(token) -> {
          if (evalStack.isEmpty()) throw IllegalArgumentException("Invalid function argument")
          val arg = evalStack.removeAt(evalStack.size - 1)
          val result = evaluateFunction(token, arg, angleMode)
          evalStack.add(result)
        }
        else -> {
          val op = CalculatorOp.fromSymbol(token)
          if (op != null) {
            if (evalStack.size < 2) throw IllegalArgumentException("Invalid syntax")
            val b = evalStack.removeAt(evalStack.size - 1)
            val a = evalStack.removeAt(evalStack.size - 1)
            val res = when (op) {
              CalculatorOp.ADD -> a + b
              CalculatorOp.SUBTRACT -> a - b
              CalculatorOp.MULTIPLY -> a * b
              CalculatorOp.DIVIDE -> {
                if (b == 0.0) throw ArithmeticException("Cannot divide by 0")
                a / b
              }
              CalculatorOp.POWER -> a.pow(b)
            }
            evalStack.add(res)
          }
        }
      }
    }

    if (evalStack.size != 1) throw IllegalArgumentException("Evaluation error")
    val finalVal = evalStack.first()
    if (finalVal.isNaN()) throw ArithmeticException("Math error")
    if (finalVal.isInfinite()) throw ArithmeticException("Number overflow")

    BigDecimal.valueOf(finalVal).round(MathContext(SCALE, RoundingMode.HALF_UP)).stripTrailingZeros()
  }

  private fun cleanExpression(expr: String): String {
    var s = expr.trim()
    while (s.isNotEmpty() && (isOperatorChar(s.last()) || s.last() == '(')) {
      s = s.dropLast(1).trim()
    }
    val openCount = s.count { it == '(' }
    val closeCount = s.count { it == ')' }
    if (openCount > closeCount) {
      s += ")".repeat(openCount - closeCount)
    }
    return s
  }

  private fun isOperatorChar(c: Char): Boolean = c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^'

  private fun hasHigherOrEqualPrecedence(topOpSymbol: String, incomingOp: CalculatorOp): Boolean {
    val topOp = CalculatorOp.fromSymbol(topOpSymbol) ?: return false
    return if (incomingOp == CalculatorOp.POWER) {
      topOp.precedence > incomingOp.precedence
    } else {
      topOp.precedence >= incomingOp.precedence
    }
  }

  private fun isNumber(token: String): Boolean = token.toDoubleOrNull() != null

  private fun isFunction(token: String): Boolean = ScientificFunction.fromSymbol(token) != null

  private fun evaluateFunction(funcSymbol: String, x: Double, angleMode: AngleMode): Double {
    val radians = if (angleMode == AngleMode.DEG) Math.toRadians(x) else x
    return when (funcSymbol) {
      "sin" -> sin(radians)
      "cos" -> cos(radians)
      "tan" -> tan(radians)
      "asin" -> {
        val rad = asin(x)
        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
      }
      "acos" -> {
        val rad = acos(x)
        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
      }
      "atan" -> {
        val rad = atan(x)
        if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
      }
      "ln" -> {
        if (x <= 0) throw ArithmeticException("Math domain error")
        ln(x)
      }
      "log" -> {
        if (x <= 0) throw ArithmeticException("Math domain error")
        log10(x)
      }
      "exp" -> exp(x)
      "10^" -> 10.0.pow(x)
      "√" -> {
        if (x < 0) throw ArithmeticException("Math domain error")
        sqrt(x)
      }
      "∛" -> cbrt(x)
      "sqr" -> x * x
      "cube" -> x * x * x
      "inv" -> {
        if (x == 0.0) throw ArithmeticException("Cannot divide by 0")
        1.0 / x
      }
      "fact" -> {
        val n = x.toInt()
        if (n < 0 || n > 170) throw ArithmeticException("Factorial out of range")
        var res = 1.0
        for (k in 2..n) res *= k
        res
      }
      else -> throw IllegalArgumentException("Unknown function: $funcSymbol")
    }
  }

  fun tokenize(expr: String): List<String> {
    val tokens = mutableListOf<String>()
    var i = 0
    val len = expr.length

    while (i < len) {
      val c = expr[i]

      if (c.isWhitespace()) {
        i++
        continue
      }

      var matchedFunc: String? = null
      for (fn in ScientificFunction.entries) {
        if (expr.startsWith(fn.symbol, i)) {
          matchedFunc = fn.symbol
          break
        }
      }

      if (matchedFunc != null) {
        tokens.add(matchedFunc)
        i += matchedFunc.length
        continue
      }

      if (c == 'π') {
        tokens.add("π")
        i++
        continue
      }
      if (c == 'e' && (i + 1 >= len || (!expr[i + 1].isLetterOrDigit() && expr[i + 1] != '^'))) {
        tokens.add("e")
        i++
        continue
      }

      if (c == '(' || c == ')' || c == '%') {
        tokens.add(c.toString())
        i++
        continue
      }

      val isOp = c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^'
      if (isOp) {
        val opSymbol = when (c) {
          '+' -> "+"
          '−', '-' -> "−"
          '×', '*' -> "×"
          '÷', '/' -> "÷"
          '^' -> "^"
          else -> c.toString()
        }

        val isUnaryMinus = (c == '−' || c == '-') && (tokens.isEmpty() || tokens.last() == "(" || CalculatorOp.fromSymbol(tokens.last()) != null)
        if (isUnaryMinus && i + 1 < len && (expr[i + 1].isDigit() || expr[i + 1] == '.')) {
          var numStr = "-"
          i++
          while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
            numStr += expr[i]
            i++
          }
          tokens.add(numStr)
          continue
        }

        tokens.add(opSymbol)
        i++
        continue
      }

      if (c.isDigit() || c == '.') {
        var numStr = ""
        while (i < len && (expr[i].isDigit() || expr[i] == '.')) {
          numStr += expr[i]
          i++
        }
        tokens.add(numStr)
        continue
      }

      i++
    }

    return tokens
  }
}
