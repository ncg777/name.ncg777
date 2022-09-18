package name.ncg.Maths;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Joiner;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.DataStructures.Sequence.ArpType;
import name.ncg.Maths.Enumerations.BitSetEnumeration;
import name.ncg.Maths.Enumerations.CompositionEnumeration;
import name.ncg.Maths.Enumerations.FixedSetPartitionEnumeration;
import name.ncg.Maths.Enumerations.KPermutationEnumeration;
import name.ncg.Maths.Enumerations.MixedRadixEnumeration;
import name.ncg.Maths.Enumerations.OrderedPairEnumeration;
import name.ncg.Maths.Enumerations.PairEnumeration;
import name.ncg.Maths.Enumerations.SetPartitionEnumeration;
import name.ncg.Maths.Enumerations.WordEnumeration;
import name.ncg.Maths.Predicates.Contiguous;
import name.ncg.Maths.Predicates.ContiguousDistinctValuesExceptZero;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.RhythmPredicates.RelativelyFlat;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;

public class Main {
  

  static public void main(String[] args) throws FileNotFoundException {
    Double mins[] = new Double[12];
    Double maxs[] = new Double[12];
    
    for(int i=0;i<12;i++) {
      mins[i] = Double.MAX_VALUE;
      maxs[i] = Double.MIN_VALUE;
    }
    
    int counter = 0;
    CompositionEnumeration ce = new CompositionEnumeration(12);
    while(ce.hasMoreElements()) {
      Sequence comp = ce.nextElement().asSequence();
      double e = comp.entropy();
      if(e<mins[comp.size()-1]) mins[comp.size()-1] = e;
      if(e>maxs[comp.size()-1]) maxs[comp.size()-1] = e;
      
      System.out.println("" + String.format("%.04f", 100.0 * ((double)++counter / 2048.0)) + "%");
    }
    
    PrintWriter p = new PrintWriter("d:/minmaxentropy12.txt");
    p.println("k, min, max");
    for(int i=0;i<12;i++) {
      p.println("" + (i+1) + ", " +  mins[i] + ", " + maxs[i]);
    }
    
    p.flush();
    p.close();
    
    
//    Sequence s = new Sequence();
//    s.add(1); s.add(2); s.add(3);
//    Sequence t = new Sequence();
//    t.add(4); t.add(5); t.add(6);
//    
//    PairEnumeration<Integer> e = new PairEnumeration<>(s, t);
//    
//    while(e.hasMoreElements()) {
//      var p = e.nextElement();
//      System.out.println(p.getFirst() + ", " + p.getSecond());
//    }
    
    
    
//    PrintWriter printer = new PrintWriter("d:/arp.txt");
//    int K = 17;
//    printer.append("seq, k, len\n");
//    for(ArpType arpType : Sequence.ArpType.values() ) {
//      StringBuilder sb = new StringBuilder();
//      for(int i=2;i<=K;i++) {
//        Sequence s0 = Sequence.arp(arpType, i, false, false);
//        sb.append(s0.toString().replaceAll("[)(]", "")+ ", " + i + ", " + s0.size() + "\n");
//        Sequence s1 = Sequence.arp(arpType, i, false, true);
//        sb.append(s1.toString().replaceAll("[)(]", "")+ ", " + i + ", " + s1.size() + "\n");
//        Sequence s2 = Sequence.arp(arpType, i, true, false);
//        sb.append(s2.toString().replaceAll("[)(]", "")+ ", " + i + ", " + s2.size() + "\n");
//        Sequence s3 = Sequence.arp(arpType, i, true, true);
//        sb.append(s3.toString().replaceAll("[)(]", "")+ ", " + i + ", " + s3.size() + "\n");
//      }
//      printer.print(sb.toString());
//    }
//    printer.close();
    
//    Set<Integer> s = new TreeSet<Integer>();
//    s.add(0); s.add(2); s.add(8); s.add(10); s.add(11);s.add(12);s.add(14);s.add(15);
//    Combination c = new Combination(16,s);
//    Composition co = c.getComposition();
//    System.out.println(co.toString());
//    List<Composition> l = co.segment();
//    for(Composition x : l) {
//      System.out.println(x.toString());
//    }
    
      //System.out.println(Rhythm16.parseRhythm16Hex("55 5E").getComposition().partitionByEquality().toString());
    
//    int[][] x = {{1,0,1,0,0},{1,1,1,1,1},{0,1,0,1,1},{0,1,1,0,1},{1,0,1,1,0},{1,1,1,0,0}};
//    
//    Sequence[] s = new Sequence[6];
//    for(int i=0;i<6;i++){
//      s[i] = new Sequence(x[i]);
//    }
//    boolean f = false;
//    
//    while(true){
//      for(int i=0;i<5;i++){
//        int total1 = 0;
//        int total2 = 0;
//        for(int j=0;j<6;j++){
//          
//          int k =0;
//          for(int n=0;n<5;n++){
//            if(s[j].get(n) == 1){ k += Math.pow(2,n);}
//            
//          }
//          total1 += k*Math.pow(2, 5*j);
//          total2 += k*Math.pow(2, 5*(5-j));
//          System.out.print(Integer.toString(k) + " ");
//          
//          s[j] = s[j].rotate(1);
//          
//          
//        }
//        System.out.print(" : " + Integer.toString(total1) + " " + Integer.toString(total2) + " \n");
//      }
//      if(f){break;}
//      for(int i=0;i<6;i++){
//        s[i] = s[i].reverse();
//      }
//      f = true;
//    }
    
//    int[] factors = {2,2,2,2,2,2,2,3,3,3,5,5};
//    PrintWriter printer = new PrintWriter("d:/spe.txt");
//    SetPartitionEnumeration spe = new SetPartitionEnumeration(12);
//    while(spe.hasMoreElements()){
//      int[] s = spe.nextElement();
//      String string = "";
//      for(int i : s){string = string + factors[i] + " ";}
//      printer.println(string);
//    }
//    printer.flush();
    
  }
//    PrintWriter pr = new PrintWriter("d:/flatandsci.txt");
//    RelativelyFlat r = new RelativelyFlat(90);
//    ShadowContourIsomorphic sci = new ShadowContourIsomorphic();
//    BitSetEnumeration b = new BitSetEnumeration(16);
//    int lower = 2;
//    int upper = 4;
//    while(b.hasMoreElements()){
//      Rhythm16 rh = Rhythm16.identifyRhythm16(new Combination(b.nextElement(),16));
//      
//      if(r.apply(rh)){
//        boolean found = false;
//        TreeSet<Rhythm16> rhythms = new TreeSet<Rhythm16>();
//        for(int i=lower;i<=upper;i++){
//          if(i>rh.getK()){continue;}
//          FixedSetPartitionEnumeration fspe = new FixedSetPartitionEnumeration(rh.getK(), i);
//          
//          while(fspe.hasMoreElements()){
//            
//            Integer[] p = fspe.nextElement();
//            Rhythm16[] rp = new Rhythm16[i];
//            Combination[] c =rh.partition(p);
//            boolean ok = true;
//            for(int j=0;j<c.length;j++){
//              rp[j] = Rhythm16.identifyRhythm16(c[j]);
//              if(!sci.apply(rp[j])){
//                ok = false;
//                break;
//              }
//            }
//            if(ok){
//              if(!found){
//                pr.println("--->" + rh.toString());
//                
//                found = true;
//              }
//              String[] o = new String[i];
//              for(int j=0;j<rp.length;j++){
//                rhythms.add(rp[j]);
//                o[j] = rp[j].toString();
//              }
//              pr.println(Joiner.on(", ").join(o));
//            }
//          }
//        }
//        if(found){
//          pr.println("distinct : " + Joiner.on(", ").join(rhythms));
//        }
//        
//      }
//    }
//    pr.flush();
//    pr.close();
//  }

}
