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

  @Test
  fun testPythonRuntimeExecutionAndMathModules() {
    val runtime = PythonRuntime()

    // Test Arithmetic functions loaded directly from Python
    val addRes = runtime.execute("add(25, 17)")
    assertEquals("42", addRes.output)
    assertEquals(false, addRes.isError)

    val factRes = runtime.execute("factorial(6)")
    assertEquals("720", factRes.output)

    val gcdRes = runtime.execute("gcd(48, 18)")
    assertEquals("6", gcdRes.output)

    val lcmRes = runtime.execute("lcm(12, 15)")
    assertEquals("60", lcmRes.output)

    // Test Special Functions: Gamma Function Γ(5) = 4! = 24
    val gamma5Res = runtime.execute("gamma(5)")
    assertEquals("24", gamma5Res.output)

    // Γ(1) = 1
    val gamma1Res = runtime.execute("gamma(1)")
    assertEquals("1", gamma1Res.output)

    // Trigonometry from python
    val sinRes = runtime.execute("sin(0)")
    assertEquals("0", sinRes.output)

    // Logarithms & Roots from python
    val sqrtRes = runtime.execute("sqrt(144)")
    assertEquals("12", sqrtRes.output)

    // Custom user variables & custom def in REPL
    runtime.execute("val = 50")
    val evalVar = runtime.execute("val * 2 + 10")
    assertEquals("110", evalVar.output)

    val defCube = runtime.execute("def cube(x): return x ** 3")
    assertEquals(false, defCube.isError)

    val callCube = runtime.execute("cube(4)")
    assertEquals("64", callCube.output)
  }

  @Test
  fun testCalculatorEvaluatorGammaFunction() {
    val res = CalculatorEvaluator.evaluate("gamma(5)").getOrThrow()
    assertEquals("24", CalculatorFormatter.formatBigDecimal(res))

    val resHalf = CalculatorEvaluator.evaluate("gamma(1)").getOrThrow()
    assertEquals("1", CalculatorFormatter.formatBigDecimal(resHalf))
  }
}
