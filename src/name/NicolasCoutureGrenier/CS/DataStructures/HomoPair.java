package name.NicolasCoutureGrenier.CS.DataStructures;


public class HomoPair<T extends Comparable<? super T>>
    extends HeteroPair<T,T> {

  public static <T extends Comparable<? super T>> HomoPair<T> makeHomoPair(
      T first, T second) {
    return new HomoPair<T>(first, second);
  }
  
  public static <T extends Comparable<? super T>> HomoPair<T> fromHeteroPair(HeteroPair<T,T> o) {
    return new HomoPair<T>(o.getFirst(),o.getSecond());
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
    HomoPair<T> other = (HomoPair<T>) obj;
    return this.compareTo(other) == 0;
  }

  private HomoPair(T p_x, T p_y) {
   super(p_x,p_y);
  }


}
