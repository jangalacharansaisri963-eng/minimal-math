"""
Logarithms and roots module written directly in pure Python.
This module is executed at runtime by the calculator's Python engine on Android.
"""

def ln(x):
    """
    Computes natural logarithm using Python series expansion:
    ln(y) where y = (x-1)/(x+1), ln(x) = 2 * sum(y^(2k+1) / (2k+1))
    """
    if x <= 0:
        raise ValueError("math domain error: ln of non-positive number")
    
    # Scale x to range (0.5, 2.0) using powers of e = 2.718281828459045
    e = 2.718281828459045
    k = 0
    while x > 2.0:
        x /= e
        k += 1
    while x < 0.5:
        x *= e
        k -= 1
        
    y = (x - 1.0) / (x + 1.0)
    y2 = y * y
    term = y
    total = 0.0
    for i in range(1, 35, 2):
        total += term / i
        term *= y2
        
    return 2.0 * total + (k * 1.0)

def log(x, base=None):
    if base is None:
        return ln(x)
    return ln(x) / ln(base)

def log10(x):
    return ln(x) / 2.302585092994046

def log2(x):
    return ln(x) / 0.6931471805599453

def sqrt(x):
    """
    Newton-Raphson approximation for square root in pure Python.
    """
    if x < 0:
        raise ValueError("math domain error: sqrt of negative number")
    if x == 0:
        return 0.0
    guess = x / 2.0 if x > 1.0 else 1.0
    for _ in range(25):
        guess = 0.5 * (guess + x / guess)
    return guess

def cbrt(x):
    """
    Newton-Raphson approximation for cube root in pure Python.
    """
    if x == 0:
        return 0.0
    sign = -1.0 if x < 0 else 1.0
    val = abs(x)
    guess = val / 3.0 if val > 1.0 else 1.0
    for _ in range(25):
        guess = (2.0 * guess + val / (guess * guess)) / 3.0
    return sign * guess

def nth_root(x, n):
    if n == 0:
        raise ZeroDivisionError("0th root is undefined")
    return x ** (1.0 / n)
