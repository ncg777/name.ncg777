package name.ncg.Music.SequencePredicates;

import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import name.ncg.Music.Rhythm;

public class MirrorRhythms implements Predicate<Integer[]> {

  @Override
  public boolean apply(Integer[] input) {
    TreeSet<Integer> t = new TreeSet<Integer>();
    t.addAll(Arrays.asList(input));
    if (t.size() < 2) {
      return false;
    }

    if (input.length % t.size() != 0) {
      return false;
    }

    Iterator<Integer> i = t.iterator();
    TreeMap<Integer, Rhythm> m = new TreeMap<Integer, Rhythm>();



    while (i.hasNext()) {
      int val = i.next();
      String r = "";
      for (int j = 0; j < input.length; j++) {
        if (input[j] == val) {
          r += "1";
        } else {
          r += "0";
        }
      }
      m.put(val, Rhythm.buildRhythm(r));
      if (m.get(val).getN() != input.length / t.size()) {
        return false;
      }
    }

    i = m.keySet().iterator();
    Rhythm o = m.get(i.next());

    while (i.hasNext()) {
      if (!Rhythm.equivalentUnderRotation(o, m.get(i.next()))) {
        return false;
      }
    }
    return true;

  }
}
