package name.ncg777.Maths.Graphs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

public class CliqueFinder<T extends Comparable<? super T>> {
  Boolean[][] connected;
  LinkedList<Short> all;
  LinkedList<Short> compsub;
  TreeMap<Short, T> map;


  LinkedList<TreeSet<T>> output;

  public CliqueFinder(DiGraph<T> d) {

    Iterator<T> i = d.getVertices().iterator();
    all = new LinkedList<Short>();
    compsub = new LinkedList<Short>();

    map = new TreeMap<Short, T>();

    short x = 0;
    while (i.hasNext()) {
      all.add(x);
      map.put(x, i.next());
      x++;
    }


    connected = new Boolean[all.size()][all.size()];

    i = d.getVertices().iterator();
    Iterator<T> j;

    x = 0;
    short y;

    while (i.hasNext()) {
      y = 0;
      T z = i.next();
      j = d.getVertices().iterator();
      while (j.hasNext()) {
        connected[x][y] = d.isPredecessor(z, j.next());

        y++;
      }
      x++;
    }

  }

  public LinkedList<TreeSet<T>> findCliques() {

    output = new LinkedList<TreeSet<T>>();

    extend(all, (short) 0, (short) all.size());

    return output;

  }

  private void extend(LinkedList<Short> oldSet, Short ne, Short ce) {
    LinkedList<Short> newSet = new LinkedList<Short>();

    for (short k = 0; k < ce; k++) {
      newSet.add((short) 0);
    }

    Short nod;
    Short fixp;
    Short newne, newce, i, j, count, pos, p, s, sel, minnod;
    minnod = ce;
    nod = 0;
    i = 0;
    s = 0;
    fixp = 0;
    pos = 0;

    while (i < ce && minnod != 0) {
      p = oldSet.get(i);
      count = 0;
      j = ne;

      while (j < ce && count < minnod) {
        if (!connected[p][oldSet.get(j)]) {
          count++;
          pos = j;
        }
        j++;
      }

      if (count < minnod) {
        fixp = p;
        minnod = count;
        if (i < ne) {
          s = pos;
        } else {
          s = i;
          nod = 1;
        }
      }
      i++;
    }

    nod = (short) (minnod + nod);
    while (nod >= 1) {
      p = oldSet.get(s);
      oldSet.set(s, oldSet.get(ne));
      oldSet.set(ne, p);
      sel = p;
      i = 0;
      newne = 0;

      while (i < ne) {
        if (connected[sel][oldSet.get(i)]) {
          newSet.set(newne++, oldSet.get(i));
        }
        i++;
      }

      newce = (short) newne;
      i = (short) (ne + 1);

      while (i < ce) {
        if (connected[sel][oldSet.get(i)]) {
          newSet.set(newce++, oldSet.get(i));
        }
        i++;
      }

      compsub.add(sel);
      if (newce == 0) {

        TreeSet<T> o = new TreeSet<T>();

        Iterator<Short> z = compsub.iterator();
        while (z.hasNext()) {
          o.add(map.get(z.next()));
        }

        output.add(o);
      }

      else if (newne < newce) {
        extend(newSet, newne, newce);
      }

      compsub.removeLast();
      ne++;
      if (nod > 1) {
        s = ne;

        while (connected[fixp][oldSet.get(s)]) {
          s++;
        }

      }

      nod--;

    }
  }
}
