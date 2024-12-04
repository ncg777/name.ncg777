package name.ncg777.maths.predicates.sequences;

import java.util.function.Predicate;

import name.ncg777.maths.enumerations.CombinationEnumeration;
import name.ncg777.maths.objects.Sequence;

import java.util.List;

public class NonCrossingPartition implements Predicate<Sequence> {

    @Override
    public boolean test(Sequence partition) {
        int n = partition.size();

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

            // Check if p.get(a) = p.get(c) and p.get(b) = p.get(d) and p.get(a) != p.get(b)
            if (partition.get(a) == partition.get(c) &&
                partition.get(b) == partition.get(d) &&
                partition.get(a) != partition.get(b)) {
                return false; // Found a crossing
            }
        }

        return true; // No crossings found
    }
}
