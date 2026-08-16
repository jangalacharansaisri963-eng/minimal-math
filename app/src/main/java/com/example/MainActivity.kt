package com.example

import android.os.Bundle
import android.widget.Toast // ADD THIS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
  private val viewModel: CalculatorViewModel by viewModels<CalculatorViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    Toast.makeText(this, "1. App Launched", Toast.LENGTH_SHORT).show() // TEST 1
    super.onCreate(savedInstanceState)
    
    try {
        Toast.makeText(this, "2. Starting Python", Toast.LENGTH_SHORT).show() // TEST 2
        if (!Python.isStarted()) {
          Python.start(AndroidPlatform(this))
        }
        Toast.makeText(this, "3. Python Started", Toast.LENGTH_SHORT).show() // TEST 3

        Toast.makeText(this, "4. Getting calc.py", Toast.LENGTH_SHORT).show() // TEST 4
        val py = Python.getInstance()
        val module = py.getModule("calc") // THIS IS THE BOSS FIGHT
        Toast.makeText(this, "5. calc.py LOADED", Toast.LENGTH_LONG).show() // TEST 5

    } catch (e: Exception) {
        Toast.makeText(this, "CRASH: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
        // Don't return here with Compose, just skip loading the UI
    }

    enableEdgeToEdge()
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
