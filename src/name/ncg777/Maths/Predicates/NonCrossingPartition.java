package name.ncg777.Maths.Predicates;

import java.util.function.Predicate;

import name.ncg777.Maths.Enumerations.CombinationEnumeration;

import java.util.List;

public class NonCrossingPartition implements Predicate<int[]> {

    @Override
    public boolean test(int[] partition) {
        int n = partition.length;

        // If n is less than 4, the partition is trivially non-crossing
        if (n < 4) {
            return true;
        }

        // Use CombinationEnumeration to generate all combinations of 4 elements
        CombinationEnumeration combinationEnum = new CombinationEnumeration(n, 4);

        while (combinationEnum.hasMoreElements()) {
          List<Integer> quad = combinationEnum.nextElement().asSequence();

            int a = quad.get(0);
            int b = quad.get(1);
            int c = quad.get(2);
            int d = quad.get(3);

            // Check if p[a] = p[c] and p[b] = p[d] and p[a] != p[b]
            if (partition[a] == partition[c] &&
                partition[b] == partition[d] &&
                partition[a] != partition[b]) {
                return false; // Found a crossing
            }
        }

        return true; // No crossings found
    }
}
