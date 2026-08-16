package com.example

import java.math.BigDecimal

enum class AngleMode {
  DEG, RAD
}

data class CalculationHistory(
  val id: Long = System.currentTimeMillis(),
  val expression: String,
  val result: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class CalculatorState(
  val expression: String = "",
  val previewResult: String? = null,
  val isCalculated: Boolean = false,
  val errorMessage: String? = null,
  val history: List<CalculationHistory> = emptyList(),
  val isHistoryOpen: Boolean = false,
  val isUnitConverterOpen: Boolean = false,
  val isTerminalOpen: Boolean = false,
  val isExtraFunctionsOpen: Boolean = false,
  val isSecondMode: Boolean = false,
  val angleMode: AngleMode = AngleMode.DEG,
  val memory: BigDecimal = BigDecimal.ZERO,
  val hasMemory: Boolean = false
)

sealed class CalculatorAction {
  data class Number(val digit: Int) : CalculatorAction() // <-- ADDED ()
  data object Decimal : CalculatorAction() // <-- ADDED ()
  data class Operator(val op: CalculatorOp) : CalculatorAction() // <-- ADDED ()
  data class ScientificFunc(val func: ScientificFunction) : CalculatorAction() // <-- ADDED ()
  data class Constant(val constVal: MathConstant) : CalculatorAction() // <-- ADDED ()
  data object OpenParenthesis : CalculatorAction() // <-- ADDED ()
  data object CloseParenthesis : CalculatorAction() // <-- ADDED ()
  data object Equals : CalculatorAction() // <-- ADDED ()
  data object Clear : CalculatorAction() // <-- ADDED ()
  data object AllClear : CalculatorAction() // <-- ADDED ()
  data object Backspace : CalculatorAction() // <-- ADDED ()
  data object ToggleSign : CalculatorAction() // <-- ADDED ()
  data object Percentage : CalculatorAction() // <-- ADDED ()
  data object ToggleSecondMode : CalculatorAction() // <-- ADDED ()
  data object ToggleAngleMode : CalculatorAction() // <-- ADDED ()
  data object ToggleExtraFunctions : CalculatorAction() // <-- ADDED ()
  data object MemoryClear : CalculatorAction() // <-- ADDED ()
  data object MemoryRecall : CalculatorAction() // <-- ADDED ()
  data object MemoryAdd : CalculatorAction() // <-- ADDED ()
  data object MemorySubtract : CalculatorAction() // <-- ADDED ()
  data class SelectHistory(val history: CalculationHistory) : CalculatorAction() // <-- ADDED ()
  data object ToggleHistory : CalculatorAction() // <-- ADDED ()
  data object ClearHistory : CalculatorAction() // <-- ADDED ()
  data object ToggleUnitConverter : CalculatorAction() // <-- ADDED ()
  data object ToggleTerminal : CalculatorAction() // <-- ADDED ()
}

enum class CalculatorOp(val symbol: String, val precedence: Int) {
  ADD("+", 1),
  SUBTRACT("−", 1),
  MULTIPLY("×", 2),
  DIVIDE("÷", 2),
  POWER("^", 3);

  companion object {
    fun fromSymbol(sym: String): CalculatorOp? = when (sym) {
      "+", "\u002B" -> ADD
      "−", "-", "\u2212" -> SUBTRACT
      "×", "*", "\u00D7" -> MULTIPLY
      "÷", "/", "\u00F7" -> DIVIDE
      "^" -> POWER
      else -> null
    }
  }
}

enum class ScientificFunction(val symbol: String, val displayName: String) {
  SIN("sin", "sin"),
  COS("cos", "cos"),
  TAN("tan", "tan"),
  ASIN("asin", "sin⁻¹"),
  ACOS("acos", "cos⁻¹"),
  ATAN("atan", "tan⁻¹"),
  LN("ln", "ln"),
  LOG("log", "log"),
  EXP_N("exp", "eˣ"),
  TEN_POW("10^", "10ˣ"),
  SQRT("√", "√x"),
  CBRT("∛", "∛x"),
  SQUARE("sqr", "x²"),
  CUBE("cube", "x³"),
  INVERSE("inv", "1/x"),
  FACTORIAL("fact", "x!");

  companion object {
    fun fromSymbol(sym: String): ScientificFunction? = entries.find { it.symbol == sym }
  }
}

enum class MathConstant(val symbol: String, val value: Double) {
  PI("π", Math.PI),
  E("e", Math.E)
}
