package name.ncg777.maths.phrases;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import name.ncg777.maths.words.BinaryWord;

public class BinaryPhrase extends ArrayList<BinaryWord>  implements Comparable<BinaryPhrase>{
  private static final long serialVersionUID = 1L;

  public BinaryPhrase(List<BinaryWord> m_l) {
    super();
    for (BinaryWord i : m_l) {
      this.add(i);
    }
  }

  public BinaryPhrase() {
    super();
  }
  
  public BinaryWord asBinary(){
    int n = size()*12;
    BitSet b = new BitSet(n);
    for(int i=0;i<n;i++){
      int s = i / 12;
      try{
        b.set(i, this.get(s).get(i%12));
      }catch(Exception e){
        System.out.println("???");
      }
    }
    return new BinaryWord(b,n);
  }
  static public BinaryPhrase parse(String str) {
    String[] strs = str.split("\\s+");
    
    LinkedList<BinaryWord> output = new LinkedList<BinaryWord>();
    for (var s : strs) {
      output.add(BinaryWord.build(s));
    }
    return new BinaryPhrase(output);

  }

  
  @Override
  public String toString() {
    String t = "";
    int n = this.size();

    for (int i = 0; i < n; i++) {
      t += this.get(i).toString();
      if (i != (n - 1)) {
        t += " ";
      }
    }
    return t;
  }
  

  
  public Integer getNumberOfCharacters() {
    int m = 0;
    for (int x = 0; x < this.size(); x++) {
      m += this.get(x).getN();
    }
    return m;
  }

  public static Boolean[] toBooleanArray(BinaryPhrase a) {

    Boolean output[] = new Boolean[a.size() * 2];
    for (int i = 0; i < a.size() * 2; i++) {
      output[i] = false;
    }

    for (int i = 0; i < a.size(); i++) {
      BinaryWord x = a.get(i);

      for (int j = x.nextSetBit(0); j >= 0; j = x.nextSetBit(j + 1)) {
        output[j + (i * 2)] = true;
      }
    }
    return output;
  }

  public static BinaryPhrase juxtapose(BinaryPhrase a, BinaryPhrase b) {
    BinaryPhrase output = new BinaryPhrase();

    for (int i = 0; i < a.size(); i++) {
      output.add(a.get(i));
    }
    for (int i = 0; i < b.size(); i++) {
      output.add(b.get(i));
    }
    return output;
  }

  public void append(BinaryPhrase a) {
    for (int i = 0; i < a.size(); i++) {
      this.add(a.get(i));
    }
  }

  @Override
  public int compareTo(BinaryPhrase o) {
    return o.toString().compareTo(this.toString());
  }
}
