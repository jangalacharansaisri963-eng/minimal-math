package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalcDarkEqualsBtn
import com.example.ui.theme.CalcDarkFuncText
import com.example.ui.theme.CalcDarkNumBtn
import com.example.ui.theme.CalcDarkNumText
import com.example.ui.theme.CalcDarkOpText

@Composable
fun StandardKeypad(
  isCompact: Boolean = false,
  onAction: (CalculatorAction) -> Unit,
  modifier: Modifier = Modifier
) {
  val spacing = if (isCompact) 6.dp else 10.dp
  val numFontSize = if (isCompact) 22.sp else 26.sp
  val eqFontSize = if (isCompact) 26.sp else 32.sp

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(spacing)
  ) {
    // Row 1: AC, ⌫, ±, ÷
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
      RoundCalculatorButton(
        text = "AC",
        textColor = CalcDarkFuncText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_all_clear",
        onClick = { onAction(CalculatorAction.AllClear) },
        modifier = Modifier.weight(1f)
      )
      RoundIconButton(
        icon = Icons.AutoMirrored.Filled.Backspace,
        iconTint = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        testTag = "btn_backspace",
        onClick = { onAction(CalculatorAction.Backspace) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "+/-",
        textColor = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_plus_minus",
        onClick = { onAction(CalculatorAction.ToggleSign) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "÷",
        textColor = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_divide",
        onClick = { onAction(CalculatorAction.Operator(CalculatorOp.DIVIDE)) },
        modifier = Modifier.weight(1f)
      )
    }

    // Row 2: 7, 8, 9, ×
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
      RoundCalculatorButton(
        text = "7",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_7",
        onClick = { onAction(CalculatorAction.Number(7)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "8",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_8",
        onClick = { onAction(CalculatorAction.Number(8)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "9",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_9",
        onClick = { onAction(CalculatorAction.Number(9)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "×",
        textColor = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_multiply",
        onClick = { onAction(CalculatorAction.Operator(CalculatorOp.MULTIPLY)) },
        modifier = Modifier.weight(1f)
      )
    }

    // Row 3: 4, 5, 6, −
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
      RoundCalculatorButton(
        text = "4",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_4",
        onClick = { onAction(CalculatorAction.Number(4)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "5",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_5",
        onClick = { onAction(CalculatorAction.Number(5)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "6",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_6",
        onClick = { onAction(CalculatorAction.Number(6)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "−",
        textColor = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_subtract",
        onClick = { onAction(CalculatorAction.Operator(CalculatorOp.SUBTRACT)) },
        modifier = Modifier.weight(1f)
      )
    }

    // Row 4: 1, 2, 3, +
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
      RoundCalculatorButton(
        text = "1",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_1",
        onClick = { onAction(CalculatorAction.Number(1)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "2",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_2",
        onClick = { onAction(CalculatorAction.Number(2)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "3",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_3",
        onClick = { onAction(CalculatorAction.Number(3)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "+",
        textColor = CalcDarkOpText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_add",
        onClick = { onAction(CalculatorAction.Operator(CalculatorOp.ADD)) },
        modifier = Modifier.weight(1f)
      )
    }

    // Row 5: %, 0, ., =
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
      RoundCalculatorButton(
        text = "%",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_percent",
        onClick = { onAction(CalculatorAction.Percentage) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "0",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_0",
        onClick = { onAction(CalculatorAction.Number(0)) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = ".",
        textColor = CalcDarkNumText,
        bgColor = CalcDarkNumBtn,
        fontSize = numFontSize,
        testTag = "btn_decimal",
        onClick = { onAction(CalculatorAction.Decimal) },
        modifier = Modifier.weight(1f)
      )
      RoundCalculatorButton(
        text = "=",
        textColor = Color.White,
        bgColor = CalcDarkEqualsBtn,
        fontSize = eqFontSize,
        fontWeight = FontWeight.Normal,
        testTag = "btn_equals",
        onClick = { onAction(CalculatorAction.Equals) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
fun RoundCalculatorButton(
  text: String,
  textColor: Color,
  bgColor: Color,
  testTag: String,
  fontSize: androidx.compose.ui.unit.TextUnit = 26.sp,
  fontWeight: FontWeight = FontWeight.Normal,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(CircleShape)
      .background(bgColor)
      .clickable(onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = fontSize,
      fontWeight = fontWeight
    )
  }
}

@Composable
fun RoundIconButton(
  icon: ImageVector,
  iconTint: Color,
  bgColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(CircleShape)
      .background(bgColor)
      .clickable(onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = "Action",
      tint = iconTint,
      modifier = Modifier.size(24.dp)
    )
  }
}
