package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) { // Screen calls this
        when (action) {
            // UI Toggles
            CalculatorAction.ToggleAngleMode -> toggleAngleMode()
            CalculatorAction.ToggleTerminal -> _state.update { it.copy(isTerminalOpen = !it.isTerminalOpen) }
            CalculatorAction.ToggleUnitConverter -> _state.update { it.copy(isUnitConverterOpen = !it.isUnitConverterOpen) }
            CalculatorAction.ToggleHistory -> _state.update { it.copy(isHistoryOpen = !it.isHistoryOpen) }
            CalculatorAction.ToggleExtraFunctions -> _state.update { it.copy(isExtraFunctionsOpen = !it.isExtraFunctionsOpen) }
            CalculatorAction.ToggleSecondMode -> _state.update { it.copy(isSecondMode = !it.isSecondMode) }
            
            // History
            CalculatorAction.ClearHistory -> _state.update { it.copy(history = emptyList()) }
            is CalculatorAction.SelectHistory -> _state.update { it.copy(expression = action.history.expression) }
            
            // Memory
            CalculatorAction.MemoryClear -> _state.update { it.copy(hasMemory = false, memory = BigDecimal.ZERO) }
            CalculatorAction.MemoryAdd -> addToMemory()
            CalculatorAction.MemorySubtract -> subtractFromMemory()
            CalculatorAction.MemoryRecall -> recallMemory()
            
            // TODO: Wire these up to your existing keypad logic
            is CalculatorAction.Number -> append(action.digit.toString())
            CalculatorAction.Decimal -> append(".")
            is CalculatorAction.Operator -> append(action.op.symbol)
            is CalculatorAction.ScientificFunc -> append(action.func.symbol + "(")
            is CalculatorAction.Constant -> append(action.constVal.symbol)
            CalculatorAction.OpenParenthesis -> append("(")
            CalculatorAction.CloseParenthesis -> append(")")
            CalculatorAction.Equals -> evaluate()
            CalculatorAction.Clear -> clear()
            CalculatorAction.AllClear -> allClear()
            CalculatorAction.Backspace -> delete()
            CalculatorAction.ToggleSign -> toggleSign()
            CalculatorAction.Percentage -> percentage()
        }
    }

    // Keep your old function too for the keypad during migration
    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> allClear()
            "C" -> clear()
            "DEL" -> delete()
            "=" -> evaluate()
            else -> append(button)
        }
    }

    private fun append(value: String) {
        _state.update { 
            it.copy(expression = it.expression + value, errorMessage = null) 
        }
    }

    private fun delete() {
        _state.update { it.copy(expression = it.expression.dropLast(1)) }
    }

    private fun clear() {
        _state.update { it.copy(expression = "", previewResult = null, errorMessage = null) }
    }

    private fun allClear() {
        _state.update { CalculatorState(angleMode = it.angleMode) }
    }

    private fun toggleSign() {
        // TODO: implement
    }

    private fun percentage() {
        // TODO: implement
    }

    private fun addToMemory() {
        _state.value.previewResult?.toBigDecimalOrNull()?.let { result ->
            _state.update { it.copy(memory = it.memory + result, hasMemory = true) }
        }
    }

    private fun subtractFromMemory() {
        _state.value.previewResult?.toBigDecimalOrNull()?.let { result ->
            _state.update { it.copy(memory = it.memory - result, hasMemory = true) }
        }
    }

    private fun recallMemory() {
        _state.update { it.copy(expression = it.expression + it.memory.toPlainString()) }
    }

    private fun toggleAngleMode() {
        _state.update { 
            it.copy(angleMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG) 
        }
    }

    private fun evaluate() {
        val expr = _state.value.expression
        if (expr.isBlank()) return

        viewModelScope.launch {
            evaluateViaPython(expr, _state.value.angleMode)
                .onSuccess { result ->
                    val newHistoryItem = CalculationHistory(
                        expression = expr,
                        result = formatResult(result)
                    )
                    _state.update { 
                        it.copy(
                            previewResult = formatResult(result),
                            expression = formatResult(result),
                            errorMessage = null,
                            history = it.history + newHistoryItem,
                            isCalculated = true
                        ) 
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(
                            previewResult = null,
                            errorMessage = "Error: ${e.message}",
                            isCalculated = false
                        ) 
                    }
                }
        }
    }

    private fun evaluateViaPython(expression: String, angleMode: AngleMode): Result<BigDecimal> {
        return try {
            val py = Python.getInstance()
            val calcModule = py.getModule("calc")
            val resultPy = calcModule.callAttr("calculate", expression, angleMode.name)
            Result.success(BigDecimal(resultPy.toString()))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatResult(value: BigDecimal): String {
        return value.stripTrailingZeros().toPlainString()
    }
}
