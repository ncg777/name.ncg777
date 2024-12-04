package name.ncg777.maths.enumerations;

import java.util.Enumeration;

import name.ncg777.maths.objects.Sequence;

/**
 * 
 * @link http://en.wikipedia.org/wiki/Weak_order
 * @link http://oeis.org/A000670
 * 
 * @author Nicolas Couture-Grenier
 * 
 */
public class WeakOrderEnumeration implements Enumeration<int[]> {
  private CompositionEnumeration ce;
  private WordPermutationEnumeration me;
  private int[] current_base;
  private int[] zerocase = {};
  public WeakOrderEnumeration(int n) {
    super();
    if(n>0) {
      ce = new CompositionEnumeration(n);
      nextBase();
      me = new WordPermutationEnumeration(current_base);
    }
  }

  private void nextBase() {
    Sequence s = ce.nextElement().asSequence();
    current_base = new int[s.size()];
    for (int i = 0; i < s.size(); i++) {
      current_base[i] = s.get(i);
    }
  }

  @Override
  public boolean hasMoreElements() {
    if(ce == null) {
      return zerocase!=null;
    }
    return ce.hasMoreElements() || me.hasMoreElements();
  }

  @Override
  public int[] nextElement() {
    if(ce == null) {
      if(zerocase == null) throw new RuntimeException("No such element.");
      zerocase=null;
      return new int[0];
    }
    if (!me.hasMoreElements()) {
      nextBase();
      me = new WordPermutationEnumeration(current_base);
    }

    return me.nextElement();
  }
}