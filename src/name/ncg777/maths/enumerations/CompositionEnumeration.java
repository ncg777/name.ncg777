package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.objects.Composition;

public class CompositionEnumeration implements Enumeration<Composition> {

  private BitSetEnumeration be;
  private int n;

  public CompositionEnumeration(int n) {
    if (n < 1) {
      throw new IllegalArgumentException();
    }
    be = new BitSetEnumeration(n - 1);
    this.n = n;
  }

  @Override
  public Composition nextElement() {
    return new Composition(be.nextElement(), n);
  }

  @Override
  public boolean hasMoreElements() {
    return be.hasMoreElements();
  }
}
