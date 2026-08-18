package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class TerminalLogItem(
  val id: Long = System.currentTimeMillis(),
  val command: String,
  val output: String,
  val isError: Boolean = false,
  val timestamp: Long = System.currentTimeMillis()
)

@Composable
fun MathTerminalSheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val engine = remember { MathTerminalEngine() }
  var currentInput by remember { mutableStateOf("") }
  val history = remember {
    mutableStateListOf(
      TerminalLogItem(
        command = "",
        output = "Kotlin Math Terminal v1.0\nPowered by com.example.functions\nType 'help' for functions or try: sin(pi/2), gcd(48, 18), ln(e)"
      )
    )
  }

  val listState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  fun runCommand(cmd: String) {
    val text = cmd.trim()
    if (text.isEmpty()) return

    try {
      val output = engine.execute(text)
      if (output == "CLEAR_SIGNAL") {
        history.clear()
      } else {
        history.add(
          TerminalLogItem(
            command = text,
            output = output,
            isError = false
          )
        )
      }
    } catch (e: Exception) {
      history.add(
        TerminalLogItem(
          command = text,
          output = "Error: ${e.message ?: "Evaluation failed"}",
          isError = true
        )
      )
    }

    currentInput = ""
    scope.launch {
      if (history.isNotEmpty()) {
        listState.animateScrollToItem(history.size - 1)
      }
    }
  }

  Surface(
    modifier = modifier.fillMaxSize().imePadding().navigationBarsPadding(),
    color = Color(0xFF1E1E2E) // Deep modern terminal dark
  ) {
    Column(
      modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
      // Top Terminal Header Bar
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Terminal decorative dots
          Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
          Spacer(modifier = Modifier.width(6.dp))
          Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
          Spacer(modifier = Modifier.width(6.dp))
          Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF27C93F)))
          Spacer(modifier = Modifier.width(12.dp))

          Icon(
            imageVector = Icons.Default.Terminal,
            contentDescription = null,
            tint = Color(0xFFA6E3A1),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Math Terminal",
            color = Color(0xFFCDD6F4),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Row {
          IconButton(
            onClick = {
              history.clear()
            },
            modifier = Modifier.testTag("btn_terminal_clear")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = "Clear Terminal",
              tint = Color(0xFFA6ADC8)
            )
          }

          IconButton(
            onClick = {
              engine.reset()
              history.add(
                TerminalLogItem(
                  command = "reset",
                  output = "Environment reset. Constants: pi, e",
                  isError = false
                )
              )
            },
            modifier = Modifier.testTag("btn_terminal_reset")
          ) {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = "Reset Engine",
              tint = Color(0xFFA6ADC8)
            )
          }

          IconButton(
            onClick = onClose,
            modifier = Modifier.testTag("btn_terminal_close")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Terminal",
              tint = Color(0xFFF38BA8)
            )
          }
        }
      }

      // Quick Suggestion Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val quickCommands = listOf(
          "sin(pi / 2)",
          "gcd(48, 18)",
          "factorial(6)",
          "log10(1000)",
          "ln(e)",
          "sqrt(144)",
          "vars",
          "help"
        )
        quickCommands.forEach { cmd ->
          SuggestionChip(
            onClick = { runCommand(cmd) },
            label = {
              Text(
                cmd,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color(0xFF89B4FA)
              )
            },
            colors = SuggestionChipDefaults.suggestionChipColors(
              containerColor = Color(0xFF313244)
            ),
            border = null
          )
        }
      }

      // Console Output Log List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(Color(0xFF181825), RoundedCornerShape(8.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(history, key = { it.id }) { item ->
          Column(modifier = Modifier.fillMaxWidth()) {
            if (item.command.isNotEmpty()) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = ">>> ",
                  color = Color(0xFFF9E2AF),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = item.command,
                  color = Color(0xFFCDD6F4),
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp
                )
              }
            }

            if (item.output.isNotEmpty()) {
              Text(
                text = item.output,
                color = if (item.isError) Color(0xFFF38BA8) else Color(0xFFA6E3A1),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = if (item.command.isNotEmpty()) 12.dp else 0.dp, top = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Input Prompt Area
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = currentInput,
          onValueChange = { currentInput = it },
          modifier = Modifier
            .weight(1f)
            .testTag("input_terminal_command"),
          placeholder = {
            Text(
              "Type Kotlin math expression...",
              color = Color(0xFF6C7086),
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp
            )
          },
          textStyle = androidx.compose.ui.text.TextStyle(
            color = Color(0xFFCDD6F4),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF89B4FA),
            unfocusedBorderColor = Color(0xFF45475A),
            focusedContainerColor = Color(0xFF181825),
            unfocusedContainerColor = Color(0xFF181825),
            cursorColor = Color(0xFF89B4FA)
          ),
          singleLine = true,
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = ImeAction.Send
          ),
          keyboardActions = KeyboardActions(
            onSend = { runCommand(currentInput) }
          ),
          shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = { runCommand(currentInput) },
          modifier = Modifier
            .size(48.dp)
            .background(Color(0xFF89B4FA), RoundedCornerShape(8.dp))
            .testTag("btn_terminal_send")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Run",
            tint = Color(0xFF11111B)
          )
        }
      }
    }
  }
}
