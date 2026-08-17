"""
Trigonometric module written directly in pure Python without importing C-libraries.
Uses Taylor Series approximations for high accuracy in pure Python on Android.
"""

PI = 3.141592653589793

def deg_to_rad(deg):
    return deg * (PI / 180.0)

def rad_to_deg(rad):
    return rad * (180.0 / PI)

def normalize_angle(rad):
    two_pi = 2.0 * PI
    rad = rad % two_pi
    if rad > PI:
        rad -= two_pi
    elif rad < -PI:
        rad += two_pi
    return rad

def sin(rad):
    x = normalize_angle(rad)
    term = x
    total = x
    x2 = x * x
    # Taylor series: x - x^3/3! + x^5/5! - ...
    for n in range(1, 12):
        term *= -x2 / ((2 * n) * (2 * n + 1))
        total += term
    return total

def cos(rad):
    x = normalize_angle(rad)
    term = 1.0
    total = 1.0
    x2 = x * x
    # Taylor series: 1 - x^2/2! + x^4/4! - ...
    for n in range(1, 12):
        term *= -x2 / ((2 * n - 1) * (2 * n))
        total += term
    return total

def tan(rad):
    c = cos(rad)
    if abs(c) < 1e-12:
        raise ValueError("math domain error: tan undefined at given angle")
    return sin(rad) / c
