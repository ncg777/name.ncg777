package name.ncg777.Maths.Objects;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;


/**
 * The {@code ImmutableCombination} class represents a combination of elements that cannot 
 * be modified after creation. It encapsulates an instance of the {@code Combination} class, 
 * providing an immutable interface for handling set operations relevant to combinatorial 
 * structures in various contexts, including music theory.
 *
 * <p>This class implements the {@code Serializable} interface, allowing instances of 
 * {@code ImmutableCombination} to be serialized and deserialized, which is useful for 
 * saving state and data persistence.</p>
 *
 * <p>Key functionalities include:</p>
 * <ul>
 * <li>Creation of combinations using different constructors that accept various parameters 
 * such as a size, a set of elements, or a binary representation.</li>
 * <li>Support for common combinatorial operations like intersection, difference, and 
 * symmetric difference through methods like {@link #intersect(ImmutableCombination)} and
 * {@link #symmetricDifference(ImmutableCombination)}.</li>
 * <li>Rotation and partitioning of combinations to facilitate the exploration of 
 * combinatorial structures, utilizing methods such as {@link #rotate(int)} and 
 * {@link #partition(Integer[])}.</li>
 * <li>Distance calculations between combinations using 
 * {@link #calcNormalizedDistanceWith(ImmutableCombination)}.</li>
 * <li>A variety of utility methods for accessing internal properties of the combination, 
 * such as getting its size with {@link #getN()} or its set representation through 
 * {@link #asSequence()}.</li>
 * </ul>
 *
 * <p>This class is designed to be used with the related {@code Combination} class, which 
 * contains the mutable logic necessary for generating and manipulating combinations.</p>
 *
 * @see Combination
 */
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

    public static ImmutableCombination fromCombination(Combination c) {
      return new ImmutableCombination(new Combination(c));
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
    public Set<Integer> asSet() {
      return combination.asSet();
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
