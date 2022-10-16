/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package name.NicolasCoutureGrenier.Music;

import java.util.LinkedList;

import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Maths.Graphs.GraphUtils;


public class R16ListFactory {

  DiGraph<Rhythm16> d;

  public R16ListFactory(DiGraph<Rhythm16> p_d) {
    d = p_d;

  }

  public R16List genRnd(int l, int iph) {

    LinkedList<Rhythm16> o = new LinkedList<Rhythm16>();

    boolean done = false;
    if (l == 1) {
      Rhythm16 tmp;
      do {
        tmp = GraphUtils.getRandomNode(d);
      } while (!tmp.getPhase().equals(iph));
      o.add(tmp);
      done = true;
    }

    while (!done) {
      if (o.contains(null)) {
        o.clear();
      }
      if (o.size() == 0) {
        Rhythm16 tmp;
        do {
          tmp = GraphUtils.getRandomNode(d);
        } while (!tmp.getPhase().equals(iph));
        o.add(tmp);
      } else if (o.size() < l) {
        o.addLast(GraphUtils.getRandomNode(d, o.get(o.size() - 1)));
      } else {
        if (d.isPredecessor(o.get(o.size() - 1), o.get(0))) {
          if (o.get(0).getPhase().equals(iph)) {
            done = true;
          } else {
            boolean fp = false;
            for (int i = 0; i < o.size(); i++) {
              o.addFirst(o.removeLast());
              if (o.get(0).getPhase().equals(iph)) {
                fp = true;
                break;
              }
            }
            if (fp) {
              done = true;
            } else {
              o.removeFirst();
            }

          }
        } else {
          o.clear();
        }
      }
    }
    return new R16List(o);
  }


}
