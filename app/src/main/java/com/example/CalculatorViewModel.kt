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
    val result: String = "0",
    val angleMode: AngleMode = AngleMode.DEG,
    val isError: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> clear()
            "DEL" -> delete()
            "=" -> evaluate()
            "DEG", "RAD" -> toggleAngleMode()
            else -> append(button)
        }
    }

    private fun append(value: String) {
        _state.update { 
            it.copy(
                expression = it.expression + value,
                isError = false
            ) 
        }
    }

    private fun delete() {
        _state.update { 
            it.copy(expression = it.expression.dropLast(1)) 
        }
    }

    private fun clear() {
        _state.update { 
            CalculatorState(angleMode = it.angleMode) 
        }
    }

    private fun toggleAngleMode() {
        _state.update { 
            it.copy(
                angleMode = if (it.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
            ) 
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
                            result = formatResult(result),
                            expression = formatResult(result), // show result in input too
                            isError = false
                        ) 
                    }
                }
                .onFailure { e ->
                    _state.update { 
                        it.copy(
                            result = "Error",
                            isError = true
                        ) 
                    }
                }
        }
    }

    private fun evaluateViaPython(expression: String, angleMode: AngleMode): Result<BigDecimal> {
        return try {
            val py = Python.getInstance()
            val calcModule = py.getModule("calc") // loads assets/python/calc.py
            val resultPy = calcModule.callAttr("calculate", expression, angleMode.name)
            val resultStr = resultPy.toString()
            
            Result.success(BigDecimal(resultStr))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun formatResult(value: BigDecimal): String {
        return value.stripTrailingZeros().toPlainString()
    }
}
