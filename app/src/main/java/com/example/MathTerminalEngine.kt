package com.example

import com.example.functions.Arithmetic
import com.example.functions.Logarithms
import com.example.functions.NaturalLogarithms
import com.example.functions.Trigonometry
import java.text.DecimalFormat

/**
 * Interactive Kotlin Math Shell Engine.
 * Evaluates math expressions, variables, and function calls from com.example.functions
 * without relying on external runtimes or Python.
 */
class MathTerminalEngine {

  private val variables = mutableMapOf<String, Double>(
    "pi" to Trigonometry.PI_VALUE,
    "PI" to Trigonometry.PI_VALUE,
    "e" to NaturalLogarithms.EULER_E,
    "E" to NaturalLogarithms.EULER_E
  )

  private val format = DecimalFormat("#.##########")

  fun getVariables(): Map<String, Double> = variables.toMap()

  fun reset() {
    variables.clear()
    variables["pi"] = Trigonometry.PI_VALUE
    variables["PI"] = Trigonometry.PI_VALUE
    variables["e"] = NaturalLogarithms.EULER_E
    variables["E"] = NaturalLogarithms.EULER_E
  }

  fun execute(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return ""

    if (trimmed == "help" || trimmed == "help()") {
      return """
        Kotlin Math Shell:
        • Arithmetic: add(a,b), subtract(a,b), multiply(a,b), divide(a,b), power(a,b), sqrt(x), cbrt(x), abs(x), mod(a,b), factorial(n), gcd(a,b), lcm(a,b)
        • Logarithms: log10(x), log2(x), logBase(x, base), pow10(x), pow2(x)
        • Natural Log: ln(x), ln1p(x), exp(x), expm1(x)
        • Trigonometry: sin(x), cos(x), tan(x), asin(x), acos(x), atan(x), atan2(y,x), sinh(x), cosh(x), tanh(x), toRadians(deg), toDegrees(rad)
        • Variables: x = 42, radius = 5, area = pi * radius^2
        • Operators: +, -, *, /, %, ^, **
        • Commands: vars, clear, reset, help
      """.trimIndent()
    }

    if (trimmed == "vars" || trimmed == "vars()") {
      return if (variables.isEmpty()) "No variables defined."
      else variables.entries.joinToString("\n") { "${it.key} = ${format.format(it.value)}" }
    }

    if (trimmed == "clear" || trimmed == "clear()") {
      return "CLEAR_SIGNAL"
    }

    if (trimmed == "reset" || trimmed == "reset()") {
      reset()
      return "Environment reset. Available constants: pi, e"
    }

    // Variable assignment: e.g. x = 10, radius = 5 + 2
    if (trimmed.contains("=") && !trimmed.contains("==") && !trimmed.contains("!=") && !trimmed.contains("<=") && !trimmed.contains(">=")) {
      val eqIdx = trimmed.indexOf('=')
      val varName = trimmed.substring(0, eqIdx).trim()
      val expr = trimmed.substring(eqIdx + 1).trim()

      if (!isValidIdentifier(varName)) {
        throw IllegalArgumentException("Invalid variable name '$varName'")
      }

      val value = eval(expr)
      variables[varName] = value
      return "$varName = ${format.format(value)}"
    }

    val result = eval(trimmed)
    return format.format(result)
  }

  private fun isValidIdentifier(name: String): Boolean {
    if (name.isEmpty() || (!name[0].isLetter() && name[0] != '_')) return false
    return name.all { it.isLetterOrDigit() || it == '_' }
  }

  private sealed interface Token {
    data class Num(val value: Double) : Token
    data class Ident(val name: String) : Token
    data class Op(val symbol: String) : Token
    data object OpenParen : Token
    data object CloseParen : Token
    data object Comma : Token
  }

  private fun tokenize(expr: String): List<Token> {
    val tokens = mutableListOf<Token>()
    var i = 0
    while (i < expr.length) {
      val c = expr[i]
      when {
        c.isWhitespace() -> i++
        c.isDigit() || c == '.' -> {
          val sb = StringBuilder()
          var dotSeen = false
          while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
            if (expr[i] == '.') {
              if (dotSeen) break
              dotSeen = true
            }
            sb.append(expr[i])
            i++
          }
          tokens.add(Token.Num(sb.toString().toDouble()))
        }
        c.isLetter() || c == '_' -> {
          val sb = StringBuilder()
          while (i < expr.length && (expr[i].isLetterOrDigit() || expr[i] == '_')) {
            sb.append(expr[i])
            i++
          }
          tokens.add(Token.Ident(sb.toString()))
        }
        c == '(' -> {
          tokens.add(Token.OpenParen)
          i++
        }
        c == ')' -> {
          tokens.add(Token.CloseParen)
          i++
        }
        c == ',' -> {
          tokens.add(Token.Comma)
          i++
        }
        c == '*' -> {
          if (i + 1 < expr.length && expr[i + 1] == '*') {
            tokens.add(Token.Op("**"))
            i += 2
          } else {
            tokens.add(Token.Op("*"))
            i++
          }
        }
        c == '+' || c == '-' || c == '/' || c == '%' || c == '^' -> {
          tokens.add(Token.Op(c.toString()))
          i++
        }
        else -> throw IllegalArgumentException("Unexpected character '$c' in expression")
      }
    }
    return tokens
  }

  fun eval(expr: String): Double {
    val tokens = tokenize(expr)
    var pos = 0

    fun peek(): Token? = if (pos < tokens.size) tokens[pos] else null
    fun consume(): Token = tokens[pos++]

    fun parsePrimary(): Double {
      val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")

      when (token) {
        is Token.Num -> {
          consume()
          return token.value
        }
        is Token.Ident -> {
          consume()
          val name = token.name

          // Check if function call
          if (peek() is Token.OpenParen) {
            consume() // consume '('
            val args = mutableListOf<Double>()
            if (peek() !is Token.CloseParen) {
              args.add(evalExpressionInParser(::parsePrimary, ::peek, ::consume))
              while (peek() is Token.Comma) {
                consume() // consume ','
                args.add(evalExpressionInParser(::parsePrimary, ::peek, ::consume))
              }
            }
            if (peek() !is Token.CloseParen) {
              throw IllegalArgumentException("Expected ')' after function arguments")
            }
            consume() // consume ')'
            return callFunction(name, args)
          }

          // Otherwise variable / constant lookup
          if (variables.containsKey(name)) {
            return variables[name]!!
          }
          throw IllegalArgumentException("Undefined identifier '$name'")
        }
        is Token.OpenParen -> {
          consume()
          val inside = evalExpressionInParser(::parsePrimary, ::peek, ::consume)
          if (peek() !is Token.CloseParen) {
            throw IllegalArgumentException("Missing closing parenthesis ')'")
          }
          consume()
          return inside
        }
        else -> throw IllegalArgumentException("Unexpected token: $token")
      }
    }

    val finalResult = evalExpressionInParser(::parsePrimary, ::peek, ::consume)
    if (pos < tokens.size) {
      throw IllegalArgumentException("Unexpected token after expression: ${tokens[pos]}")
    }
    return finalResult
  }

  private fun evalExpressionInParser(
    parsePrimaryFunc: () -> Double,
    peekFunc: () -> Token?,
    consumeFunc: () -> Token
  ): Double {
    fun parseUnary(): Double {
      val next = peekFunc()
      if (next is Token.Op && next.symbol == "-") {
        consumeFunc()
        return -parseUnary()
      }
      if (next is Token.Op && next.symbol == "+") {
        consumeFunc()
        return parseUnary()
      }
      return parsePrimaryFunc()
    }

    fun parseFactor(): Double {
      var left = parseUnary()
      while (true) {
        val next = peekFunc()
        if (next is Token.Op && (next.symbol == "^" || next.symbol == "**")) {
          consumeFunc()
          val right = parseUnary()
          left = Arithmetic.power(left, right)
        } else {
          break
        }
      }
      return left
    }

    fun parseTerm(): Double {
      var left = parseFactor()
      while (true) {
        val next = peekFunc()
        if (next is Token.Op && (next.symbol == "*" || next.symbol == "/" || next.symbol == "%")) {
          consumeFunc()
          val right = parseFactor()
          left = when (next.symbol) {
            "*" -> Arithmetic.multiply(left, right)
            "/" -> Arithmetic.divide(left, right)
            "%" -> Arithmetic.modulo(left, right)
            else -> left
          }
        } else {
          break
        }
      }
      return left
    }

    var left = parseTerm()
    while (true) {
      val next = peekFunc()
      if (next is Token.Op && (next.symbol == "+" || next.symbol == "-")) {
        consumeFunc()
        val right = parseTerm()
        left = if (next.symbol == "+") Arithmetic.add(left, right) else Arithmetic.subtract(left, right)
      } else {
        break
      }
    }
    return left
  }

  private fun callFunction(name: String, args: List<Double>): Double {
    return when (name.lowercase()) {
      // Arithmetic
      "add" -> {
        requireArgs(name, args, 2)
        Arithmetic.add(args[0], args[1])
      }
      "sub", "subtract" -> {
        requireArgs(name, args, 2)
        Arithmetic.subtract(args[0], args[1])
      }
      "mul", "multiply" -> {
        requireArgs(name, args, 2)
        Arithmetic.multiply(args[0], args[1])
      }
      "div", "divide" -> {
        requireArgs(name, args, 2)
        Arithmetic.divide(args[0], args[1])
      }
      "pow", "power" -> {
        requireArgs(name, args, 2)
        Arithmetic.power(args[0], args[1])
      }
      "sqrt" -> {
        requireArgs(name, args, 1)
        Arithmetic.squareRoot(args[0])
      }
      "cbrt" -> {
        requireArgs(name, args, 1)
        Arithmetic.cubeRoot(args[0])
      }
      "abs" -> {
        requireArgs(name, args, 1)
        Arithmetic.absoluteValue(args[0])
      }
      "mod" -> {
        requireArgs(name, args, 2)
        Arithmetic.modulo(args[0], args[1])
      }
      "fact", "factorial" -> {
        requireArgs(name, args, 1)
        Arithmetic.factorial(args[0].toLong()).toDouble()
      }
      "gcd" -> {
        requireArgs(name, args, 2)
        Arithmetic.gcd(args[0].toLong(), args[1].toLong()).toDouble()
      }
      "lcm" -> {
        requireArgs(name, args, 2)
        Arithmetic.lcm(args[0].toLong(), args[1].toLong()).toDouble()
      }

      // Logarithms
      "log", "log10" -> {
        if (args.size == 1) {
          Logarithms.log10(args[0])
        } else if (args.size == 2) {
          Logarithms.logBase(args[0], args[1])
        } else {
          throw IllegalArgumentException("log takes 1 or 2 arguments")
        }
      }
      "log2" -> {
        requireArgs(name, args, 1)
        Logarithms.log2(args[0])
      }
      "logbase" -> {
        requireArgs(name, args, 2)
        Logarithms.logBase(args[0], args[1])
      }
      "pow10" -> {
        requireArgs(name, args, 1)
        Logarithms.pow10(args[0])
      }
      "pow2" -> {
        requireArgs(name, args, 1)
        Logarithms.pow2(args[0])
      }

      // Natural Logarithms
      "ln" -> {
        requireArgs(name, args, 1)
        NaturalLogarithms.ln(args[0])
      }
      "ln1p" -> {
        requireArgs(name, args, 1)
        NaturalLogarithms.ln1p(args[0])
      }
      "exp" -> {
        requireArgs(name, args, 1)
        NaturalLogarithms.exp(args[0])
      }
      "expm1" -> {
        requireArgs(name, args, 1)
        NaturalLogarithms.expm1(args[0])
      }

      // Trigonometry
      "sin" -> {
        requireArgs(name, args, 1)
        Trigonometry.sin(args[0])
      }
      "cos" -> {
        requireArgs(name, args, 1)
        Trigonometry.cos(args[0])
      }
      "tan" -> {
        requireArgs(name, args, 1)
        Trigonometry.tan(args[0])
      }
      "asin" -> {
        requireArgs(name, args, 1)
        Trigonometry.asin(args[0])
      }
      "acos" -> {
        requireArgs(name, args, 1)
        Trigonometry.acos(args[0])
      }
      "atan" -> {
        requireArgs(name, args, 1)
        Trigonometry.atan(args[0])
      }
      "atan2" -> {
        requireArgs(name, args, 2)
        Trigonometry.atan2(args[0], args[1])
      }
      "sinh" -> {
        requireArgs(name, args, 1)
        Trigonometry.sinh(args[0])
      }
      "cosh" -> {
        requireArgs(name, args, 1)
        Trigonometry.cosh(args[0])
      }
      "tanh" -> {
        requireArgs(name, args, 1)
        Trigonometry.tanh(args[0])
      }
      "toradians", "rad" -> {
        requireArgs(name, args, 1)
        Trigonometry.toRadians(args[0])
      }
      "todegrees", "deg" -> {
        requireArgs(name, args, 1)
        Trigonometry.toDegrees(args[0])
      }
      "sindeg" -> {
        requireArgs(name, args, 1)
        Trigonometry.sinDegrees(args[0])
      }
      "cosdeg" -> {
        requireArgs(name, args, 1)
        Trigonometry.cosDegrees(args[0])
      }
      "tandeg" -> {
        requireArgs(name, args, 1)
        Trigonometry.tanDegrees(args[0])
      }

      else -> throw IllegalArgumentException("Unknown function '$name()'. Type 'help' for available functions.")
    }
  }

  private fun requireArgs(func: String, args: List<Double>, expected: Int) {
    if (args.size != expected) {
      throw IllegalArgumentException("$func() expects $expected arguments, got ${args.size}")
    }
  }
}
