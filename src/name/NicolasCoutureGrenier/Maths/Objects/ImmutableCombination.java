package name.NicolasCoutureGrenier.Maths.Objects;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

public class ImmutableCombination implements Serializable, Comparable<ImmutableCombination> {

    private static final long serialVersionUID = 1L;
    
    private final Combination combination;

    // Private constructor initializes the internal Combination instance.
    private ImmutableCombination(Combination combination) {
        this.combination = combination;
    }

    public ImmutableCombination(Integer p_n) {
        this(new Combination(p_n));
    }

    public ImmutableCombination(Integer p_n, Set<Integer> p_s) {
        this(new Combination(p_n, p_s));
    }

    public ImmutableCombination(BitSet c, int n) {
        this(new Combination(c, n));
    }

    public Integer getN() {
        return combination.getN();
    }

    public int getK() {
        return combination.getK();
    }

    public int calcSpan() {
        return combination.calcSpan();
    }

    public Sequence getIntervalVector() {
        return combination.getIntervalVector();
    }

    public double calcNormalizedDistanceWith(ImmutableCombination other) {
        return combination.calcNormalizedDistanceWith(other.combination);
    }

    public ImmutableCombination symmetricDifference(ImmutableCombination y) {
        return new ImmutableCombination(combination.symmetricDifference(y.combination));
    }

    public ImmutableCombination rotate(int t) {
        return new ImmutableCombination(combination.rotate(t));
    }

    public ImmutableCombination intersect(ImmutableCombination c) {
        return new ImmutableCombination(combination.intersect(c.combination));
    }

    public ImmutableCombination minus(ImmutableCombination c) {
        return new ImmutableCombination(combination.minus(c.combination));
    }

    public ImmutableCombination[] partition(Integer[] partition) {
        Combination[] result = combination.partition(partition);
        return toImmutableCombinations(result);
    }

    public ImmutableCombination[] partition(Sequence p0) {
        Combination[] result = combination.partition(p0);
        return toImmutableCombinations(result);
    }

    public static ImmutableCombination[] toImmutableCombinations(Combination[] combinations) {
        ImmutableCombination[] immutableCombinations = new ImmutableCombination[combinations.length];
        for (int i = 0; i < combinations.length; i++) {
            immutableCombinations[i] = new ImmutableCombination(combinations[i]);
        }
        return immutableCombinations;
    }

    public Sequence asSequence() {
        return combination.asSequence();
    }
    public Sequence asBinarySequence() {
      return this.combination.asBinarySequence();
    }
    public Composition getComposition() {return this.combination.getComposition();}
    public ImmutableCombination merge(ImmutableCombination other) {
      return new ImmutableCombination(Combination.merge(this.combination, other.combination));
    }
    public static ImmutableCombination fromBinarySequence(Sequence s) {
      return new ImmutableCombination(Combination.fromBinarySequence(s));
    }
    public boolean get(int bitIndex) {return this.combination.get(bitIndex);}
    public boolean isEmpty() {return this.combination.isEmpty();}
    @Override
    public String toString() {
        return combination.toString();
    }

    public Combination getCombinationCopy() {
      return new Combination(this.combination);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ImmutableCombination)) return false;
        ImmutableCombination other = (ImmutableCombination) obj;
        return combination.equals(other.combination);
    }

    @Override
    public int hashCode() {
        return combination.hashCode();
    }

    public int compareTo(ImmutableCombination o) {
        return combination.compareTo(o.combination);
    }

    // Example of a method that produces combinations
    public ImmutableCombination[] generate(int k) {
        Combination[] combinations = Combination.generate(combination.getN(), k);
        return toImmutableCombinations(combinations);
    }
}
