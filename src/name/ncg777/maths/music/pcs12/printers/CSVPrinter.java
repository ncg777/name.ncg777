package name.ncg777.maths.music.pcs12.printers;

import com.google.common.base.Function;

import name.ncg777.maths.music.pcs12.Pcs12;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class CSVPrinter implements Function<List<Pcs12>, Void> {
  PrintWriter o;

  public CSVPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  @Override
  public Void apply(List<Pcs12> input) {
    TreeSet<String> I = new TreeSet<String>();

    Pcs12 combined = Pcs12.identify(new TreeSet<Integer>());
    for (Pcs12 ch : input) {
      combined = combined.combineWith(ch);
    }
    ArrayList<Pcs12> input2 = new ArrayList<Pcs12>(); input2.addAll(input);
    Collections.sort(input2);
    boolean same = true;
    for(int i=0;i<input.size();i++){
      if(!input.get(i).equals(input2.get(i))){same=false;break;}}
    if(!same){return null;}

    {
      TreeSet<Pcs12> tmp = new TreeSet<Pcs12>();
      tmp.addAll(input);
      if(tmp.size()!=input.size()) {return null;}
    }
    
    
    int sz = input.size();
    int i = 0;
    o.printf("\"");
    for (Pcs12 ch : input) {
      
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
