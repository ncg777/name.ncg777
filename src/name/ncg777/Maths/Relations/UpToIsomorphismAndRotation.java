package name.ncg777.Maths.Relations;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.Maths.Objects.Sequence;

/**
 * This equivalence states that 2 sequences s1 and s2 are considered equal if there exist a
 * bijective mapping m and an integer r such that s2 = rotate(m(s1), r)
 * 
 * @author Nicolas Couture-Grenier
 * 
 */
public class UpToIsomorphismAndRotation extends Equivalence<Sequence> {

  @Override
  protected boolean doEquivalent(@Nonnull Sequence a, @Nonnull Sequence b) {
    if (a.size() != b.size() || a.distinct().size() != b.distinct().size()) {
      return false;
    }

    int n = a.size();

    for (int i = 0; i < n; i++) {
      if (a.existsMapTo(b.rotate(i))) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected int doHash(@Nonnull Sequence t) {
    return t.distinct().size() * 3 + 5 * t.size();
  }

}
