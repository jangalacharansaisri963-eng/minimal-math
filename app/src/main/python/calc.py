PI = 3.141592653589793

def deg_to_rad(deg): return deg * (PI / 180.0)
def rad_to_deg(rad): return rad * (180.0 / PI)

def sin(rad): 
    x = rad % (2*PI)
    term = x; total = x; x2 = x*x
    for n in range(1, 10): term *= -x2 / ((2*n)*(2*n+1)); total += term
    return total

def cos(rad):
    x = rad % (2*PI)
    term = 1.0; total = 1.0; x2 = x*x
    for n in range(1, 10): term *= -x2 / ((2*n-1)*(2*n)); total += term
    return total

def tan(rad): return sin(rad) / cos(rad)

def sqrt(x):
    if x < 0: raise ValueError("sqrt negative")
    guess = x/2 if x>1 else 1.0
    for _ in range(20): guess = 0.5*(guess + x/guess)
    return guess

def ln(x):
    import math # chaquopy HAS math built in bro 💀
    return math.log(x)

def log10(x): 
    import math
    return math.log10(x)

def factorial(n):
    n = int(n)
    res = 1
    for i in range(2, n+1): res *= i
    return res

def calculate(expression, angle_mode):
    expr = expression.replace('×','*').replace('÷','/').replace('−','-').replace('^','**')
    
    if angle_mode == 'DEG':
        s = lambda x: sin(deg_to_rad(x))
        c = lambda x: cos(deg_to_rad(x))
        t = lambda x: tan(deg_to_rad(x))
    else:
        s = sin; c = cos; t = tan
    
    safe_dict = {'sin':s, 'cos':c, 'tan':t, 'sqrt':sqrt, 'log':log10, 'ln':ln, 'fact':factorial, 'pi':PI}
    return eval(expr, {"__builtins__": None}, safe_dict)
