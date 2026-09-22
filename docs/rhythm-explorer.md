# Rhythm Explorer

Open **Rhythm Explorer** in the main menu, run `scripts/rhythm-explorer.sh`
(`.cmd` on Windows), or use `scripts/rhythm.sh gui`.
This Swing application and the CLI share `RhythmExplorer`, `RhythmMask`,
`RhythmClauseParser`, and `RhythmPredicateRegistry`. No training is required.
Java 17 and the project's existing dependencies are sufficient.

## Explore visually

1. Start with the default `9249` hex rhythm and `NONEMPTY AND HITS(6)` rule.
2. Click a step to cycle **rest → onset → unknown**. `?` releases a position;
   a known `0` remains a required rest. Shift-click to protect a position from
   automatic erasure (purple outline); this protects either a hit or a rest.
3. Select predicates in the **Predicates** tab, supply an argument if applicable,
   and click **Insert predicate**. Use AND/OR/NOT and grouping buttons or type
   directly. Insertions replace the selected text or occur at the caret. Blank
   clauses mean TRUE. **Validate expressions** checks syntax and ordinal lengths.
4. **Complete** lists distinct compatible rhythms. The rhythm clause applies to
   each candidate; the transition clause applies only during evolution.
5. Select a result, **Audition selected**, **Copy selected** (binary), or
   **Use selected**. **Erase current** releases positions according to the
   Evolution tab. Complete again, or undo the edit.
6. **Evolve once**, **Evolve batch**, or **Run continuously** performs repeated
   erase/complete/select steps. A compatible complete starting rhythm is required.
   **Stop generation** interrupts work without throwing away completed rows.
   The continuous delay paces exploration; it is not a musical playback clock.
7. **Export results…** writes JSONL, or MIDI if the filename ends in `.mid`.
   MIDI plays every listed row in table order on one GM percussion voice.
   **Audition selected** loops just the selected rhythm; **Stop audio** ends it.
   MIDI note number and BPM are in **Run / MIDI**. Audio requires a working Java
   synthesizer/audio device; a device error does not affect completion or MIDI export.

Automatic evolution keeps the latest 2,000 rows; completion retains up to 10,000.
Export includes the retained rows. Use CLI streaming for a longer persistent log.
**Save setup…** stores the current mask and controls as versioned JSON.
**Load setup…** restores them and starts a new history/seed sequence. A setup is
not a checkpoint of the internal random walk. Consecutive evolution actions
continue the same session while the rhythm and settings remain unchanged;
manually reusing a result or changing settings starts a new session.

## Representation and erasure

Display order is chronological, most-significant bit first, matching the existing
completion panel: `8080` hex is `1000000010000000` binary. Protected step indices
start at **0 at the left**. Leading zeros and total length are significant.
Whitespace is ignored in input. Rhythms have 1–256 binary steps.

| Cipher | Steps per digit / question mark | Four digits |
| --- | ---: | ---: |
| Binary | 1 | 4 steps |
| Octal | 3 | 12 steps |
| Hexadecimal | 4 | 16 steps |

A conversion requires a whole number of digits. A partially known digit cannot
be represented as a digit-level `?` without losing constraints, so the GUI falls
back to Binary after a bit edit/erasure when needed. Explicit conversions reject
lossy conversions and never pad or change the rhythm's length. To enter a new
length, edit the mask in the current cipher; choose Binary first if needed.

Erasure releases exactly the requested number of eligible units, not necessarily
that number of changed bits in the selected completion:

- **SCATTERED:** sample distinct unprotected units.
- **BLOCK:** a contiguous cyclic block, selected from blocks with no protected steps.
- **PERIODIC:** evenly spaced indices through the eligible units, with a random phase.
  Without protection and with divisible counts this is every kth unit; otherwise
  spacing can alternate, and protected units are skipped.

A protected step protects its whole digit when erasing digits. Impossible erasures
report an error rather than silently relaxing protection. A count of zero is valid.

## Rules and selection

The existing Boolean grammar is shared with Rhythm Network: NOT binds tighter
than AND, which binds tighter than OR; parentheses override precedence. Registry
metadata also drives the GUI builder and `rhythm predicates`.

Added predicates:

- `NONEMPTY`: one or more onsets.
- `HITS(n)`, `MIN_HITS(n)`, `MAX_HITS(n)`: exact/lower/upper onset bounds.
- `EUCLIDEAN`: maximally even at the rhythm's own length and onset count, any rotation.
- `EUCLIDEAN(n)`: additionally require exactly n onsets.
- `SPECTRUM_RISING` and `MAXIMIZE_QUALITY`: expose existing project predicates.

Euclidean matching uses a mechanical-word construction and all cyclic rotations;
empty and full rhythms are included. Combine with NONEMPTY to exclude silence.
It does not just test the multiset of gaps: their cyclic arrangement matters.
See [Toussaint's Euclidean rhythm paper](https://cgm.cs.mcgill.ca/~godfried/publications/banff.pdf).

Existing predicates retain their project meanings: EVEN means even inter-onset
intervals, not even distribution; HAS_NO_GAPS refers to interval-vector support.
MINIMUM_GAP/MAXIMUM_GAP take arguments >= 2. ORDINAL's argument must divide the
length on which it is evaluated. Parameterless predicates now reject accidental
arguments instead of silently ignoring them. EUCLIDEAN supports an optional count.

The **transition rule** evaluates `previous + candidate` in chronological order,
on twice as many steps. For example, `HITS(12)` allows two successive six-hit bars.
A transition clause is not a predicate over a set difference. This first explorer
operates on one voice across time. The existing **Ternary image laboratory → Rhythm
completion** still provides the four-bar SCI/pairwise-difference workflow, unchanged.

Candidates satisfying the hard rules receive five additive costs:

1. Absolute error from target Hamming changes, divided by step count.
2. Absolute error from target hits, divided by step count; -1 disables this term.
3. Euclidean weight times minimum Hamming distance to the Euclidean necklace at
   the candidate's own length/density, divided by step count; zero disables it.
4. Maximum normalized similarity to the most recent `memory` rhythms, including
   the current rhythm. Zero history disables it. This discourages repetition.
5. Absolute error from the sync target; -1 disables it. The explicit proxy is the
   fraction of onsets off a beat boundary whose next beat boundary is silent.
   Beat boundaries are steps 0, b, 2b, ... for steps-per-beat b, which must divide
   the cycle length. This is a simple configurable metric, not a perceptual model.

Selection uses `exp(-(cost - minimumCost) / temperature)`, normalized over the
candidate pool. Temperature must be finite and positive; lower values favor the
lowest cost. Scores are preferences, not extra constraints. To demand a fixed
hit count, use HITS(n) in the clause. Pool probabilities and each score component
are exported in evolution JSONL.

## Search, reproducibility and limits

Completion visits assignments to unknown bits in a seeded affine permutation
modulo 2^unknowns. The odd stride visits each assignment exactly once if allowed
to finish. It never enumerates a full rhythm graph or duplicates a candidate.
Search stops at 1–100,000 tested assignments or 1–10,000 accepted candidates.
Statuses distinguish EXHAUSTED (complete search), RESULT_LIMIT, and BUDGET.
An empty BUDGET result is not a proof of impossibility. Large arbitrary masks
remain exponential; use fewer unknowns or a more permissive rule.

The affine visit order and truncated pool are **not uniform sampling over all
valid rhythms**. Selection probabilities apply only to the pool actually built.
Evolution also adds the unchanged rhythm if it satisfies the transition and was
missed by the bounded search; the pool may thus have limit+1 entries. It is a
valid fallback, not a forced change. If no valid transition is found and holding
would violate the transition rule, evolution stops with an error. Rules are
never relaxed implicitly.

The initial rhythm, settings, and seed reproduce a session. Iteration t uses
seed+t, an erasure draw, a search-seed draw, then a selection draw. UI and CLI
use the same implementation. JSONL includes previous rhythm, released mask,
selected bits, changed count, hit count, score components, selection probability,
search status and attempts. Keep the initial setup alongside a log to replay it.

## CLI

Build the dependency jar using the existing Maven assembly configuration:

```sh
mvn test package assembly:single
export RHYTHM_JAR="$PWD/target/name.ncg777-20260502T1743Z-jar-with-dependencies.jar"
sh scripts/rhythm-explorer.sh
sh scripts/rhythm.sh predicates
sh scripts/rhythm.sh complete --cipher Octal --mask '4?2?' --where 'EUCLIDEAN(5)'
sh scripts/rhythm.sh mask --start '9249' --erase-unit digit --erase-count 2
sh scripts/rhythm.sh evolve --start '9249' --where 'HITS(6)' \
  --erase-count 4 --target-changes 2 --avoid-recent 8 \
  --temperature 0.15 --iterations 64 --seed 777 --format jsonl > evolution.jsonl
```

Scripts default to `name.ncg777.jar` in the working directory if RHYTHM_JAR is
unset. `.cmd` equivalents are included. Quote masks/clauses to avoid shell globbing.
`mask` prints its output cipher on stderr, using Binary when necessary to retain
partial bits. `complete` prints status on stderr; stdout contains only rhythms
(or JSONL with `--format jsonl`). `evolve --iterations 0` streams until interrupted;
a finite run supports up to 1,000,000 iterations. Broken output pipes stop streaming.

Completion exit codes: 0 = at least one result; 1 = exhaustive no-solution;
3 = budget exhausted with no result. Picocli syntax errors use 2; execution or
validation errors use 1. All commands support `--help`.

## Validation

Tests compare completion with a brute-force oracle, assert known/protected rests
and onsets, verify Euclidean necklaces and a counterexample with rearranged gaps,
check budgets, cancellation, hold/invalid-transition behavior, JSON round trips,
MIDI channel/tick timing, CLI/engine reproducibility, and Swing complete/reuse/evolve/
stop actions. The Swing panel is rendered offscreen for layout inspection.
