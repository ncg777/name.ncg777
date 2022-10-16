package name.NicolasCoutureGrenier.Maths.DataStructures;


public class HomogeneousPair<T extends Comparable<? super T>>
    extends HeterogeneousPair<T,T> {

  public static <T extends Comparable<? super T>> HomogeneousPair<T> makeHomogeneousPair(
      T first, T second) {
    return new HomogeneousPair<T>(first, second);
  }
  
  public static <T extends Comparable<? super T>> HomogeneousPair<T> fromHeterogeneousPair(HeterogeneousPair<T,T> o) {
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
    return this.compareTo(other) == 0;
  }

  private HomogeneousPair(T p_x, T p_y) {
   super(p_x,p_y);
  }


}
