# Recursive functional language on a ternary tape

Open **Ternary functional language** from the main menu. The editor now supports
arbitrary-precision signed integers, recursive and mutually recursive definitions,
first-class functions, and lexical closures. Every expression returns an integer
or a function. All existing single-trit operations remain available.

```lisp
(inputs n)
(def factorial (x)
  (case (compare x 0)
    0
    1
    (* x (factorial (- x 1)))))
(factorial n)
```

Supply `n=5` to obtain 120. This particular definition returns 0 for negative inputs.
Use decimal integer literals and input assignments; `T` remains an alias for -1.
Internally, program integers are variable-length balanced-ternary digit lists.
They are not limited to a 64-trit word, though configurable resource budgets apply.

## Functions and lexical scope

```lisp
(def make_adder (x)
  (lambda (y) (+ x y)))
(let add_seven (make_adder 7)
  (add_seven 35)) ; 42
```

The returned function retains its defining environment. Shadowing x elsewhere
does not change its captured value. The operator position of a call is an
expression, so `((make_adder 7) 35)` also works. Built-ins are first-class too:

```lisp
(def apply (f x y) (f x y))
(apply + 20 22)
```

Top-level definitions share a recursive environment, allowing forward references
and mutual recursion. Top-level inputs are in that environment too. Parameters
and let bindings shadow outer values. Arguments evaluate exactly once, left to
right. Let evaluates its value before establishing the new binding. Definitions,
inputs, and lambda parameters must not have duplicate names in their own scope.
Identifiers contain ASCII letters, digits, and underscores and cannot begin with
a digit. Special forms and built-in names are reserved. User names are
case-sensitive; existing Trit gate names remain case-insensitive.

## Operations

| Form | Meaning |
|---|---|
| `(+ a b)`, `(- a b)`, `(* a b)` | Binary integer addition, subtraction, multiplication |
| `(compare a b)` | -1, 0, or +1 according to a's ordering relative to b |
| `(case value negative neutral positive)` | Select one branch using a single-trit integer |
| `(lambda (x ...) body)` | Produce a function capturing the current environment |
| `(def f (x ...) body)` | Top-level recursive function definition |
| `(let x value body)` | Immutable lexical binding |
| `(inputs x ...)` | At most one top-level external-input declaration |

Exactly one final expression follows the declarations. Semicolon begins a line
comment. A case evaluates only its selected branch. There is no implicit truthiness:
`(case 2 ...)` is a type error; use `(case (compare 2 0) ...)` to branch by sign.
Integer arithmetic is separate from `SUM`, which still wraps one trit without carry.
The existing 26 Trit gates require arguments in {-1,0,1}. Calling an integer,
incorrect arity, or passing a function to arithmetic produces an error.

## Execution and practical limits

The runtime uses a compact microcoded controller for `TernaryMachine`. Each step
reads the cell under the head, writes one trit, moves at most one cell, and advances
the control state. Bounded control registers hold addresses, expression identifiers,
small counters, and local trits. These registers are part of the controller state;
`machine.state()` alone is not a complete snapshot of a microcoded execution.
This is not a materialized static `Program.rules()` table.

Integers, lexical environments, closure records, argument lists, and pending
continuations are stored on the tape. Arithmetic visits balanced-ternary digits:
addition propagates a trit carry, subtraction negates the second operand's digits,
and multiplication uses shifted additions. Host BigInteger arithmetic is used only
to load input/literal data and decode a halted result, never to perform language
arithmetic during execution. Metadata pointers use fixed-width unsigned ternary
fields; integer payloads use signed balanced ternary.

Calls execute at runtime rather than being inlined. Tail calls reuse the caller's
continuation, and popped continuation records are recycled. A tail loop therefore
has bounded continuation depth. **General heap garbage collection is not yet
implemented:** unreachable integers, argument pairs, and lexical bindings still
consume the allocation budget. Tail calls prevent stack growth but do not make
arbitrarily long runs fit in a finite heap.

The default execution allows 131,072 tape cells and 256 trits per integer. The Java
API can configure 1,024..1,000,000 cells and 1..4,096 integer trits. Limits apply to
intermediate arithmetic as well as results. Source is limited to 16,384 characters,
4,096 parsed forms, nesting depth 64, 32 inputs, and 100,000 microinstructions.
All numeric literals are loaded at startup and count toward the same memory limits.

The editor runs in a background worker with a selectable step budget up to 100
million transitions. An exhausted step budget produces no result and is resumable
through the Java API. A type or allocation error terminates that execution; start
a new execution after correcting it. Since there is one physical head, traversing
the heap costs many tape steps. This is an experimental computing system, not a
replacement for native Java arithmetic.

The language has the ingredients for Turing completeness under idealized unbounded
resources: general recursion, arbitrary-size integers, arithmetic, and conditional
branching. The shipped runtime deliberately has finite resource limits.

## Java API and CLI

```java
var program = TernaryLanguage.compile("(inputs x) (+ x 1)");
var execution = program.start(Map.of("x", BigInteger.ONE.shiftLeft(120)));
if (execution.run(10_000_000)) {
  BigInteger result = execution.integerResult();
} // Otherwise call run again to continue.
```

Use `functionResult()` after halt to distinguish a returned function from an integer.
`maximumContinuationDepth()` reports pending continuation depth, and
`allocatedCells()` reports the allocation high-water mark, including recycled frames.
Each execution has its own tape and control registers.

```sh
java -cp target/classes name.ncg777.computing.apps.TernaryFunctionalApp examples/ternary-machine/factorial.tfun 10000000 "n=5"
java -cp target/classes name.ncg777.computing.apps.TernaryFunctionalApp examples/ternary-machine/closures.tfun 10000000
```

With no arguments the runner opens Swing. Existing expression and static compiler
APIs are retained; [the legacy guide](ternary-functional.md) describes the old
nonrecursive `TernaryFunctional` compiler. No new dependencies are required.
