package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class CalculatorUnitTest {

  @Test
  fun testContinuousTypingAndPreviewFlow() {
    val vm = CalculatorViewModel()

    // 1 + 1
    vm.onAction(CalculatorAction.Number(1))
    assertEquals("1", vm.state.value.expression)
    assertNull(vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Operator(CalculatorOp.ADD))
    assertEquals("1+", vm.state.value.expression)
    assertEquals("1", vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Number(1))
    assertEquals("1+1", vm.state.value.expression)
    assertEquals("2", vm.state.value.previewResult)

    // * 2 (1+1*2)
    vm.onAction(CalculatorAction.Operator(CalculatorOp.MULTIPLY))
    assertEquals("1+1×", vm.state.value.expression)
    assertEquals("2", vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Number(2))
    assertEquals("1+1×2", vm.state.value.expression)
    assertEquals("3", vm.state.value.previewResult)

    // Press Equals: result becomes primary expression
    vm.onAction(CalculatorAction.Equals)
    assertEquals("3", vm.state.value.expression)
    assertNull(vm.state.value.previewResult)
    assertTrue(vm.state.value.isCalculated)

    // Multiply by 3 (3 * 3 = 9)
    vm.onAction(CalculatorAction.Operator(CalculatorOp.MULTIPLY))
    assertEquals("3×", vm.state.value.expression)
    assertEquals("3", vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Number(3))
    assertEquals("3×3", vm.state.value.expression)
    assertEquals("9", vm.state.value.previewResult)

    // Press Equals: result 9
    vm.onAction(CalculatorAction.Equals)
    assertEquals("9", vm.state.value.expression)
    assertNull(vm.state.value.previewResult)
  }

  @Test
  fun testVideoCalculationSequence() {
    // Exact sequence in the user's video: 2 × 3 = 6, 6 × 5 = 30
    val vm = CalculatorViewModel()

    vm.onAction(CalculatorAction.Number(2))
    vm.onAction(CalculatorAction.Operator(CalculatorOp.MULTIPLY))
    vm.onAction(CalculatorAction.Number(3))
    assertEquals("2×3", vm.state.value.expression)
    assertEquals("6", vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Equals)
    assertEquals("6", vm.state.value.expression)
    assertNull(vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Operator(CalculatorOp.MULTIPLY))
    vm.onAction(CalculatorAction.Number(5))
    assertEquals("6×5", vm.state.value.expression)
    assertEquals("30", vm.state.value.previewResult)

    vm.onAction(CalculatorAction.Equals)
    assertEquals("30", vm.state.value.expression)
    assertNull(vm.state.value.previewResult)
  }

  @Test
  fun testBasicAddition() {
    val result = CalculatorEvaluator.evaluate("2 + 3").getOrThrow()
    assertEquals("5", result.toPlainString())
  }

  @Test
  fun testBasicSubtraction() {
    val result = CalculatorEvaluator.evaluate("10 − 4").getOrThrow()
    assertEquals("6", result.toPlainString())
  }

  @Test
  fun testBasicMultiplication() {
    val result = CalculatorEvaluator.evaluate("7 × 8").getOrThrow()
    assertEquals("56", result.toPlainString())
  }

  @Test
  fun testBasicDivision() {
    val result = CalculatorEvaluator.evaluate("20 ÷ 4").getOrThrow()
    assertEquals("5", result.toPlainString())
  }

  @Test
  fun testOperatorPrecedence() {
    val result = CalculatorEvaluator.evaluate("2 + 3 × 4").getOrThrow()
    assertEquals("14", result.toPlainString())
  }

  @Test
  fun testTrigonometryDegree() {
    val sin30 = CalculatorEvaluator.evaluate("sin(30)", AngleMode.DEG).getOrThrow()
    assertEquals(0.5, sin30.toDouble(), 0.0001)

    val cos60 = CalculatorEvaluator.evaluate("cos(60)", AngleMode.DEG).getOrThrow()
    assertEquals(0.5, cos60.toDouble(), 0.0001)
  }

  @Test
  fun testFactorialAndLogarithm() {
    val fact5 = CalculatorEvaluator.evaluate("fact(5)").getOrThrow()
    assertEquals("120", fact5.toPlainString())

    val log100 = CalculatorEvaluator.evaluate("log(100)").getOrThrow()
    assertEquals("2", log100.toPlainString())
  }

  @Test
  fun testDivisionByZero() {
    val result = CalculatorEvaluator.evaluate("10 ÷ 0")
    assertTrue(result.isFailure)
  }

  @Test
  fun testMemoryFunctions() {
    val vm = CalculatorViewModel()
    // Type 25 and add to memory
    vm.onAction(CalculatorAction.Number(2))
    vm.onAction(CalculatorAction.Number(5))
    vm.onAction(CalculatorAction.MemoryAdd)
    assertTrue(vm.state.value.hasMemory)
    assertEquals(BigDecimal("25"), vm.state.value.memory)

    // Clear display, then recall memory
    vm.onAction(CalculatorAction.AllClear)
    assertEquals("", vm.state.value.expression)
    vm.onAction(CalculatorAction.MemoryRecall)
    assertEquals("25", vm.state.value.expression)

    // Memory clear
    vm.onAction(CalculatorAction.MemoryClear)
    assertEquals(false, vm.state.value.hasMemory)
  }
}
