package name.ncg.Maths.DataStructures;

/**
 * Interval on a total order.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T>
 */
public class Interval<T extends Comparable<? super T>> implements Comparable<Interval<T>>{
  private T minimum;
  private boolean includesMinimum;
  private T maximum;
  private boolean includesMaximum;

  public static <T extends Comparable<? super T>> Interval<T> make(T minimum, boolean includesMinimum, T maximum, boolean includesMaximum) {
    return new Interval<T>(minimum,includesMinimum, maximum, includesMaximum);
  }
  
  public static <T extends Comparable<? super T>> Interval<T> makeClosedInterval(T minimum, T maximum) {
    return new Interval<T>(minimum,true, maximum, true);
  }

  public static <T extends Comparable<? super T>> Interval<T> makeOpenInterval(T minimum, T maximum) {
    return new Interval<T>(minimum,false, maximum, false);
  }
  
  public static <T extends Comparable<? super T>> Interval<T> getEmptyInterval() {
    return new Interval<T>(null, true, null, true);
  }
  
  public T getMinimum() {
    return minimum;
  }
  
  public T getMaximum() {
    return maximum;
  }
  
  public boolean isLeftClosed() {return this.includesMinimum;}
  public boolean isRightClosed() {return this.includesMaximum;}
  public boolean isLeftOpen() {return !this.includesMinimum;}
  public boolean isRightOpen() {return !this.includesMaximum;}
  public boolean isHalfOpen() {return this.includesMaximum != this.includesMinimum;}
  public boolean isEmpty() { return this.minimum.equals(maximum) && !this.includesMinimum && !this.includesMaximum; }
  public boolean isClosed() { return this.includesMinimum && this.includesMaximum; }
  public boolean isOpen() { return !this.includesMinimum && !this.includesMaximum;}
  public boolean isDegenerate() { return this.minimum.equals(this.maximum) && this.includesMinimum && this.includesMaximum;}
  
  /**
   * this ⊇ p
   * @param p
   * @return
   */
  public boolean isSupersetOf(Interval<T> p) {
    if(isEmpty() && p.isEmpty()) return true;
    else if(p.isEmpty()) return true;
    else if(isEmpty()) return false;
    else return this.equals(p) || this.isProperSupersetOf(p);
  }
  /**
   * this ⊆ p
   * @param p
   * @return
   */
  public boolean isSubsetOf(Interval<T> p) {
    return p.isSupersetOf(this);
  }
  
  /**
   * Merges 2 overlapping intervals.
   * 
   * @param other
   * @return
   */
  public Interval<T> mergeWith(Interval<T> other) {
    if(!this.overlapsWith(other)) {
      throw new UnsupportedOperationException();
    }
    
    T minimum = null;
    Boolean include_min = null; 
    if(this.minimum.compareTo(other.minimum) < 0) {
      minimum = this.minimum;
      include_min = this.includesMinimum;
    } else if(this.minimum.equals(other.minimum) ) {
      minimum = this.minimum;
      include_min = this.includesMinimum || other.includesMinimum;
    } else if(this.minimum.compareTo(other.minimum) > 0) {
      minimum = other.minimum;
      include_min = other.includesMinimum;
    }
    
    T maximum = null;
    Boolean include_max = null; 
    if(this.maximum.compareTo(other.maximum) < 0) {
      maximum = other.maximum;
      include_max = other.includesMaximum;
    } else if(this.maximum.equals(other.maximum) ) {
      maximum = this.maximum;
      include_max = this.includesMaximum || other.includesMaximum;
    } else if(this.maximum.compareTo(other.maximum) > 0) {
      maximum = this.maximum;
      include_max = this.includesMaximum;
    }
    return make(minimum, include_min, maximum, include_max);
  }
  
  /**
   * this ⊃ p
   * 
   * The algorithm could be improved.
   *  
   * @param p
   * @return
   */
  public boolean isProperSupersetOf(Interval<T> p) {
    if(isEmpty() && p.isEmpty()) return false;
    else if(p.isEmpty()) return true;
    else if(isEmpty()) return false;
    else if(this.equals(p)) return false;
    // [ ]
    else if(this.isClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
    else if(this.isOpen()) { // ( )
      // [ ]
      if(p.isClosed()) return this.minimum.compareTo(p.minimum) < 0 && this.maximum.compareTo(p.maximum) > 0;
      // ( )
      if(p.isOpen()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
      // [ )
      if(p.isLeftClosed()) return this.minimum.compareTo(p.minimum) < 0 && this.maximum.compareTo(p.maximum) >= 0;
      // ( ]
      if(p.isRightClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) > 0;
    }
    else if(this.isLeftClosed()) { // [ )
      // [ ]
      if(p.isClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) > 0;
      // ( )
      if(p.isOpen()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
      // [ )
      if(p.isLeftClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
      // ( ]
      if(p.isRightClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) > 0;
    } else if(this.isRightClosed()) { // ( ]
      // [ ]
      if(p.isClosed()) return this.minimum.compareTo(p.minimum) < 0 && this.maximum.compareTo(p.maximum) >= 0;
      // ( )
      if(p.isOpen()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
      // [ )
      if(p.isLeftClosed()) return this.minimum.compareTo(p.minimum) < 0 && this.maximum.compareTo(p.maximum) >= 0;
      // ( ]
      if(p.isRightClosed()) return this.minimum.compareTo(p.minimum) <= 0 && this.maximum.compareTo(p.maximum) >= 0;
    }
    
    throw new RuntimeException("unhandled case. this should never happen.");
  }
  
  /**
   * this ⊂ p
   * 
   * @param p
   * @return
   */
  public boolean isProperSubsetOf(Interval<T> p) {
    return p.isProperSupersetOf(this);
  }
  
  public boolean contains(T item) {
    if(isEmpty()) return false;
    return (this.includesMinimum ? item.compareTo(minimum) >= 0 : item.compareTo(minimum) > 0) && 
        (this.includesMaximum ? item.compareTo(maximum) <= 0 : item.compareTo(maximum) < 0);
  }
  
  /**
   * Tests overlapping with another interval.
   * 
   * @param p
   * @return true if this overlaps with p, false otherwise
   */
  public boolean overlapsWith(Interval<T> p) {
    // May or may not work/be efficient... 
    // There must be a more elegant implementation...
    if(this.isEmpty() || p.isEmpty()) return false;
    return
        this.isSupersetOf(p) || 
        p.isSupersetOf(this) ||
        // ? ?
        //  [
        (this.contains(p.minimum) && p.isLeftClosed()) ||
        // ? ]
        //  ? ?
        (p.contains(maximum) && isRightClosed()) || 
        // ? ?
        //  ]
        (this.contains(p.maximum) && p.isRightClosed()) ||
        //  [
        // ? ?
        (p.contains(minimum) && isLeftClosed()) ||
        // ? )
        //  ( ? ...
        (this.isRightOpen() && p.isLeftOpen() && p.minimum.compareTo(maximum) < 0 && p.maximum.compareTo(maximum) >= 0) ||
        //  ( ?
        // ? ) ...
        (this.isLeftOpen() && p.isRightOpen() && p.maximum.compareTo(minimum) > 0 && p.minimum.compareTo(minimum) <= 0);
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
    
    throw new RuntimeException("unhandled case. this should never happen.");
  }
  
  private Interval(T minimum, boolean includesMinimum, T maximum, boolean includesMaximum)  {
    super();
    if ((minimum == null) != (maximum == null)) {
      throw new IllegalArgumentException("minimum cannot be null if maximum is not also null and vice versa.");
    } else if (minimum == null && maximum == null && (includesMinimum || includesMaximum)) {
      throw new IllegalArgumentException("null minimum and maximum is only for the empty interval.");
    } else if (minimum.equals(maximum) && includesMinimum != includesMaximum) {
      throw new IllegalArgumentException("contradictory arguments.");
    }
    
    this.includesMinimum = includesMinimum;
    this.includesMaximum = includesMaximum;
    if(!(minimum == null && maximum == null) && 
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
