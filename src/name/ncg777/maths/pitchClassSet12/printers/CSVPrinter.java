package name.ncg777.maths.pitchClassSet12.printers;

import com.google.common.base.Function;

import name.ncg777.maths.pitchClassSet12.PitchClassSet12;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class CSVPrinter implements Function<List<PitchClassSet12>, Void> {
  PrintWriter o;

  public CSVPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  @Override
  public Void apply(List<PitchClassSet12> input) {
    TreeSet<String> I = new TreeSet<String>();

    PitchClassSet12 combined = PitchClassSet12.identify(new TreeSet<Integer>());
    for (PitchClassSet12 ch : input) {
      combined = combined.combineWith(ch);
    }
    ArrayList<PitchClassSet12> input2 = new ArrayList<PitchClassSet12>(); input2.addAll(input);
    Collections.sort(input2);
    boolean same = true;
    for(int i=0;i<input.size();i++){
      if(!input.get(i).equals(input2.get(i))){same=false;break;}}
    if(!same){return null;}

    {
      TreeSet<PitchClassSet12> tmp = new TreeSet<PitchClassSet12>();
      tmp.addAll(input);
      if(tmp.size()!=input.size()) {return null;}
    }
    
    
    int sz = input.size();
    int i = 0;
    o.printf("\"");
    for (PitchClassSet12 ch : input) {
      
      o.printf(ch.toString());
      I.add(String.format("%02d",ch.getK()) + "-" + String.format("%02d",ch.getOrder()));
      if (++i != sz) {
        o.printf(" ");
      }
    }
    o.printf("\"");

    int nb_distinct = combined.getK();

    o.printf(",%d,", sz);

    Iterator<String> j = I.iterator();
    o.printf("\"");
    while (j.hasNext()) {
      o.printf("%s", (j.next()));
      if (j.hasNext()) {
        o.printf(" ");
      }
    }
    o.printf("\"");
    o.printf(",%d", I.size());
    o.printf("," + Integer.toString(nb_distinct));
    o.printf("\n");
    o.flush();

    return null;
  }
}
