package name.ncg.Music;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg.Maths.Composition;
import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Interval;
import name.ncg.Maths.DataStructures.Matrix;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Enumerations.CombinationEnumeration;
import name.ncg.Maths.Enumerations.CompositionEnumeration;
import name.ncg.Maths.Graphs.CircuitFinder;
import name.ncg.Maths.Graphs.DiGraph;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.PCS12ListPrinters.CSVPrinter;
import name.ncg.Music.PCS12Predicates.Consonant;
import name.ncg.Music.PCS12Predicates.SizeIs;
import name.ncg.Music.PCS12Predicates.SubsetOf;
import name.ncg.Music.PCS12Relations.CloseIVs;
import name.ncg.Music.PCS12Relations.CommonNotesAtLeast;
import name.ncg.Music.PCS12Relations.Different;
import name.ncg.Music.PCS12Relations.IVEQRotOrRev;
import name.ncg.Music.PCS12Relations.PredicatedDifferences;
import name.ncg.Music.PCS12Relations.PredicatedSymmetricDifference;
import name.ncg.Music.PCS12Relations.PredicatedUnion;
import name.ncg.Music.RhythmPredicates.IsInSet;
import name.ncg.Music.RhythmPredicates.MaximizeQuality;
import name.ncg.Music.RhythmPredicates.ScaleModuloIsomorphic;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.ncg.Music.RhythmRelations.PredicatedIntersection;
import name.ncg.Music.RhythmRelations.PredicatedJuxtaposition;

import name.ncg.Music.RhythmRelations.PredicatedXOR;
import name.ncg.Music.RhythmRelations.SpectrumProximity;

public class Main {

  static public void main(String[] args) {

    try {
//       System.out.println("Started...");
 
//       TreeSet<Rhythm16> t = Rhythm16.getRhythms16();
//       PrintWriter p = new PrintWriter("d:/sci.csv"); 
//       
//       CollectionUtils.filter(t, new ShadowContourIsomorphic());
//       
//       for(Rhythm16 r : t) {
//           p.println(String.format("%02d-%03d.%02d", r.getK(), r.getOrder(), r.getPhase())+ "\t" + r.toString());  
//       }
//         
//       p.close();
       
//       
//       
//       Double mins[] = new Double[32];
//       Double maxs[] = new Double[32];
//       
//       for(int i=0;i<32;i++) {
//         mins[i] = Double.MAX_VALUE;
//         maxs[i] = Double.MIN_VALUE;
//       }
//       
//       int counter = 0;
//       CompositionEnumeration ce = new CompositionEnumeration(32);
//       while(ce.hasMoreElements()) {
//         Sequence comp = ce.nextElement().asSequence();
//         double e = comp.entropy();
//         if(e<mins[comp.size()-1]) mins[comp.size()-1] = e;
//         if(e>maxs[comp.size()-1]) maxs[comp.size()-1] = e;
//         
//         System.out.println("" + String.format("%.04f", 100.0 * ((double)++counter / 2147483648.0)) + "%");
//       }
//       
//       PrintWriter p = new PrintWriter("d:/minmaxentropy32.txt");
//       p.println("k, min, max");
//       for(int i=0;i<32;i++) {
//         p.println("" + i+1 + ", " +  mins[i] + ", " + maxs[i]);
//       }
//       
//       p.flush();
//       p.close();
      
      TreeSet<PCS12> t0 = PCS12.getChords();
      TreeSet<PCS12> t = new TreeSet<PCS12>();
      PCS12 scale = PCS12.parse("07-43.11");
      Predicate<PCS12> pred = new SubsetOf(scale);
      
      for(PCS12 r : t0){
        if(pred.apply(r) && r.getK() == 4) {
          t.add(r);
        }
      }
      
     
      Relation<PCS12, PCS12> rel_horiz = Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1)));
      
      DiGraph<PCS12> d = new DiGraph<>(t, rel_horiz);
      PrintWriter p = new PrintWriter("d:/horiz.txt");
      for(PCS12 u : d.getVertices()) {
        for(PCS12 v : d.getNeighbors(u)) {
          p.println("" + u.toString() + ", " +  v.toString());
        }
      }
      
      p.flush();
      p.close();
      
      } catch (Exception e) {
        e.printStackTrace();
    }


  }

}
