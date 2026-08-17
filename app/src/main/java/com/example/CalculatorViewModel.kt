package com.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {

  private val _state = MutableStateFlow(CalculatorState())
  val state: StateFlow<CalculatorState> = _state.asStateFlow()

  fun onAction(action: CalculatorAction) {
    when (action) {
      is CalculatorAction.Number -> handleNumber(action.digit)
      is CalculatorAction.Decimal -> handleDecimal()
      is CalculatorAction.Operator -> handleOperator(action.op)
      is CalculatorAction.ScientificFunc -> handleScientificFunc(action.func)
      is CalculatorAction.Constant -> handleConstant(action.constVal)
      is CalculatorAction.OpenParenthesis -> handleOpenParenthesis()
      is CalculatorAction.CloseParenthesis -> handleCloseParenthesis()
      is CalculatorAction.Equals -> handleEquals()
      is CalculatorAction.Clear -> handleClear()
      is CalculatorAction.AllClear -> handleAllClear()
      is CalculatorAction.Backspace -> handleBackspace()
      is CalculatorAction.ToggleSign -> handleToggleSign()
      is CalculatorAction.Percentage -> handlePercentage()
      is CalculatorAction.ToggleSecondMode -> _state.update { it.copy(isSecondMode = !it.isSecondMode) }
      is CalculatorAction.ToggleAngleMode -> _state.update {
        val newMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        val updated = it.copy(angleMode = newMode)
        updated.copy(previewResult = computePreview(updated))
      }
      is CalculatorAction.ToggleExtraFunctions -> _state.update { it.copy(isExtraFunctionsOpen = !it.isExtraFunctionsOpen) }
      is CalculatorAction.ToggleUnitConverter -> _state.update { it.copy(isUnitConverterOpen = !it.isUnitConverterOpen) }
      is CalculatorAction.MemoryClear -> _state.update { it.copy(memory = BigDecimal.ZERO, hasMemory = false) }
      is CalculatorAction.MemoryRecall -> handleMemoryRecall()
      is CalculatorAction.MemoryAdd -> handleMemoryModify(add = true)
      is CalculatorAction.MemorySubtract -> handleMemoryModify(add = false)
      is CalculatorAction.SelectHistory -> handleSelectHistory(action.history)
      is CalculatorAction.ToggleHistory -> _state.update { it.copy(isHistoryOpen = !it.isHistoryOpen) }
      is CalculatorAction.ClearHistory -> _state.update { it.copy(history = emptyList()) }
      is CalculatorAction.ToggleTerminal -> _state.update { it.copy(isTerminalOpen = !it.isTerminalOpen) }
    }
  }

  private fun handleNumber(digit: Int) {
    _state.update { current ->
      val newExpr = if (current.isCalculated || current.errorMessage != null) {
        digit.toString()
      } else {
        if (current.expression == "0") digit.toString() else current.expression + digit.toString()
      }
      val updated = current.copy(expression = newExpr, isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleDecimal() {
    _state.update { current ->
      if (current.isCalculated || current.errorMessage != null || current.expression.isEmpty()) {
        val updated = current.copy(expression = "0.", isCalculated = false, errorMessage = null)
        return@update updated.copy(previewResult = computePreview(updated))
      }
      val lastNum = getTrailingNumberToken(current.expression)
      if (lastNum != null && lastNum.contains(".")) return@update current

      val lastChar = current.expression.last()
      val newExpr = if (isOperator(lastChar) || lastChar == '(') current.expression + "0." else current.expression + "."
      val updated = current.copy(expression = newExpr)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleOperator(op: CalculatorOp) {
    _state.update { current ->
      if (current.errorMessage != null) return@update current
      val baseExpr = when {
        current.isCalculated -> current.expression
        current.expression.isEmpty() -> "0"
        else -> current.expression
      }
      val cleanBase = if (baseExpr.isNotEmpty() && isOperator(baseExpr.last())) baseExpr.dropLast(1) else baseExpr
      val updated = current.copy(expression = cleanBase + op.symbol, isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleScientificFunc(func: ScientificFunction) {
    _state.update { current ->
      val effectiveFunc = if (current.isSecondMode) {
        when (func) {
          ScientificFunction.SIN -> ScientificFunction.ASIN
          ScientificFunction.COS -> ScientificFunction.ACOS
          ScientificFunction.TAN -> ScientificFunction.ATAN
          ScientificFunction.LN -> ScientificFunction.EXP_N
          ScientificFunction.LOG -> ScientificFunction.TEN_POW
          ScientificFunction.SQRT -> ScientificFunction.SQUARE
          ScientificFunction.CBRT -> ScientificFunction.CUBE
          else -> func
        }
      } else func

      val isUnary = effectiveFunc in listOf(ScientificFunction.SQUARE, ScientificFunction.CUBE, ScientificFunction.INVERSE, ScientificFunction.FACTORIAL)
      if (isUnary) {
        val target = if (current.expression.isNotEmpty()) current.expression else "0"
        CalculatorEvaluator.evaluate("${effectiveFunc.symbol}($target)", current.angleMode).fold(
          onSuccess = { res -> current.copy(expression = CalculatorFormatter.formatBigDecimal(res), isCalculated = true, errorMessage = null, previewResult = null) },
          onFailure = { err -> current.copy(errorMessage = err.message ?: "Error") }
        )
      } else {
        val base = if (current.isCalculated) "" else current.expression
        val updated = current.copy(expression = base + effectiveFunc.symbol + "(", isCalculated = false, errorMessage = null)
        updated.copy(previewResult = computePreview(updated))
      }
    }
  }

  private fun handleConstant(constVal: MathConstant) {
    _state.update { current ->
      val base = if (current.isCalculated || current.expression == "0") "" else current.expression
      val updated = current.copy(expression = base + constVal.symbol, isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleOpenParenthesis() {
    _state.update { current ->
      val base = if (current.isCalculated || current.expression == "0") "" else current.expression
      val updated = current.copy(expression = base + "(", isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleCloseParenthesis() {
    _state.update { current ->
      if (current.expression.isEmpty()) return@update current
      val updated = current.copy(expression = current.expression + ")", isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handlePercentage() {
    _state.update { current ->
      if (current.expression.isEmpty() || isOperator(current.expression.last()) || current.expression.last() == '(' || current.expression.last() == '%') return@update current
      val updated = current.copy(expression = current.expression + "%", isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleToggleSign() {
    _state.update { current ->
      if (current.expression.isEmpty() || current.expression == "0") return@update current
      val expr = current.expression
      val lastNumber = getTrailingNumberToken(expr) ?: return@update current
      val idx = expr.lastIndexOf(lastNumber)
      val newExpr = when {
        idx > 0 && expr[idx - 1] == '-' -> expr.substring(0, idx - 1) + lastNumber
        idx >= 2 && expr.substring(idx - 2, idx) == "(-" -> expr.substring(0, idx - 2) + lastNumber
        else -> expr.substring(0, idx) + "(-" + lastNumber + ")"
      }
      val updated = current.copy(expression = newExpr)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleEquals() {
    _state.update { current ->
      if (current.expression.isEmpty()) return@update current
      CalculatorEvaluator.evaluate(current.expression, current.angleMode).fold(
        onSuccess = { res ->
          val fmt = CalculatorFormatter.formatBigDecimal(res)
          current.copy(expression = fmt, previewResult = null, isCalculated = true, errorMessage = null, history = listOf(CalculationHistory(expression = current.expression, result = fmt)) + current.history.take(29))
        },
        onFailure = { err -> current.copy(errorMessage = err.message ?: "Error", previewResult = null, isCalculated = true) }
      )
    }
  }

  private fun handleClear() = _state.update { it.copy(expression = "", previewResult = null, isCalculated = false, errorMessage = null) }
  private fun handleAllClear() = _state.update { it.copy(expression = "", previewResult = null, isCalculated = false, errorMessage = null) }

  private fun handleBackspace() {
    _state.update { current ->
      if (current.isCalculated || current.errorMessage != null || current.expression.isEmpty()) {
        return@update current.copy(expression = "", previewResult = null, isCalculated = false, errorMessage = null)
      }
      var newExpr = current.expression
      val funcs = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "ln(", "log(", "exp(", "10^(")
      var matched = false
      for (fn in funcs) {
        if (newExpr.endsWith(fn)) {
          newExpr = newExpr.dropLast(fn.length)
          matched = true
          break
        }
      }
      if (!matched) newExpr = newExpr.dropLast(1)
      val updated = current.copy(expression = newExpr)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleMemoryRecall() {
    _state.update { current ->
      if (!current.hasMemory) return@update current
      val formatted = CalculatorFormatter.formatBigDecimal(current.memory)
      val base = if (current.isCalculated) "" else current.expression
      val updated = current.copy(expression = base + formatted, isCalculated = false, errorMessage = null)
      updated.copy(previewResult = computePreview(updated))
    }
  }

  private fun handleMemoryModify(add: Boolean) {
    _state.update { current ->
      val target = if (current.expression.isNotEmpty()) current.expression else "0"
      val valueBd = CalculatorEvaluator.evaluate(target, current.angleMode).getOrElse { BigDecimal.ZERO }
      val newMem = if (add) current.memory.add(valueBd) else current.memory.subtract(valueBd)
      current.copy(memory = newMem, hasMemory = true)
    }
  }

  private fun handleSelectHistory(item: CalculationHistory) {
    _state.update { it.copy(expression = item.result, previewResult = null, isCalculated = true, isHistoryOpen = false, errorMessage = null) }
  }

  private fun computePreview(state: CalculatorState): String? {
    if (state.expression.isEmpty() || state.isCalculated || state.expression.toDoubleOrNull() != null) return null
    val eval = CalculatorEvaluator.evaluate(state.expression, state.angleMode).getOrNull() ?: return null
    return CalculatorFormatter.formatBigDecimal(eval)
  }

  private fun isOperator(c: Char): Boolean = c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^'

  private fun getTrailingNumberToken(expr: String): String? {
    var i = expr.length - 1
    while (i >= 0 && (expr[i].isDigit() || expr[i] == '.')) i--
    val start = i + 1
    return if (start < expr.length) expr.substring(start) else null
  }
}
