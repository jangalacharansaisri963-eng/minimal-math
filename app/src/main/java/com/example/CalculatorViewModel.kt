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

enum class AngleMode { DEG, RAD }

data class CalculatorState(
    val expression: String = "",
    val previewResult: String? = null, // was result
    val errorMessage: String? = null,  // was isError
    val angleMode: AngleMode = AngleMode.DEG,
    val isExtraFunctionsOpen: Boolean = false,
    val isTerminalOpen: Boolean = false,
    val isUnitConverterOpen: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val history: List<String> = emptyList(),
    val hasMemory: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onAction(action: CalculatorAction) { // Screen calls this
        when (action) {
            CalculatorAction.ToggleAngleMode -> toggleAngleMode()
            CalculatorAction.ToggleTerminal -> _state.update { it.copy(isTerminalOpen = !it.isTerminalOpen) }
            CalculatorAction.ToggleUnitConverter -> _state.update { it.copy(isUnitConverterOpen = !it.isUnitConverterOpen) }
            CalculatorAction.ToggleHistory -> _state.update { it.copy(isHistoryOpen = !it.isHistoryOpen) }
            CalculatorAction.ToggleExtraFunctions -> _state.update { it.copy(isExtraFunctionsOpen = !it.isExtraFunctionsOpen) }
            CalculatorAction.ClearHistory -> _state.update { it.copy(history = emptyList()) }
            is CalculatorAction.SelectHistory -> _state.update { it.copy(expression = action.expr) }
            CalculatorAction.MemoryClear -> _state.update { it.copy(hasMemory = false) }
            CalculatorAction.MemoryAdd -> _state.update { it.copy(hasMemory = true) }
            CalculatorAction.MemorySubtract -> _state.update { it.copy(hasMemory = true) }
            CalculatorAction.MemoryRecall -> {} // implement later
        }
    }

    // Keep your old function too for the keypad
    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> clear()
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
        _state.update { CalculatorState(angleMode = it.angleMode) }
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
                    _state.update { 
                        it.copy(
                            previewResult = formatResult(result), // FIXED
                            expression = formatResult(result),
                            errorMessage = null // FIXED
                        ) 
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(
                            previewResult = null,
                            errorMessage = "Error: ${e.message}" // FIXED
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

// ADD THIS: Screen is calling this sealed class
sealed class CalculatorAction {
    object ToggleAngleMode : CalculatorAction()
    object ToggleTerminal : CalculatorAction()
    object ToggleUnitConverter : CalculatorAction()
    object ToggleHistory : CalculatorAction()
    object ToggleExtraFunctions : CalculatorAction()
    object ClearHistory : CalculatorAction()
    object MemoryClear : CalculatorAction()
    object MemoryAdd : CalculatorAction()
    object MemorySubtract : CalculatorAction()
    object MemoryRecall : CalculatorAction()
    data class SelectHistory(val expr: String) : CalculatorAction()
}
