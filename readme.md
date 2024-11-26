# name.ncg777
This application was compiled with Microsoft's build of OpenJDK 21.

To run the main menu application that lets one open the apps with graphical interfaces, run the name.ncg777.sh script.

# Enumeration Scripts Documentation
Arguments' names cannot be specified and are just determined positionally at the moment.

## bitsets.sh
- **Class**: name.ncg777.Maths.Enumerations.BitSetEnumeration
- **Parameters**: 
  - `n` (int): The size of the bitset.

## combinations.sh
- **Class**: name.ncg777.Maths.Enumerations.CombinationEnumeration
- **Parameters**: 
  - `n` (int): The size of the set.
  - `k` (int): The size of the subsets.

## compositions.sh
- **Class**: name.ncg777.Maths.Enumerations.CompositionEnumeration
- **Parameters**: 
  - `n` (int): The integer to be partitioned.

## dyckwords.sh
- **Class**: name.ncg777.Maths.Enumerations.DyckWordEnumeration
- **Parameters**: 
  - `nbOfPairs` (int): The number of pairs of parentheses.

## fixedsetpartitions.sh
- **Class**: name.ncg777.Maths.Enumerations.FixedSetPartitionEnumeration
- **Parameters**: 
  - `n` (int) : The size of the set.
  - `k` (int) : The number of blocks.

## kpermutations.sh
- **Class**: name.ncg777.Maths.Enumerations.KPermutationEnumeration
- **Parameters**: 
  - `n` (int): The size of the set.
  - `k` (int): The size of the permutations.

## mixedradices.sh
- **Class**: name.ncg777.Maths.Enumerations.MixedRadixEnumeration
- **Parameters**: 
  - `base` (List[int] or int[] as a space separated list of integers within double quotes) : The base for the mixed radix enumeration.

## ngoodpaths.sh
- **Class**: name.ncg777.Maths.Enumerations.NGoodPathEnumeration
- **Parameters**: 
  - `n` (int) : n

## noncrossingpartitions.sh
- **Class**: name.ncg777.Maths.Enumerations.NonCrossingPartitionEnumeration
- **Parameters**: 
  - `n` (int): The size of the partitions.
  
## metacompositons.sh
- **Class**: name.ncg777.Maths.Enumerations.MetaCompositionEnumeration
- **Parameters**: 
  - `s` (String): The String to metacompose.
  - [`transform`] (boolean): transform the string (purpose yet unknown).
  
## permutations.sh
- **Class**: name.ncg777.Maths.Enumerations.PermutationEnumeration
- **Parameters**: 
  - `n` (int) : n
  
## regularlanguage.sh
- **Class**: name.ncg777.Maths.Enumerations.RegexEnumeration
- **Parameters**:
  - `regex` (String) : the regular expression to enumerate
  - [`stopAt`] (int) : the maximum number of elements to enumerate (-1 for no limit)
  - [`maxLength`] (int) : the maximum length of strings to generate
  
## setpartitions.sh
- **Class**: name.ncg777.Maths.Enumerations.SetPartitionEnumeration
- **Parameters**: 
  - `n` (int) : Size of partition.

## weakcompositions.sh
- **Class**: name.ncg777.Maths.Enumerations.WeakCompositionEnumeration
- **Parameters**: 
  - `n` (int) : Total.
  - `k` (int) : Number of summands.

## weakorders.sh
- **Class**: name.ncg777.Maths.Enumerations.WeakOrdersEnumeration
- **Parameters**: 
  - `n` (int) : Size of ordering.
  
## words.sh
- **Class**: name.ncg777.Maths.Enumerations.WordEnumeration
- **Parameters**: 
  - `length` (int) : Length of word.
  - `size` (int) : Size of alphabet.

## wordpermutations.sh
- **Class**: name.ncg777.Maths.Enumerations.WordPermutationEnumeration
- **Parameters**: 
  - `rk` (List[int] or int[] as a space separated list of integers within double quotes) : List of the number of occurences of each element. The sum of this list will be the length of the output lists.