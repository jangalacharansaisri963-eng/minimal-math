package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class TerminalEntry(
  val command: String,
  val output: String
)

@Composable
fun PythonTerminalSheet(
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var inputQuery by remember { mutableStateOf("") }
  val terminalHistory = remember { mutableStateListOf<TerminalEntry>() }
  val listState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Surface(
    modifier = modifier.fillMaxSize(),
    color = Color(0xFF0D1117)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFF388BFD).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              tint = Color(0xFF58A6FF),
              modifier = Modifier.size(20.dp)
            )
          }

          Column {
            Text(
              text = "Python Terminal",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFE6EDF3)
            )
            Text(
              text = "Interactive Python Math Shell",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF8B949E)
            )
          }
        }

        IconButton(
          onClick = onClose,
          modifier = Modifier.testTag("btn_close_terminal")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Terminal",
            tint = Color(0xFF8B949E)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Terminal Canvas & Output Area
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF161B22))
          .padding(16.dp)
      ) {
        if (terminalHistory.isEmpty()) {
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = null,
              tint = Color(0xFF58A6FF).copy(alpha = 0.6f),
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Terminal Ready",
              fontFamily = FontFamily.Monospace,
              fontSize = 18.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF58A6FF)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Type Python commands below (e.g., math.gcd(12, 18))",
              fontFamily = FontFamily.Monospace,
              fontSize = 13.sp,
              color = Color(0xFF8B949E)
            )
          }
        } else {
          LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(terminalHistory) { entry ->
              Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                  text = ">>> ${entry.command}",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF58A6FF)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = entry.output,
                  fontFamily = FontFamily.Monospace,
                  fontSize = 13.sp,
                  color = Color(0xFFE6EDF3)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Input Line Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = inputQuery,
          onValueChange = { inputQuery = it },
          placeholder = {
            Text(
              text = "Enter python script...",
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF8B949E)
            )
          },
          singleLine = true,
          modifier = Modifier.weight(1f),
          textStyle = LocalTextStyle.current.copy(
            fontFamily = FontFamily.Monospace,
            color = Color(0xFFE6EDF3)
          ),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF58A6FF),
            unfocusedBorderColor = Color(0xFF30363D),
            focusedContainerColor = Color(0xFF161B22),
            unfocusedContainerColor = Color(0xFF161B22)
          ),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(
            onDone = {
              if (inputQuery.isNotBlank()) {
                val response = PythonTerminalRunner.executeCommand(inputQuery)
                terminalHistory.add(TerminalEntry(command = inputQuery, output = response))
                inputQuery = ""
                coroutineScope.launch {
                  if (terminalHistory.isNotEmpty()) {
                    listState.animateScrollToItem(terminalHistory.size - 1)
                  }
                }
              }
            }
          ),
          shape = RoundedCornerShape(12.dp)
        )

        IconButton(
          onClick = {
            if (inputQuery.isNotBlank()) {
              val response = PythonTerminalRunner.executeCommand(inputQuery)
              terminalHistory.add(TerminalEntry(command = inputQuery, output = response))
              inputQuery = ""
              coroutineScope.launch {
                if (terminalHistory.isNotEmpty()) {
                  listState.animateScrollToItem(terminalHistory.size - 1)
                }
              }
            }
          },
          modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF238636))
        ) {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = "Run Python Expression",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
