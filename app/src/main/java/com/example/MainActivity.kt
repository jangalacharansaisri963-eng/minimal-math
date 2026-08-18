package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.functions.Arithmetic
import com.example.functions.Logarithms
import com.example.functions.NaturalLogarithms
import com.example.functions.Trigonometry
import com.example.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
  private val viewModel: CalculatorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Demonstration of importing and calling one function from each topic file
    val sum = Arithmetic.add(15.0, 27.0) // from arithmetic.kt
    val logTenVal = Logarithms.log10(1000.0) // from logarithms.kt
    val expVal = NaturalLogarithms.exp(1.0) // from natural_logarithms.kt
    val sinVal = Trigonometry.sin(Trigonometry.PI_VALUE / 2.0) // from trigonometry.kt

    Log.d("MathDemo", "Arithmetic.add(15, 27) = $sum")
    Log.d("MathDemo", "Logarithms.log10(1000) = $logTenVal")
    Log.d("MathDemo", "NaturalLogarithms.exp(1) = $expVal")
    Log.d("MathDemo", "Trigonometry.sin(PI / 2) = $sinVal")

    setContent {
      CalculatorTheme {
        CalculatorScreen(
          viewModel = viewModel,
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}
