"""
Merged Calculator Engine for Android Chaquopy
Combines: Arithmetic, Logs/Roots, Trigonometry
"""

PI = 3.141592653589793

# ================== ARITHMETIC ==================
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

# ================== LOGS & ROOTS ==================
def ln(x):
    if x <= 0:
        raise ValueError("math domain error: ln of non-positive number")
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
    if x < 0:
        raise ValueError("math domain error: sqrt of negative number")
    if x == 0:
        return 0.0
    guess = x / 2.0 if x > 1.0 else 1.0
    for _ in range(25):
        guess = 0.5 * (guess + x / guess)
    return guess

def cbrt(x):
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

# ================== TRIGONOMETRY ==================
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
    for n in range(1, 12):
        term *= -x2 / ((2 * n) * (2 * n + 1))
        total += term
    return total

def cos(rad):
    x = normalize_angle(rad)
    term = 1.0
    total = 1.0
    x2 = x * x
    for n in range(1, 12):
        term *= -x2 / ((2 * n - 1) * (2 * n))
        total += term
    return total

def tan(rad):
    c = cos(rad)
    if abs(c) < 1e-12:
        raise ValueError("math domain error: tan undefined at given angle")
    return sin(rad) / c

def asin(x):
    # Simple wrapper using ln formula for now. Replace if needed
    return rad_to_deg(0.5 * PI - 2 * atan(sqrt((1-x)/(1+x))))

def acos(x):
    return rad_to_deg(PI/2 - asin(deg_to_rad(x)))

def atan(x):
    # Taylor series for atan
    if abs(x) > 1:
        return rad_to_deg(PI/2 * (1 if x > 0 else -1) - atan(1/x))
    term = x
    total = x
    x2 = x * x
    for n in range(1, 12):
        term *= -x2
        total += term / (2 * n + 1)
    return rad_to_deg(total)

# ================== MAIN CALCULATOR ENTRY ==================
def calculate(expression, angle_mode):
    expr = expression.replace('×', '*').replace('÷', '/').replace('−', '-').replace('^', '**').replace('%', '/100')

    # Wrap trig functions to handle DEG/RAD
    if angle_mode == 'DEG':
        s = lambda x: sin(deg_to_rad(x))
        c = lambda x: cos(deg_to_rad(x))
        t = lambda x: tan(deg_to_rad(x))
        asin_f = lambda x: asin(x)
        acos_f = lambda x: acos(x)
        atan_f = lambda x: atan(x)
    else:
        s = sin
        c = cos
        t = tan
        asin_f = lambda x: rad_to_deg(asin(deg_to_rad(x)))
        acos_f = lambda x: rad_to_deg(acos(deg_to_rad(x)))
        atan_f = lambda x: rad_to_deg(atan(deg_to_rad(x)))

    result = eval(expr, {"__builtins__": None}, {
        'sin': s, 'cos': c, 'tan': t, 'asin': asin_f, 'acos': acos_f, 'atan': atan_f,
        'sqrt': sqrt, 'cbrt': cbrt, 'log': log10, 'ln': ln, 'exp': lambda x: power(2.718281828459045, x),
        'pi': PI, 'e': 2.718281828459045,
        'fact': factorial, 'abs': abs_val
    })
    return result
