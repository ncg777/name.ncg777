package name.ncg777.maths;

import name.ncg777.maths.exceptions.InvalidIntervalException;
import name.ncg777.maths.exceptions.InvalidIntervalOperationException;

/**
 * Interval on a total order with a notion of open and closed boundaries.
 * 
 * This class represents mathematical intervals with support for:
 * - Closed intervals [a,b] (boundaries included)
 * - Open intervals (a,b) (boundaries excluded) 
 * - Half-open intervals [a,b) or (a,b] (one boundary included)
 * - Empty intervals ∅
 * - Degenerate intervals [a,a] (single point)
 * 
 * The implementation follows principles from Allen's interval algebra for
 * interval relationships and operations. All interval operations are
 * mathematically sound and handle edge cases properly.
 * 
 * Thread Safety: This class is immutable and therefore thread-safe.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T> the type of values in the interval, must be Comparable
 */
public class Interval<T extends Comparable<? super T>> implements Comparable<Interval<T>>{
  private T minimum;
  private boolean includesMinimum;
  private T maximum;
  private boolean includesMaximum;

  /**
   * Creates a new interval with specified boundaries and inclusion flags.
   * 
   * @param minimum the lower bound of the interval
   * @param includesMinimum true if the lower bound is included (closed), false if excluded (open)
   * @param maximum the upper bound of the interval  
   * @param includesMaximum true if the upper bound is included (closed), false if excluded (open)
   * @return a new interval with the specified properties
   * @throws InvalidIntervalException if the parameters are inconsistent
   */
  public static <T extends Comparable<? super T>> Interval<T> make(T minimum, boolean includesMinimum, T maximum, boolean includesMaximum) {
    return new Interval<T>(minimum,includesMinimum, maximum, includesMaximum);
  }
  
  /**
   * Creates a closed interval [minimum, maximum] where both boundaries are included.
   * 
   * @param minimum the lower bound (included)
   * @param maximum the upper bound (included)
   * @return a closed interval [minimum, maximum]
   */
  public static <T extends Comparable<? super T>> Interval<T> makeClosedInterval(T minimum, T maximum) {
    return new Interval<T>(minimum,true, maximum, true);
  }

  /**
   * Creates an open interval (minimum, maximum) where both boundaries are excluded.
   * 
   * @param minimum the lower bound (excluded)
   * @param maximum the upper bound (excluded) 
   * @return an open interval (minimum, maximum)
   */
  public static <T extends Comparable<? super T>> Interval<T> makeOpenInterval(T minimum, T maximum) {
    return new Interval<T>(minimum,false, maximum, false);
  }
  
  /**
   * Creates an empty interval ∅ that contains no points.
   * 
   * @return the empty interval
   */
  public static <T extends Comparable<? super T>> Interval<T> getEmptyInterval() {
    return new Interval<T>(null, false, null, false);
  }
  /**
   * Gets the minimum (lower bound) of this interval.
   * 
   * @return the minimum value, or null for empty intervals
   */
  public T getMinimum() {
    return minimum;
  }
  
  /**
   * Gets the maximum (upper bound) of this interval.
   * 
   * @return the maximum value, or null for empty intervals
   */
  public T getMaximum() {
    return maximum;
  }
  
  /** @return true if the left boundary is closed (minimum is included) */
  public boolean isLeftClosed() {return this.includesMinimum;}
  
  /** @return true if the right boundary is closed (maximum is included) */
  public boolean isRightClosed() {return this.includesMaximum;}
  
  /** @return true if the left boundary is open (minimum is excluded) */
  public boolean isLeftOpen() {return !this.includesMinimum;}
  
  /** @return true if the right boundary is open (maximum is excluded) */
  public boolean isRightOpen() {return !this.includesMaximum;}
  
  /** @return true if exactly one boundary is open and one is closed */
  public boolean isHalfOpen() {return this.includesMaximum != this.includesMinimum;}
  
  /** @return true if this is the empty interval ∅ containing no points */
  public boolean isEmpty() { 
    return this.minimum == null && this.maximum == null; 
  }
  
  /** @return true if both boundaries are closed */
  public boolean isClosed() { return this.includesMinimum && this.includesMaximum; }
  
  /** @return true if both boundaries are open */
  public boolean isOpen() { return !this.includesMinimum && !this.includesMaximum;}
  
  /** @return true if this interval represents a single point [a,a] */
  public boolean isDegenerate() { 
    return this.minimum != null && this.minimum.equals(this.maximum) && 
           this.includesMinimum && this.includesMaximum;
  }
  
  /**
   * Tests if this interval is a superset of another interval (this ⊇ p).
   * A superset contains all points of the other interval (allows equality).
   * 
   * @param p the interval to test against
   * @return true if this ⊇ p, false otherwise
   */
  public boolean isSupersetOf(Interval<T> p) {
    if(isEmpty() && p.isEmpty()) return true;
    else if(p.isEmpty()) return true;
    else if(isEmpty()) return false;
    else return this.equals(p) || this.isProperSupersetOf(p);
  }
  
  /**
   * Tests if this interval is a subset of another interval (this ⊆ p).
   * A subset has all its points contained in the other interval (allows equality).
   * 
   * @param p the interval to test against
   * @return true if this ⊆ p, false otherwise
   */
  public boolean isSubsetOf(Interval<T> p) {
    return p.isSupersetOf(this);
  }
  
  /**
   * Merges two overlapping intervals into a single interval.
   * The resulting interval will have the minimum of the two minimums and the maximum 
   * of the two maximums, with boundary inclusion rules following interval union semantics.
   * 
   * Algorithm:
   * 1. Verify intervals overlap (prerequisite for merge)
   * 2. Determine merged minimum: take the smaller minimum value, include boundary if either interval includes it
   * 3. Determine merged maximum: take the larger maximum value, include boundary if either interval includes it
   * 
   * @param other the interval to merge with this interval
   * @return a new interval representing the union of this and other
   * @throws InvalidIntervalOperationException if intervals do not overlap
   */
  public Interval<T> mergeWith(Interval<T> other) {
    if (!this.overlapsWith(other)) {
      throw new InvalidIntervalOperationException(
        "Cannot merge non-overlapping intervals: " + this + " and " + other);
    }
    
    // Handle empty intervals
    if (this.isEmpty()) return other;
    if (other.isEmpty()) return this;
    
    // Determine merged minimum and its inclusion
    T mergedMin = selectMinimum(this.minimum, other.minimum);
    boolean includeMin = shouldIncludeMinimum(this, other, mergedMin);
    
    // Determine merged maximum and its inclusion  
    T mergedMax = selectMaximum(this.maximum, other.maximum);
    boolean includeMax = shouldIncludeMaximum(this, other, mergedMax);
    
    return make(mergedMin, includeMin, mergedMax, includeMax);
  }
  
  /**
   * Helper method to select the minimum value from two intervals.
   */
  private T selectMinimum(T min1, T min2) {
    return min1.compareTo(min2) <= 0 ? min1 : min2;
  }
  
  /**
   * Helper method to select the maximum value from two intervals.
   */
  private T selectMaximum(T max1, T max2) {
    return max1.compareTo(max2) >= 0 ? max1 : max2;
  }
  
  /**
   * Helper method to determine if the merged minimum boundary should be included.
   * The boundary is included if either interval that has this minimum includes it.
   */
  private boolean shouldIncludeMinimum(Interval<T> interval1, Interval<T> interval2, T mergedMin) {
    boolean firstHasMin = interval1.minimum.equals(mergedMin);
    boolean secondHasMin = interval2.minimum.equals(mergedMin);
    
    return (firstHasMin && interval1.includesMinimum) || 
           (secondHasMin && interval2.includesMinimum);
  }
  
  /**
   * Helper method to determine if the merged maximum boundary should be included.
   * The boundary is included if either interval that has this maximum includes it.
   */
  private boolean shouldIncludeMaximum(Interval<T> interval1, Interval<T> interval2, T mergedMax) {
    boolean firstHasMax = interval1.maximum.equals(mergedMax);
    boolean secondHasMax = interval2.maximum.equals(mergedMax);
    
    return (firstHasMax && interval1.includesMaximum) || 
           (secondHasMax && interval2.includesMaximum);
  }
  
  /**
   * Tests if this interval is a proper superset of another interval.
   * A proper superset means this interval contains all points of the other interval
   * and at least one additional point (they are not equal).
   * 
   * Based on Allen's interval algebra, this implements the "contains" relationship
   * where this interval strictly contains the other.
   * 
   * Algorithm:
   * 1. Handle empty interval cases
   * 2. Check if intervals are equal (proper superset requires strict containment)
   * 3. Verify this interval's boundaries encompass the other interval's boundaries
   * 4. Account for open/closed boundary semantics
   * 
   * @param p the interval to test containment against
   * @return true if this ⊃ p (this properly contains p), false otherwise
   */
  public boolean isProperSupersetOf(Interval<T> p) {
    // Empty interval special cases
    if (this.isEmpty() && p.isEmpty()) return false;  // Empty cannot be proper superset of empty
    if (p.isEmpty()) return !this.isEmpty();         // Non-empty is proper superset of empty
    if (this.isEmpty()) return false;                // Empty cannot be proper superset of non-empty
    
    // Equal intervals cannot have proper superset relationship
    if (this.equals(p)) return false;
    
    // Check if this interval contains the other interval
    return this.containsInterval(p);
  }
  
  /**
   * Helper method to test if this interval contains another interval entirely.
   * This handles the complex boundary inclusion logic systematically.
   */
  private boolean containsInterval(Interval<T> other) {
    // Check minimum boundary containment
    boolean minContained = isMinimumContained(other);
    boolean maxContained = isMaximumContained(other);
    
    return minContained && maxContained;
  }
  
  /**
   * Helper method to check if this interval contains the other's minimum boundary.
   */
  private boolean isMinimumContained(Interval<T> other) {
    int minComparison = this.minimum.compareTo(other.minimum);
    
    if (minComparison < 0) {
      // This minimum is strictly less, so it contains other's minimum
      return true;
    } else if (minComparison == 0) {
      // Same minimum value, check boundary inclusion rules
      // This contains other's minimum if this includes the boundary or other excludes it
      return this.includesMinimum || !other.includesMinimum;
    } else {
      // This minimum is greater, so it cannot contain other's minimum
      return false;
    }
  }
  
  /**
   * Helper method to check if this interval contains the other's maximum boundary.
   */
  private boolean isMaximumContained(Interval<T> other) {
    int maxComparison = this.maximum.compareTo(other.maximum);
    
    if (maxComparison > 0) {
      // This maximum is strictly greater, so it contains other's maximum
      return true;
    } else if (maxComparison == 0) {
      // Same maximum value, check boundary inclusion rules
      // This contains other's maximum if this includes the boundary or other excludes it
      return this.includesMaximum || !other.includesMaximum;
    } else {
      // This maximum is smaller, so it cannot contain other's maximum
      return false;
    }
  }
  
  /**
   * Tests if this interval is a proper subset of another interval (this ⊂ p).
   * A proper subset means all points are contained but the intervals are not equal.
   * 
   * @param p the interval to test against
   * @return true if this ⊂ p, false otherwise
   */
  public boolean isProperSubsetOf(Interval<T> p) {
    return p.isProperSupersetOf(this);
  }
  
  /**
   * Tests if this interval contains a specific point.
   * The containment respects the boundary inclusion rules.
   * 
   * @param item the point to test for containment
   * @return true if the point is contained in this interval, false otherwise
   */
  public boolean contains(T item) {
    if(isEmpty()) return false;
    return (this.includesMinimum ? item.compareTo(minimum) >= 0 : item.compareTo(minimum) > 0) && 
        (this.includesMaximum ? item.compareTo(maximum) <= 0 : item.compareTo(maximum) < 0);
  }
  
  /**
   * Tests if this interval overlaps with another interval.
   * Two intervals overlap if they share at least one common point.
   * This implements Allen's interval algebra overlap relationships.
   * 
   * Algorithm based on the fact that two intervals [a,b] and [c,d] do NOT overlap if:
   * - b < c (first entirely before second), or  
   * - d < a (second entirely before first)
   * So they DO overlap if neither of these conditions is true.
   * 
   * The boundary inclusion rules are handled by the comparison logic:
   * - For boundary point equality, inclusion flags determine overlap
   * 
   * @param p the interval to test overlap against
   * @return true if this overlaps with p, false otherwise
   */
  public boolean overlapsWith(Interval<T> p) {
    // Empty intervals don't overlap with anything
    if (this.isEmpty() || p.isEmpty()) return false;
    
    // Check if this interval ends before the other starts
    if (this.isBeforeInterval(p)) return false;
    
    // Check if the other interval ends before this starts  
    if (p.isBeforeInterval(this)) return false;
    
    // If neither interval is completely before the other, they must overlap
    return true;
  }
  
  /**
   * Helper method to test if this interval is completely before another interval.
   * This interval is before another if its maximum is less than the other's minimum,
   * accounting for boundary inclusion rules.
   */
  private boolean isBeforeInterval(Interval<T> other) {
    int comparison = this.maximum.compareTo(other.minimum);
    
    if (comparison < 0) {
      // This maximum is strictly less than other minimum - definitely before
      return true;
    } else if (comparison == 0) {
      // Maximum equals minimum - check boundary inclusion
      // They don't overlap if this excludes its right boundary OR other excludes its left boundary
      return !this.includesMaximum || !other.includesMinimum;
    } else {
      // This maximum is greater than other minimum - not before
      return false;
    }
  }
  
  @Override
  public String toString() {
    if(isEmpty()) return "∅";
    return (includesMinimum ? "[":"(") + minimum.toString() + "," + maximum.toString() + (includesMaximum ? "]" : ")");
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if(!isEmpty()) {
      result = prime * result + minimum.hashCode();
      result = prime * result + maximum.hashCode();
    }
    result = prime * result + Boolean.hashCode(includesMinimum);
    result = prime * result + Boolean.hashCode(includesMaximum);
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("unchecked")
    Interval<T> other = (Interval<T>) obj;
    if(this.isEmpty() && other.isEmpty()) return true;
    if (maximum == null) {
      if (other.maximum != null) return false;
    } else if (!maximum.equals(other.maximum)) return false;
    if (minimum == null) {
      if (other.minimum != null) return false;
    } else if (!minimum.equals(other.minimum)) return false;
    if(includesMinimum != other.includesMinimum || includesMaximum != other.includesMaximum) return false;
    
    return true;
  }
  
  
  @Override
  public int compareTo(Interval<T> other) {
    if(this.equals(other)) return 0;
    else if(this.minimum.compareTo(other.minimum) < 0) return -1;
    else if(this.minimum.equals(other.minimum) && this.isLeftClosed() && other.isLeftOpen()) return -1;
    else if(this.minimum.equals(other.minimum) && this.isLeftOpen() && other.isLeftClosed()) return 1;
    else if(
        (this.minimum.equals(other.minimum) && this.isLeftClosed() && other.isLeftClosed()) ||
        (this.minimum.equals(other.minimum) && this.isLeftOpen() && other.isLeftOpen())) {
      if(this.maximum.compareTo(other.maximum) < 0) return -1;
      else if(this.maximum.equals(other.maximum) && this.isRightOpen() && other.isRightClosed()) return -1;
      else if(this.maximum.equals(other.maximum) && this.isRightClosed() && other.isRightOpen()) return 1;
      // if both maxima are equal and are both open or closed, then the intervals are equal; this case is already handled
      else if(this.maximum.compareTo(other.maximum) > 0) return 1;
    }
    else if(this.minimum.compareTo(other.minimum) > 0) return 1;
    
    throw new InvalidIntervalOperationException("Unhandled comparison case between intervals: " + this + " and " + other);
  }
  
  private Interval(T minimum, boolean includesMinimum, T maximum, boolean includesMaximum)  {
    super();
    if ((minimum == null) != (maximum == null)) {
      throw new InvalidIntervalException("minimum cannot be null if maximum is not also null and vice versa.");
    } else if (minimum == null && maximum == null && (includesMinimum || includesMaximum)) {
      throw new InvalidIntervalException("null minimum and maximum is only for the empty interval.");
    } else if(minimum != null && maximum != null && minimum.equals(maximum) && includesMinimum != includesMaximum) {
      throw new InvalidIntervalException("contradictory arguments for single-point interval: both boundaries must have same inclusion.");
    }
    
    this.includesMinimum = includesMinimum;
    this.includesMaximum = includesMaximum;
    if(minimum != null && maximum != null && 
        // if minimum == maximum and both ends are open, 
        // we have an empty set and the minimum and maximum remain null
        !(minimum.compareTo(maximum) == 0 && !includesMinimum && !includesMaximum)) {
      // if minimum >= maximum, the values are swapped.
      if(minimum.compareTo(maximum) < 0) {
        this.minimum = minimum;
        this.maximum = maximum;  
      } else {
        this.minimum = maximum;
        this.maximum = minimum;
      }
    }
  }
}
