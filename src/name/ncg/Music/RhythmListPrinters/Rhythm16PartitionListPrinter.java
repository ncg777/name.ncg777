package name.ncg.Music.RhythmListPrinters;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import name.ncg.Maths.DataStructures.Matrix;
import name.ncg.Music.R16List;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.Rhythm16Partition;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

public class Rhythm16PartitionListPrinter implements Function<List<Rhythm16Partition>, Void> {
  PrintWriter o;

  public Rhythm16PartitionListPrinter(PrintWriter o) {
    this.o = o;
  }
  public void close() {
    o.close();
  }

  public void flush() {
    o.flush();
  }
  @Override
  public Void apply(List<Rhythm16Partition> arg0) {
    int m = -1;
    for(int i=0;i<arg0.size();i++){
      if(arg0.get(i).getRhythms().size() > m){
        m = arg0.get(i).getRhythms().size();
      }
    }
    int n = arg0.size();
    
    Matrix<Rhythm16> mat = new Matrix<Rhythm16>(m+1,n,Rhythm16.getZeroRhythm());
    List<Integer> ks = new ArrayList<Integer>(m+1);
    List<Integer> phases = new ArrayList<Integer>(m+1);
    for(int i=0;i<m+1;i++){ks.add(0);}
    
    for(int j=0;j<n;j++){
      Rhythm16Partition p = arg0.get(j);
      mat.set(0, j, p.getRhythm());
      ks.set(0, ks.get(0)+p.getRhythm().getK());
      for(int i=0;i<m;i++){
        if(i<p.getRhythms().size()){
          mat.set(i+1,j, p.getRhythms().get(i));
          ks.set(i+1, ks.get(i+1)+p.getRhythms().get(i).getK());
        } else{break;}
      }
      
    }
    for(int i=0;i<m+1;i++){
      R16List l = new R16List(mat.getRow(i));
      int k=0;
      int p=-1;
      for(int j=0;j<l.size();j++){
        Rhythm16 r = l.get(j);
        p = r.nextSetBit(0);
        if(p!=-1){p+=k*16; break;}
        k++;
      }
      phases.add(p);
    }
    String part = "";
    for(Rhythm16Partition r : arg0) {
      part += Joiner.on(" ").join(r.getPartition()) + " ";
    }
    
    o.println(mat.toString());
    o.println(part.trim());
    o.println("               length : "  + arg0.size());
    o.println("               height : " + m);
    o.println("cardinalities of rows : " + Joiner.on(" ").join(ks));
    o.println("       phases of rows : " + Joiner.on(" ").join(phases));
    o.println("===\n");
    o.flush();
    
    return null;
  }

}
