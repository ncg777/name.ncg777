# A virtual ternary machine

The existing `Trit` class defines the local alphabet `-1, 0, +1`. `TritWord64` adds an immutable balanced-ternary 64-trit word. Its range is `-1,716,841,910,146,256,242,328,924,544,640` through the corresponding positive value, representing `3^64 = 3,433,683,820,292,512,484,657,849,089,281` distinct values (about 101.44 bits). Trit index zero is least significant; printed `T` means `-1`. `addWrapped` performs arithmetic modulo `3^64`, returning the balanced representative. Conversion uses `BigInteger`, not a 64-bit Java integer.

`TernaryMachine` is a deterministic three-symbol tape machine. A finite control state and the trit under its head select a rule `(write trit, move -1/0/+1, next state)`. Control state zero starts; `HALT = -1` stops. Each control state supplies all three read cases, ordered `-1, 0, +1`. A sparse tape stores nonzero trits at `BigInteger` addresses; absent cells read as zero. A 64-trit word can be loaded at addresses 0..63 and read back at any address. `run(maxSteps)` gives every execution a caller-selected step budget and reports whether it halted.

Run the example after building the project:

```sh
java -cp target/classes name.ncg777.computing.apps.TernaryMachineDemo
```

To run your own transition table, write one rule per line as `state read write move next`. `read` and `write` use `T`, `0`, `1`; `move` is `-1`, `0`, or `1`; `next` is a nonnegative state or `HALT`. Every state must supply all three read cases. Lines can have `#` comments. The input word is high trit first, with up to 64 characters (`T`, `0`, `1`); missing high trits are zero. For example:

```sh
java -cp target/classes name.ncg777.computing.apps.TernaryMachineApp examples/ternary-machine/rewrite-positive.tri 111 100
```

This prints `halted=true` and a word ending in `TTT`. A nonhalting program stops when the step budget is exhausted and reports `halted=false`.

The demonstration rewrites a run of `+1` trits into `-1` trits and stops at the first zero. Programs can branch on all three possible trits and can extend their tape in either direction. This is the familiar three-symbol Turing-machine structure; with a suitable finite rule table and a tape that can grow without a fixed bound, it can express general computation. Actual JVM memory and time are finite, and each `run` call is explicitly bounded. A fixed 64-trit word alone is a finite-state device, however large its state space. The existing `TernarySpinModel` is an equilibrium probability model with symmetric interactions; it does not execute transition rules and its 64-node training cap is unrelated to tape length. A future integration could use it to *propose* or *score* configurations, while the machine retains explicit deterministic semantics.
# Expression workbench

See [Ternary expressions](ternary-expressions.md) for a Swing truth-table explorer
and bounded expression-to-tape compiler using all existing Trit operations.

## Functional language

The [functional language guide](ternary-functional-runtime.md) introduces integers,
recursion, closures, immutable local values, and three-way branches on the tape runtime.
Open **Ternary functional language** from the main menu to edit and run programs.
