package name.NicolasCoutureGrenier.Music.PCS12ListPrinters;

import com.google.common.base.Function;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.FuzzyLogic.Valuators.CombinationDispersion;
import name.NicolasCoutureGrenier.Music.PCS12;

public class FunnyCSVPrinter implements Function<List<PCS12>, Void> {
  PrintWriter o;
  CombinationDispersion cd = new CombinationDispersion();

  
  public FunnyCSVPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  @SuppressWarnings("null")
  @Override
  public Void apply(List<PCS12> input) {
    TreeSet<Integer> I = new TreeSet<Integer>();
    PCS12 c = implicitChord(input);

    PCS12 combined = PCS12.identify(new TreeSet<Integer>());
    for (PCS12 ch : input) {
      combined = combined.combineWith(ch);
    }
    ArrayList<PCS12> input2 = new ArrayList<PCS12>(); input2.addAll(input);
    Collections.sort(input2);
    boolean same = true;
    for(int i=0;i<input.size();i++){
      if(!input.get(i).equals(input2.get(i))){same=false;break;}}
    if(!same){return null;}
    
    // maj is 07-43
    // jazz is 06-56
    // harmin is 07-38
    if(!(combined.getK() == 7 && combined.getOrder() == 38)){return null;}
    
    {
      TreeSet<PCS12> tmp = new TreeSet<PCS12>();
      tmp.addAll(input);
      if(tmp.size()!=input.size()) {return null;}
    }
    
    double disp_prod = 1.0;
    double succ_disp_sum = 0.0;
    double global_mean = 0;
    int sz = input.size();
    int i = 0;
    o.printf("\"");
    for (PCS12 ch : input) {
      global_mean += ch.getMean();
      disp_prod *= cd.apply(ch.getCombinationCopy()).getValue();
      o.printf(ch.toString());
      I.add(ch.getOrder());
      if (++i != sz) {
        o.printf(" ");
      }
    }
    o.printf("\"");

    global_mean /= sz;
    double dist = 0.0;
    Double last = null;

    for (PCS12 ch : input) {
      double cdv = cd.apply(ch.getCombinationCopy()).getValue();
      if (last != null) {
        succ_disp_sum += Math.abs(last - cdv);
      }
      last = cdv;
      dist += Math.abs(ch.getMean() - global_mean);
    }
    int nb_distinct = combined.getK();
    succ_disp_sum += Math.abs(last - cd.apply(input.get(0).getCombinationCopy()).getValue());

    dist /= sz;

    o.printf(",%d,", sz);

    Iterator<Integer> j = I.iterator();
    o.printf("\"");
    while (j.hasNext()) {
      o.printf("%d", (j.next()));
      if (j.hasNext()) {
        o.printf(" ");
      }
    }
    o.printf("\"");
    o.printf(",%d", I.size());
    o.printf(",\"" + c.toString() + "\"");
    o.printf("," + Integer.toString(nb_distinct));
    o.printf("," + Double.toString(global_mean / 12));
    o.printf("," + Double.toString(dist / 12));
    o.printf("," + Double.toString(disp_prod));
    o.printf("," + Double.toString(succ_disp_sum) + "\n");
    o.flush();

    return null;
  }

  private PCS12 implicitChord(List<PCS12> input) {

    PCS12 x = PCS12.identify(new TreeSet<Integer>());
    for (int i = 0; i < input.size(); i++) {
      x = x.combineWith(input.get(i).symmetricDifference(input.get((i + 1) % input.size())));
    }
    return x;

  }
}
