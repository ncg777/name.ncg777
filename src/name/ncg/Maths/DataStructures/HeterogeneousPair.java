/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package name.ncg.Maths.DataStructures;


/**
 * This class represents an ordered pair of objects of 2 different types. It overrides hashCode and
 * equals and the pairs can be compared. The generic types must be comparable. Values stored in the
 * pair may be null without it causing problems with equals, hashcode or compareTo.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T> The type of the first element
 * @param <U> The type of the second element
 */
public class HeterogeneousPair<T extends Comparable<? super T>, U extends Comparable<? super U>>
    implements
      Comparable<HeterogeneousPair<T, U>> {
  /**
   * Static method to construct a pair.
   * 
   * @param first
   * @param second
   * @return HeterogeneousPair<T,U>
   */
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> HeterogeneousPair<T, U> makeHeterogeneousPair(
      T first, U second) {
    return new HeterogeneousPair<T, U>(first, second);
  }

  protected T x;
  protected U y;

  public T getFirst() {
    return x;
  }

  public U getSecond() {
    return y;
  }

  public HeterogeneousPair<U,T> converse() {return makeHeterogeneousPair(y, x);}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (x != null) {
      result = prime * result + x.hashCode();
    }
    if (y != null) {
      result = prime * result + y.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("unchecked")
    HeterogeneousPair<T, U> other = (HeterogeneousPair<T, U>) obj;
    if (x == null) {
      if (other.x != null) return false;
    } else if (!x.equals(other.x)) return false;
    if (y == null) {
      if (other.y != null) return false;
    } else if (!y.equals(other.y)) return false;
    return true;
  }

  protected HeterogeneousPair(T p_x, U p_y) {
    x = p_x;
    y = p_y;
  }

  /**
   * This compareTo function supports HeterogeneousPair with null values.
   */
  @Override
  public int compareTo(HeterogeneousPair<T, U> o) {
    if (o == null) {
      return 1;
    }

    if (x == null && o.x == null) {
      if (y == null) {
        if (o.y == null) {
          return 0;
        }// x = o.x = y = o.y = null
        else {
          return -1;
        } // x = o.x = y = null, o.y != null
      } else {
        if (o.y == null) {
          return 1;
        } // x = o.x = o.y = null, y!=null
        else {
          return y.compareTo(o.y);
        } // x = o.x = null, y!=null, o.y!=null
      }
    } else if (x == null && o.x != null) {
      return -1;
    } // x null, o.x!=null
    else if (x != null && o.x == null) {
      return 1;
    } // x !=null, o.x = null
    else { // x !=null, o.x !=null
      if (y == null && o.y == null) {
        return x.compareTo(o.x);
      } // x !=null, o.x !=null, y = o.y = null
      else if (y == null && o.y != null) {
        return -1;
      } // x !=null, o.x !=null, y = null, o.y!=null
      else if (y != null && o.y == null) {
        return 1;
      } // x !=null, o.x !=null, y != null, o.y = null
      else { // x, o.x, y, o.y != null
        if (x.compareTo(o.x) == 0) {
          return y.compareTo(o.y);
        } else {
          return x.compareTo(o.x);
        }
      }
    }
  }

  @Override
  public String toString() {
    return "<" + (x == null ? "null" : x.toString()) + "," + (y == null ? "null" : y.toString())
        + '>';
  }

}
