package name.ncg.Maths.DataStructures;


public class HomogeneousPair<T extends Comparable<? super T>>
    extends HeterogeneousPair<T,T> {

  public static <T extends Comparable<? super T>> HomogeneousPair<T> makePair(
      T first, T second) {
    return new HomogeneousPair<T>(first, second);
  }
  
  public static <T extends Comparable<? super T>> HomogeneousPair<T> fromOrderedPair(HeterogeneousPair<T,T> o) {
    return new HomogeneousPair<T>(o.getFirst(),o.getSecond());
  }
  

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
    HomogeneousPair<T> other = (HomogeneousPair<T>) obj;
    if (x == null) {
      if (other.x != null) return false;
    } else if (!x.equals(other.x)) return false;
    if (y == null) {
      if (other.y != null) return false;
    } else if (!y.equals(other.y)) return false;
    return true;
  }

  private HomogeneousPair(T p_x, T p_y) {
   super(p_x,p_y);
  }


}
