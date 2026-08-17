package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CalcDarkEqualsBtn
import com.example.ui.theme.CalcDarkFuncBtn
import com.example.ui.theme.CalcDarkFuncText
import com.example.ui.theme.CalcDarkOpText

@Composable
fun ScientificKeypad(
  state: CalculatorState,
  onAction: (CalculatorAction) -> Unit,
  modifier: Modifier = Modifier
) {
  val sciSpacing = 6.dp

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(sciSpacing)
  ) {
    // Scientific Row 1: 2nd, sin, cos, tan, ln
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(sciSpacing)
    ) {
      SciPillButton(
        text = "2nd",
        isHighlighted = state.isSecondMode,
        testTag = "btn_sci_2nd",
        onClick = { onAction(CalculatorAction.ToggleSecondMode) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "sin⁻¹" else "sin",
        testTag = "btn_sci_sin",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.SIN)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "cos⁻¹" else "cos",
        testTag = "btn_sci_cos",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.COS)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "tan⁻¹" else "tan",
        testTag = "btn_sci_tan",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.TAN)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "eˣ" else "ln",
        testTag = "btn_sci_ln",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.LN)) },
        modifier = Modifier.weight(1f)
      )
    }

    // Scientific Row 2: log, xʸ, √x, x!, (
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(sciSpacing)
    ) {
      SciPillButton(
        text = if (state.isSecondMode) "10ˣ" else "log",
        testTag = "btn_sci_log",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.LOG)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = "xʸ",
        testTag = "btn_sci_power",
        onClick = { onAction(CalculatorAction.Operator(CalculatorOp.POWER)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "x²" else "√x",
        testTag = "btn_sci_sqrt",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.SQRT)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = "x!",
        testTag = "btn_sci_fact",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.FACTORIAL)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = "(",
        testTag = "btn_sci_open_paren",
        onClick = { onAction(CalculatorAction.OpenParenthesis) },
        modifier = Modifier.weight(1f)
      )
    }

    // Scientific Row 3: 1/x, π, e, ∛x, )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(sciSpacing)
    ) {
      SciPillButton(
        text = "1/x",
        testTag = "btn_sci_inv",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.INVERSE)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = "π",
        testTag = "btn_sci_pi",
        onClick = { onAction(CalculatorAction.Constant(MathConstant.PI)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = "e",
        testTag = "btn_sci_e",
        onClick = { onAction(CalculatorAction.Constant(MathConstant.E)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = if (state.isSecondMode) "x³" else "∛x",
        testTag = "btn_sci_cbrt",
        onClick = { onAction(CalculatorAction.ScientificFunc(ScientificFunction.CBRT)) },
        modifier = Modifier.weight(1f)
      )
      SciPillButton(
        text = ")",
        testTag = "btn_sci_close_paren",
        onClick = { onAction(CalculatorAction.CloseParenthesis) },
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
fun SciPillButton(
  text: String,
  isHighlighted: Boolean = false,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .height(32.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(if (isHighlighted) CalcDarkEqualsBtn.copy(alpha = 0.25f) else CalcDarkFuncBtn)
      .clickable(onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Medium,
      color = if (isHighlighted) CalcDarkOpText else CalcDarkFuncText
    )
  }
}
