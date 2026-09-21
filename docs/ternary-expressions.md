# Ternary expressions

Open **Ternary expressions** from the main menu, or run
`name.ncg777.computing.apps.TernaryExpressionApp`.
Enter an expression and choose **Evaluate all inputs**. The table shows every
assignment, its direct result, and its result on the ternary tape machine.

## Syntax and meaning

Use nested function calls: `AND(a, NOT(b))`. Constants are `T` (negative one),
`-1`, `0`, and `1`. Variables use ASCII letters, digits and underscores, cannot
start with a digit, and are case-sensitive. Uppercase `T` is reserved. Operation
names are case-insensitive. Infix symbols and grouping-only parentheses are not
supported; function parentheses make the grouping explicit.

All semantics come from the existing `name.ncg777.maths.Trit` tables:

- Unary: BUF NOT PNOT NNOT ABS CLU CLD INC DEC RTU RTD ISP ISZ ISN
- Binary: AND NAND OR NOR CONS NCONS ANY NANY MUL NMUL SUM NSUM

For an optional logical interpretation, T means false, 0 means unknown, and 1
means true. AND is minimum, OR is maximum, and NOT changes the sign. This
interpretation is useful for those gates; other gates have their own tables.

| a | NOT(a) | AND(a,NOT(a)) | OR(a,NOT(a)) |
|---|---|---|---|
| T | 1 | T | 1 |
| 0 | 0 | 0 | 0 |
| 1 | T | T | 1 |

Try these expressions:

- `CONS(a,b)`: keep a shared nonzero value; otherwise return zero.
- `ANY(a,b)`: zero yields to a nonzero input; opposing nonzero inputs yield zero.
- `SUM(a,b)`: cyclic single-trit sum; `SUM(1,1)` is T. This is not integer addition with carry.
- `OR(ISN(a),ISP(b))`: true when a is negative or b is positive. The sign tests return T or 1.
- `AND(sensor,NOT(inhibit))`: an illustrative three-valued decision rule, including unknown inputs.

## Tape compilation

`TernaryExpression.parse(source)` provides `variables()`, `evaluate(values)` and
`compile()`. Variables are ordered alphabetically and occupy tape cells 0..n-1.
The compiled program preserves those inputs, writes its result at cell n, and
halts there after exactly n+1 transitions. Any previous result-cell value is
overwritten. No neural training is involved.

This first compiler builds a complete decision tree by evaluating the expression
for all 3^n assignments during compilation. Execution then follows ordinary tape
transitions; it does not call Java arithmetic to evaluate the expression at runtime.
It is specialized to the expression, rather than a general interpreter reading
expression text from the tape. Compilation uses (3^(n+1)-1)/2 control states.

The workbench and compiler allow at most six distinct variables: 729 assignments
and 1,093 states. This is a resource budget, not a ternary-machine limitation.
Direct evaluation does not enumerate assignments and supports more variables.
All parsing is limited to 4,096 characters and 64 nested calls. Larger-scale
compilation would benefit from shared subexpressions or reusable tape routines.
