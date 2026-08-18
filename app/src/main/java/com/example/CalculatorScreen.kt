package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CalcDarkFuncBtn
import com.example.ui.theme.CalcDarkFuncText
import com.example.ui.theme.CalcDarkNumText
import com.example.ui.theme.CalcDarkOpText

@Composable
fun CalculatorScreen(
  viewModel: CalculatorViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  val haptic = LocalHapticFeedback.current
  val clipboardManager = LocalClipboardManager.current

  val exprScroll = rememberScrollState()
  val resScroll = rememberScrollState()

  LaunchedEffect(state.expression, state.previewResult) {
    exprScroll.animateScrollTo(exprScroll.maxValue)
    resScroll.animateScrollTo(resScroll.maxValue)
  }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Action Bar
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Angle Mode Badge button (DEG / RAD)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(CalcDarkFuncBtn)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleAngleMode)
              }
              .padding(horizontal = 12.dp, vertical = 6.dp)
              .testTag("badge_angle_mode_top")
          ) {
            Text(
              text = state.angleMode.name,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleTerminal)
              },
              modifier = Modifier.size(40.dp).testTag("btn_top_terminal")
            ) {
              Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Math Terminal",
                tint = if (state.isTerminalOpen) Color(0xFFA6E3A1) else MaterialTheme.colorScheme.outline
              )
            }

            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleUnitConverter)
              },
              modifier = Modifier.size(40.dp).testTag("btn_top_convert")
            ) {
              Icon(
                imageVector = Icons.Default.Straighten,
                contentDescription = "Unit Converter",
                tint = if (state.isUnitConverterOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
              )
            }

            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleHistory)
              },
              modifier = Modifier.size(40.dp).testTag("btn_top_history")
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = if (state.isHistoryOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
              )
            }
          }
        }

        // Display Area
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalArrangement = Arrangement.Bottom,
          horizontalAlignment = Alignment.End
        ) {
          val displayText = state.errorMessage ?: state.expression.ifEmpty { "0" }
          val isError = state.errorMessage != null
          val isSci = state.isExtraFunctionsOpen

          val fontSize = if (isSci) {
            when {
              displayText.length > 20 -> 18.sp
              displayText.length > 14 -> 22.sp
              displayText.length > 8 -> 28.sp
              else -> 36.sp
            }
          } else {
            when {
              displayText.length > 20 -> 24.sp
              displayText.length > 14 -> 32.sp
              displayText.length > 8 -> 42.sp
              else -> 56.sp
            }
          }

          // Expression Line
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(exprScroll, reverseScrolling = true)
              .clickable {
                if (state.expression.isNotEmpty()) {
                  clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(state.expression))
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
              },
            contentAlignment = Alignment.CenterEnd
          ) {
            Text(
              text = if (isError) AnnotatedString(displayText) else formatAnnotatedExpression(displayText),
              fontSize = fontSize,
              fontWeight = FontWeight.Light,
              color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.End,
              maxLines = 1,
              fontFamily = FontFamily.Default,
              modifier = Modifier.testTag("display_expression")
            )
          }

          // Live Preview Result Line
          if (state.previewResult != null && !isError) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(resScroll, reverseScrolling = true),
              contentAlignment = Alignment.CenterEnd
            ) {
              Text(
                text = "= ${state.previewResult}",
                fontSize = if (isSci) 20.sp else 28.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.testTag("display_preview")
              )
            }
          }
        }

        // Secondary Toolbar (History, Unit Converter, Scientific Expand)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleHistory)
              },
              modifier = Modifier.size(38.dp).testTag("btn_history")
            ) {
              Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.outline)
            }

            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleTerminal)
              },
              modifier = Modifier.size(38.dp).testTag("btn_terminal")
            ) {
              Icon(
                imageVector = Icons.Default.Code,
                contentDescription = "Math Terminal",
                tint = if (state.isTerminalOpen) Color(0xFFA6E3A1) else MaterialTheme.colorScheme.outline
              )
            }

            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleUnitConverter)
              },
              modifier = Modifier.size(38.dp).testTag("btn_unit_converter")
            ) {
              Icon(imageVector = Icons.Default.Straighten, contentDescription = "Unit Converter", tint = MaterialTheme.colorScheme.outline)
            }

            IconButton(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(CalculatorAction.ToggleExtraFunctions)
              },
              modifier = Modifier.size(38.dp).testTag("btn_scientific_toggle")
            ) {
              Icon(
                imageVector = if (state.isExtraFunctionsOpen) Icons.Default.KeyboardArrowDown else Icons.Default.Sync,
                contentDescription = "Scientific Toggle",
                tint = if (state.isExtraFunctionsOpen) CalcDarkOpText else MaterialTheme.colorScheme.outline
              )
            }
          }

          if (state.isExtraFunctionsOpen) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(CalcDarkFuncBtn)
                  .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onAction(CalculatorAction.ToggleAngleMode)
                  }
                  .padding(horizontal = 8.dp, vertical = 3.dp)
                  .testTag("badge_angle_mode")
              ) {
                Text(text = state.angleMode.name, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              }

              IconButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  viewModel.onAction(CalculatorAction.ToggleExtraFunctions)
                },
                modifier = Modifier.size(32.dp).testTag("btn_close_scientific")
              ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Scientific", tint = CalcDarkFuncText, modifier = Modifier.size(18.dp))
              }
            }
          }
        }

        // Memory Buttons
        val isSciMode = state.isExtraFunctionsOpen
        Row(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MemoryButton(label = "mc", isCompact = isSciMode, enabled = state.hasMemory, testTag = "btn_mem_mc", onClick = { viewModel.onAction(CalculatorAction.MemoryClear) }, modifier = Modifier.weight(1f))
          MemoryButton(label = "m+", isCompact = isSciMode, enabled = true, testTag = "btn_mem_mplus", onClick = { viewModel.onAction(CalculatorAction.MemoryAdd) }, modifier = Modifier.weight(1f))
          MemoryButton(label = "m-", isCompact = isSciMode, enabled = true, testTag = "btn_mem_mminus", onClick = { viewModel.onAction(CalculatorAction.MemorySubtract) }, modifier = Modifier.weight(1f))
          MemoryButton(label = "mr", isCompact = isSciMode, enabled = state.hasMemory, testTag = "btn_mem_mr", onClick = { viewModel.onAction(CalculatorAction.MemoryRecall) }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Keypad Container
        Box(
          modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).align(Alignment.CenterHorizontally)
        ) {
          Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
            AnimatedVisibility(
              visible = state.isExtraFunctionsOpen,
              enter = expandVertically() + fadeIn(),
              exit = shrinkVertically() + fadeOut()
            ) {
              ScientificKeypad(
                state = state,
                onAction = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  viewModel.onAction(it)
                },
                modifier = Modifier.padding(bottom = 6.dp)
              )
            }

            StandardKeypad(
              isCompact = state.isExtraFunctionsOpen,
              onAction = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.onAction(it)
              }
            )
          }
        }
      }

      // History Sheet
      AnimatedVisibility(
        visible = state.isHistoryOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        CalculationHistorySheet(
          history = state.history,
          onSelect = { viewModel.onAction(CalculatorAction.SelectHistory(it)) },
          onClear = { viewModel.onAction(CalculatorAction.ClearHistory) },
          onClose = { viewModel.onAction(CalculatorAction.ToggleHistory) }
        )
      }

      // Unit Converter Sheet
      AnimatedVisibility(
        visible = state.isUnitConverterOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        UnitConverterSheet(onClose = { viewModel.onAction(CalculatorAction.ToggleUnitConverter) })
      }

      // Pure Kotlin Math Terminal Sheet
      AnimatedVisibility(
        visible = state.isTerminalOpen,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
      ) {
        MathTerminalSheet(onClose = { viewModel.onAction(CalculatorAction.ToggleTerminal) })
      }
    }
  }
}

@Composable
private fun MemoryButton(
  label: String,
  isCompact: Boolean = false,
  enabled: Boolean,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(if (isCompact) 32.dp else 40.dp)
      .clip(CircleShape)
      .background(CalcDarkFuncBtn)
      .clickable(enabled = enabled, onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      fontSize = if (isCompact) 14.sp else 16.sp,
      fontWeight = FontWeight.Normal,
      color = if (enabled) CalcDarkFuncText else CalcDarkFuncText.copy(alpha = 0.35f)
    )
  }
}

private fun formatAnnotatedExpression(expr: String): AnnotatedString {
  return buildAnnotatedString {
    for (char in expr) {
      when (char) {
        '+', '−', '-', '×', '*', '÷', '/', '^', '%' -> {
          pushStyle(SpanStyle(color = CalcDarkOpText, fontWeight = FontWeight.Bold))
          append(char)
          pop()
        }
        '(', ')' -> {
          pushStyle(SpanStyle(color = CalcDarkOpText.copy(alpha = 0.7f)))
          append(char)
          pop()
        }
        else -> {
          pushStyle(SpanStyle(color = CalcDarkNumText))
          append(char)
          pop()
        }
      }
    }
  }
}
