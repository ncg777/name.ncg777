package name.ncg.Music.RhythmPredicates;

import com.google.common.base.Predicate;

import static name.ncg.Maths.DataStructures.CollectionUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import name.ncg.Music.Rhythm;

public class CoprimeIntervals implements Predicate<Rhythm> {

  @Override
  public boolean apply(Rhythm input) {
    int n = input.getN();
    if (input.getK() <= 1) {
      return false;
    }
    if (input.getK() == n) {
      return true;
    }
    Integer[] l = new Integer[input.getK() + 1];

    int j = 0;
    for (int i = input.nextSetBit(0); i >= 0; i = input.nextSetBit(i + 1)) {
      l[j++] = i;
    }
    for (int i = j - 1; i >= 0; i--) {
      l[i] = l[i] - l[0];
    }
    l[j] = n;

    Integer[] d = difference(l);

    TreeSet<Integer> t = new TreeSet<Integer>();
    t.addAll(Arrays.asList(d));

    if (t.size() == 1) {
      return false;
    }
    Integer[] s = new Integer[t.size()];
    j = 0;
    Iterator<Integer> it = t.iterator();
    while (it.hasNext()) {
      s[j] = it.next();
      j++;
    }

    int gcd = greatestCommonDivisor(s[0], s[1]);

    if (s.length > 2) {
      for (int y = 2; y < s.length; y++) {
        gcd = greatestCommonDivisor(gcd, s[y]);
      }
    }

    return gcd == 1;
  }

  private static int greatestCommonDivisor(int m, int n) {
    int x;
    int y;
    while (m % n != 0) {
      x = n;
      y = m % n;
      m = x;
      n = y;
    }
    return n;
  }

}
