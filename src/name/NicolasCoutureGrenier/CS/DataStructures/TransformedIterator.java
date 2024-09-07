package name.NicolasCoutureGrenier.CS.DataStructures;

import java.util.Iterator;
import java.util.function.Function;

public class TransformedIterator<T, E>  implements Iterator<E> {
  private Function<T,E> f;
  private Iterator<T> iter;
  public TransformedIterator(Iterator<T> iter, Function<T,E> f){
    this.iter = iter;
    this.f = f;}
  @Override
  public boolean hasNext() {
    return iter.hasNext();
  }
  @Override
  public E next() {
    return f.apply(iter.next());
  }
}
