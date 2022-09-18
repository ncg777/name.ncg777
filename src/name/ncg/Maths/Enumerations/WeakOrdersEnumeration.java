package name.ncg.Maths.Enumerations;

import java.util.Enumeration;

import name.ncg.Maths.DataStructures.Sequence;

/**
 * 
 * @link http://en.wikipedia.org/wiki/Weak_order
 * @link http://oeis.org/A000670
 * 
 * @author Nicolas Couture-Grenier
 * 
 */
public class WeakOrdersEnumeration implements Enumeration<int[]> {
  private CompositionEnumeration ce;
  private WordPermutationEnumeration me;
  private int[] current_base;

  public WeakOrdersEnumeration(int n) {
    super();
    ce = new CompositionEnumeration(n);
    nextBase();
    me = new WordPermutationEnumeration(current_base);
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
    return ce.hasMoreElements() || me.hasMoreElements();
  }

  @Override
  public int[] nextElement() {
    if (!me.hasMoreElements()) {
      nextBase();
      me = new WordPermutationEnumeration(current_base);
    }

    return me.nextElement();
  }
}
