# name.ncg777
Until further notice, this application is a prototype. Some of these apps may be considered experimental or may contain unusual terminology.

First, you should extract the zip file wherever you want and add the path to the jar file to your CLASSPATH environment variable.

To run the main menu application that lets one open the apps with graphical interfaces, run the name.ncg777.sh script.

# Enumeration Scripts Documentation
Arguments' names cannot be specified and are just determined positionally at the moment.

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
 - [`factor`] (int[]) : Optional factor vector in inner product of elements.
 
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