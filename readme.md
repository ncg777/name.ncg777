# name.ncg777
Until further notice, this application is a prototype. Some of these apps may be considered experimental or may contain unusual terminology.

First, you should extract the zip file wherever you want.

To run the main menu application that lets one open the apps with graphical interfaces, simply double click on the jar file or run the name.ncg777.cmd (windows) or name.ncg777.sh (linux) script.

# Partition functions and ternary networks

The library includes stable finite Boltzmann distributions, ternary attraction-neutrality-repulsion
models with exact statistics and Gibbs sampling, and Boolean-lattice zeta/Möbius transforms.
See [the guide and runnable examples](docs/partition-functions.md) for constrained pattern
generation, trajectory weighting, thermal activations and learning a coupling from observations.

A [minimal ternary network training framework](docs/ternary-training.md) learns connections,
biases and neutrality from example patterns, with exact or sampled training, work limits,
validation-based early stopping and reusable JSON checkpoints.

# Rhythm Network CLI (Interactive)
`name.ncg777.maths.numbers.fixed.rhythm.apps.RhythmNetworkApp` now supports an interactive session mode for keeping multiple networks loaded in memory and reusing them for fast walk generation.

Start interactive mode with:

`--interactive` (or `-i`)

Inside interactive mode, type `help`.

Main commands:
- `list` : show loaded networks (`*` marks selected)
- `build` : build and add a network in memory
- `load` : load and add a saved `.rnet` network
- `select <name>` : select active network
- `save` : save a loaded network to file
- `unload <name|selected|all>` : remove network(s) from memory
- `analyze [navTemp]` : run analysis on selected network
- `walk [steps] [navTemp] [seed]` : generate one walk
- `matrix` : generate N constrained walks as a matrix
- `quit` / `exit` : leave interactive mode

`matrix` prompts for:
- walk count `N`
- steps
- navigation temperature `τ`
- seed (`-1` for random)
- successive clause (applied to adjacent walk elements via juxtaposition)
- simultaneous clause (applied across matrix rows via differences)
- max attempts

Typical session:
1. `build` (or `load`)
2. `list` / `select <name>`
3. `walk` for single walk, or `matrix` for constrained N-walk output
4. `save` if needed
5. `unload` when done

# Enumeration Scripts Documentation
Arguments' names cannot be specified and are just determined positionally.

The int[] type is printed or parsed as space-separated lists of integers (ex: "1 2 3") or JSON array-like strings (ex: "[1, 2, 3]").

## bitsequences.sh
- **Class**: name.ncg777.maths.enumerations.BitSequenceEnumeration
- **Parameters**: 
  - `n` (int): The size of the bit sequences.
  
## bitsets.sh
- **Class**: name.ncg777.maths.enumerations.BitSetEnumeration
- **Parameters**: 
  - `n` (int): The size of the bit sets.

## combinations.sh
- **Class**: name.ncg777.maths.enumerations.CombinationEnumeration
- **Parameters**: 
  - `n` (int): The size of the set.
  - `k` (int): The size of the subsets.

## compositions.sh
- **Class**: name.ncg777.maths.enumerations.CompositionEnumeration
- **Parameters**: 
  - `n` (int): The integer to be partitioned.

## crossingpartitions.sh
- **Class**: name.ncg777.maths.enumerations.CrossingPartitionEnumeration
- **Parameters**: 
  - `n` (int): The size of the crossing partitions.
   
## dyckwords.sh
- **Class**: name.ncg777.maths.enumerations.DyckWordEnumeration
- **Parameters**: 
  - `nbOfPairs` (int): The number of pairs of parentheses.

## fixedsetpartitions.sh
- **Class**: name.ncg777.maths.enumerations.FixedSetPartitionEnumeration
- **Parameters**: 
  - `n` (int) : The size of the set.
  - `k` (int) : The number of blocks.

## kpermutations.sh
- **Class**: name.ncg777.maths.enumerations.KPermutationEnumeration
- **Parameters**: 
  - `n` (int): The size of the set.
  - `k` (int): The size of the permutations.

## metacompositons.sh
- **Class**: name.ncg777.maths.enumerations.MetaCompositionEnumeration
- **Parameters**: 
  - `s` (String): The String to metacompose (< and > are reserved characters).
  - [`transform`] (boolean): transform the string.

## metacompositonsoflist.sh
- **Class**: name.ncg777.maths.enumerations.MetaCompositionOfListEnumeration
- **Parameters**: 
  - `s` (String): Space separated word list to metacompose (<, > and - are reserved characters).
  - [`transform`] (boolean): transform the string.
  
## mixedradices.sh
- **Class**: name.ncg777.maths.enumerations.MixedRadixEnumeration
- **Parameters**: 
  - `base` (int[]) : The base for the mixed radix enumeration.
  - [`transformation`] (int[]) : transformation.apply(coordinates).
  - [`factor`] (int[]) : Optional factor vector in inner product of elements.
  - [`cumulative_products`] (boolean): Optionally process factors to cumulative products.
 
## ngoodpaths.sh
- **Class**: name.ncg777.maths.enumerations.NGoodPathEnumeration
- **Parameters**: 
  - `n` (int) : n

## noncrossingpartitions.sh
- **Class**: name.ncg777.maths.enumerations.NonCrossingPartitionEnumeration
- **Parameters**: 
  - `n` (int): The size of the non-crossing partitions.

## partitions.sh
- **Class**: name.ncg777.maths.enumerations.PartitionEnumeration
- **Parameters**: 
  - `n` (int) : Integer to partition.

## permutations.sh
- **Class**: name.ncg777.maths.enumerations.PermutationEnumeration
- **Parameters**: 
  - `n` (int) : n
  
## regularlanguage.sh
- **Class**: name.ncg777.maths.enumerations.RegexEnumeration
- **Parameters**:
  - `regex` (String) : the regular expression to enumerate
  - [`stopAt`] (int) : the maximum number of elements to enumerate (-1 for no limit)
  - [`maxLength`] (int) : the maximum length of strings to generate
  
## setpartitions.sh
- **Class**: name.ncg777.maths.enumerations.SetPartitionEnumeration
- **Parameters**: 
  - `n` (int) : Size of partition.

## weakcompositions.sh
- **Class**: name.ncg777.maths.enumerations.WeakCompositionEnumeration
- **Parameters**: 
  - `n` (int) : Total.
  - `k` (int) : Number of summands.

## weakorders.sh
- **Class**: name.ncg777.maths.enumerations.WeakOrdersEnumeration
- **Parameters**: 
  - `n` (int) : Size of ordering.
  
## words.sh
- **Class**: name.ncg777.maths.enumerations.WordEnumeration
- **Parameters**: 
  - `length` (int) : Length of word.
  - `size` (int) : Size of alphabet.

## wordpermutations.sh
- **Class**: name.ncg777.maths.enumerations.WordPermutationEnumeration
- **Parameters**: 
  - `rk` (int[]) : List of the number of occurrences of each element. The sum of this list will be the length of the output lists.

---
This application was compiled with Microsoft's build of OpenJDK 21.

## Market forecast

A Swing app for free-source price charts and two-bar probabilistic forecasts, with symbol/bar-size selection,
background training, automatic refresh, CSV import and a held-out independent baseline comparison.
Launch **Market forecast** from the main menu; see [the guide](docs/market-forecast.md).

## Virtual ternary machine

`TritWord64` represents exact 64-trit balanced integers. `TernaryMachine` executes
three-symbol transition programs on a sparse tape with an explicit step budget.
Run a program from a text file or use the Java API; see [the machine guide](docs/ternary-machine.md).

Open **Ternary functional language** from the main menu to write programs with
integers, recursive functions, lexical closures, and three-way branches. A microcoded
controller executes arithmetic and call frames on the ternary tape; see
[the language guide](docs/ternary-functional-runtime.md).

## Ternary associative memory

Open **Ternary associative memory** to compare learned spin-model recall with
nearest stored patterns. Hide positions with `?`, allow noisy observations, and
measure recovery of damaged memories; see [the guide](docs/ternary-associative-memory.md).

Open **Ternary image laboratory** to paint black/white/transparent 8×8 memories,
reconstruct hidden pixels, and compare a small convolutional boundary detector
with an explicit rule on synthetic shapes; see [the image guide](docs/ternary-image-laboratory.md).
