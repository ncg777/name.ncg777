package name.NicolasCoutureGrenier.Music.SequencePredicates;

import name.NicolasCoutureGrenier.CS.Functional.StandardAndGuavaPredicate;
import name.NicolasCoutureGrenier.Maths.Objects.Sequence;

import static name.NicolasCoutureGrenier.CS.DataStructures.CollectionUtils.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

public class SeqSpecHasNoGaps implements StandardAndGuavaPredicate<Integer[]> {

  @Override
  public boolean apply(@SuppressWarnings("null") Integer[] input) {

    TreeMap<Integer, Sequence> t = calcIntervalVector(input);
    Iterator<Integer> j = t.keySet().iterator();

    while (j.hasNext()) {
      Sequence s = t.get(j.next());

      LinkedList<Integer> l = new LinkedList<Integer>();


      for (int i = 0; i < s.size(); i++) {
        if (s.get(i) != 0) {
          l.add(i);
        }
      }

      if (l.size() == 1) {
        continue;
      }

      for (int i = 0; i < l.size() - 1; i++) {
        if ((l.get(i + 1) - l.get(i)) != 1) {
          return false;
        }
      }

    }


    return true;

  }

}
