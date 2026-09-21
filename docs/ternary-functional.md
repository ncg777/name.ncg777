# A small functional language for the ternary machine

Open **Ternary functional language** in the main menu. The editor accepts a
program and input assignments such as `a=T b=1`. Choose **Compile and run**;
the result and execution statistics appear below. Compilation and execution run
in a background worker, with a selectable execution budget.

This is a pure, first-order language: expressions return a single trit, local
bindings are immutable, and named functions compose without side effects.
All values are T (negative one), 0, or 1. It does not yet support multi-trit
integers, lists, recursion, anonymous functions, closures, or functions as values.

## Example

```lisp
; Inputs are supplied by the caller, not baked into the compiled program.
(inputs a b)
(def agreement (x y)
  (CONS x y))

(let answer (agreement a b)
  (case answer T 0 1))
```

For `a=1 b=1`, this returns 1; for `a=T b=T`, it returns T; for disagreement,
it returns 0. The final case is deliberately explicit to illustrate all branches.
Replacing it with `(case answer 0 T 1)` would map negative agreement to 0,
neutral agreement to T, and positive agreement to 1.

The program consists of optional declarations followed by exactly one expression.
Syntax uses parenthesized prefix calls and whitespace, with no commas. Semicolon
starts a comment lasting to the end of the line.

| Form | Meaning |
|---|---|
| `T`, `-1`, `0`, `1` | Trit constants |
| `(inputs a b)` | Declare external inputs in tape order; at most one declaration |
| `(NOT a)`, `(AND a b)` | Apply an existing Trit operation |
| `(def f (x y) body)` | Declare a named function at the top level |
| `(f a b)` | Evaluate arguments left to right, once each, then evaluate the function |
| `(let x value body)` | Evaluate value once; bind x only within body |
| `(case value negative neutral positive)` | Evaluate value, then only its selected branch |

Names are case-sensitive ASCII identifiers. Built-in operation names are
case-insensitive. T, built-in names, and the keywords `inputs`, `def`, `let`,
and `case` are reserved. A let binding may shadow an outer binding; its value
expression still sees the outer scope. Function bodies can use their parameters
and local bindings. Inputs must be passed explicitly; functions do not capture
the caller's locals or top-level input names. Calls to later-defined functions
are allowed. Recursive cycles are rejected, including in unused definitions.
Every definition and branch is checked for unbound names and incorrect arity.

All 26 existing operations are available:

```text
BUF NOT PNOT NNOT ABS CLU CLD INC DEC RTU RTD ISP ISZ ISN
AND NAND OR NOR CONS NCONS ANY NANY MUL NMUL SUM NSUM
```

Their meaning is unchanged. In particular, SUM wraps a single trit without carry,
INC saturates, and RTU cycles. See [ternary expressions](ternary-expressions.md)
for logic examples and truth-table exploration.

## How it runs on the machine

The compiler builds tape routines for individual operations. Variables and local
values occupy tape cells; branches select machine states; function calls are
inlined with separate parameter cells. Built-in truth tables generate transition
rules at compilation time. No Java expression evaluator or neural model is
called during tape execution, and no full input truth table is enumerated.

Inputs occupy cells 0..n-1 in declaration order and are preserved. Cell n holds
the final result. Later cells hold scratch values and are not cleared on halt.
The head halts at the result cell. Create a fresh execution for each input set.
The API allows pausing at a step budget and continuing the same machine; an
incomplete execution is never presented as a result.

```java
var compiled = TernaryFunctional.compile("(inputs a) (NOT a)");
var machine = compiled.newMachine(Map.of("a", -1));
if (machine.run(100_000)) {
  int result = compiled.result(machine); // 1
}
```

Practical limits: 32 inputs, 256 allocated tape cells including inputs/result,
50,000 control states, 16,384 source characters, 4,096 parsed forms and expanded
expressions, and nesting/expansion depth 64. These are resource budgets, not
limits inherent in ternary computation. Inlining repeated function calls can
increase code size quickly; compilation stops with an error before exceeding its
budget. Recursion would require a separate runtime stack design.

The editor permits up to ten million execution steps. The Java API and CLI accept
an explicit nonnegative long step budget. The current compiler favors simple,
verifiable routines and returns the head to cell zero between expressions, so
programs may take many more transitions than a handwritten machine program.

Run the included example without opening Swing:

```sh
java -cp target/classes name.ncg777.computing.apps.TernaryFunctionalApp examples/ternary-machine/agreement.tfun 100000 "a=1 b=1"
```

With no arguments, that same class opens the editor. No new dependencies are needed.
