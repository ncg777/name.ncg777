package name.ncg777.mathematics.relations;


import java.util.function.BiPredicate;
/**
 * Majorization bipredicate for number arrays.
 * 
 * @param <T>
 * @author meta.ai
 */
public class MajorizationPredicate<T extends Number> implements BiPredicate<T[], T[]> {

    @Override
    public boolean test(T[] a, T[] b) {
        if (a == null || b == null) {
            throw new NullPointerException("Arrays cannot be null");
        }

        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of the same length");
        }

        double[] prefixSumsA = calculatePrefixSums(a);
        double[] prefixSumsB = calculatePrefixSums(b);

        for (int i = 0; i < prefixSumsA.length; i++) {
            if (prefixSumsA[i] > prefixSumsB[i]) {
                return false;
            }
        }

        return true;
    }

    private double[] calculatePrefixSums(T[] array) {
        double[] prefixSums = new double[array.length];
        prefixSums[0] = array[0].doubleValue();

        for (int i = 1; i < array.length; i++) {
            prefixSums[i] = prefixSums[i - 1] + array[i].doubleValue();
        }

        return prefixSums;
    }
}