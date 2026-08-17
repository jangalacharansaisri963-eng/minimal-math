package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

@Composable
fun UnitConverterSheet(
  onClose: () -> Unit
) {
  var selectedCategory by remember { mutableStateOf("Length") }
  var inputValue by remember { mutableStateOf("1") }

  val categories = listOf("Length", "Weight", "Temperature", "Speed")

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.65f))
      .clickable(onClick = onClose)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .clickable(enabled = false) {},
      shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Unit Converter",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onClose, modifier = Modifier.testTag("btn_close_unit_converter")) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(categories) { cat ->
            val isSelected = cat == selectedCategory
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { selectedCategory = cat }
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("cat_$cat")
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = inputValue,
          onValueChange = { inputValue = it },
          label = { Text("Value to convert") },
          modifier = Modifier.fillMaxWidth().testTag("input_converter_val"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        val valNum = inputValue.toDoubleOrNull() ?: 0.0
        val results = calculateConversions(selectedCategory, valNum)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          results.forEach { (unit, value) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
              Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }
    }
  }
}

private fun calculateConversions(category: String, v: Double): List<Pair<String, String>> {
  val fmt = DecimalFormat("0.####")
  return when (category) {
    "Length" -> listOf(
      "Meters (m)" to "${fmt.format(v)} m",
      "Kilometers (km)" to "${fmt.format(v / 1000.0)} km",
      "Centimeters (cm)" to "${fmt.format(v * 100.0)} cm",
      "Inches (in)" to "${fmt.format(v * 39.3701)} in",
      "Feet (ft)" to "${fmt.format(v * 3.28084)} ft",
      "Miles (mi)" to "${fmt.format(v * 0.000621371)} mi"
    )
    "Weight" -> listOf(
      "Kilograms (kg)" to "${fmt.format(v)} kg",
      "Grams (g)" to "${fmt.format(v * 1000.0)} g",
      "Pounds (lbs)" to "${fmt.format(v * 2.20462)} lbs",
      "Ounces (oz)" to "${fmt.format(v * 35.274)} oz"
    )
    "Temperature" -> listOf(
      "Celsius (°C)" to "${fmt.format(v)} °C",
      "Fahrenheit (°F)" to "${fmt.format(v * 9.0 / 5.0 + 32.0)} °F",
      "Kelvin (K)" to "${fmt.format(v + 273.15)} K"
    )
    "Speed" -> listOf(
      "m/s" to "${fmt.format(v)} m/s",
      "km/h" to "${fmt.format(v * 3.6)} km/h",
      "mph" to "${fmt.format(v * 2.23694)} mph",
      "Knots" to "${fmt.format(v * 1.94384)} kn"
    )
    else -> emptyList()
  }
}
