"""
Arithmetic module written directly in pure Python.
This module is executed at runtime by the calculator's Python engine on Android.
"""

def add(a, b):
    return a + b

def subtract(a, b):
    return a - b

def multiply(a, b):
    return a * b

def divide(a, b):
    if b == 0:
        raise ZeroDivisionError("division by zero")
    return a / b

def floor_divide(a, b):
    if b == 0:
        raise ZeroDivisionError("integer division by zero")
    return int(a // b)

def modulo(a, b):
    if b == 0:
        raise ZeroDivisionError("modulo by zero")
    return a % b

def power(base, exp):
    return base ** exp

def abs_val(x):
    return abs(x)

def factorial(n):
    n = int(n)
    if n < 0:
        raise ValueError("factorial() not defined for negative values")
    res = 1
    for i in range(2, n + 1):
        res *= i
    return res

def gcd(a, b):
    x, y = abs(int(a)), abs(int(b))
    while y != 0:
        x, y = y, x % y
    return x

def lcm(a, b):
    a, b = int(a), int(b)
    if a == 0 or b == 0:
        return 0
    return abs(a * b) // gcd(a, b)
