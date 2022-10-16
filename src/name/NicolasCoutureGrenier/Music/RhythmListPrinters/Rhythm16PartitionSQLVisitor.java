package name.NicolasCoutureGrenier.Music.RhythmListPrinters;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.BidiMap;

import name.NicolasCoutureGrenier.Maths.DataStructures.Matrix;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.Rhythm16;
import name.NicolasCoutureGrenier.Music.Rhythm16Partition;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

public class Rhythm16PartitionSQLVisitor implements Function<List<Rhythm16Partition>, Void> {
  private Connection c;
  private BidiMap<Long, Rhythm16Partition> map;
  public Rhythm16PartitionSQLVisitor(Connection c, 
                                     BidiMap<Long, Rhythm16Partition> map) {
    this.c = c;
    this.map = map;
  }
  public void close() throws SQLException {
    c.close();
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
    
    try {
      
      Long first = map.getKey(arg0.get(0));
      Long last = map.getKey(arg0.get(arg0.size()-1));
      String firstline = Joiner.on(" ").join(mat.getRow(0));
      String matrix = mat.toString();
      Statement s = c.createStatement();
      s.execute(
        "insert into r16loops(firstline, first, last, matrix, length, height,ks, phases) values(" +
        "'" + firstline + "', " + first + ", " + last + ", '" + matrix + "', " + 
            + arg0.size() + ", " + m + ", '" + Joiner.on(" ").join(ks) + "', '" + 
            Joiner.on(" ").join(phases)   + "');"
          );
      
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return null;
  }

}