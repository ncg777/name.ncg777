import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;

public class PrintRhythm16Graph {

  public static void main(String[] args) throws FileNotFoundException {
    ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
    ArrayList<Rhythm16> r = new ArrayList<Rhythm16>();
    TreeSet<Rhythm16> t = Rhythm16.getRhythms16();
    CollectionUtils.filter(t, sci);
    r.addAll(t);
    
    int sz = r.size();
    PrintWriter pw = new PrintWriter("d:/r16_graph.csv");
    for(int i=0;i<sz-1;i++){
      for(int j=i+1;j<sz;j++){
        Rhythm16 or = Rhythm16.or(r.get(i), r.get(j));
        Rhythm16 and = Rhythm16.and(r.get(i), r.get(j));
        
        if( sci.apply(or) && 
            (sci.apply(and) || and.isEmpty() )
            
            ){
          pw.println(String.format("%s %s %s %s", 
            r.get(i).getId(),
            r.get(j).getId(),
            Rhythm16.or(r.get(i), r.get(j)).getId(),
            Rhythm16.and(r.get(i), r.get(j)).getId()));
        }
      }
    }
    pw.close();

  }

}
