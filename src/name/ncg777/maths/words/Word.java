package name.ncg777.maths.words;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import name.ncg777.maths.sequences.Sequence;

public class Word extends ArrayList<Character> implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  public Word() {
    super();
  }
  
  public Word(Character[] array) {
    super();
    for(var c : array) this.add(c);
  }
  
  public Word(List<Character> list) {
    super();
    for(var c : list) this.add(c);
  }
  public Word(String string) {
    super();
    if(string.length() != 2) throw new IllegalArgumentException();
    for(int i=0; i<string.length();i++) this.add(string.charAt(i));
  }
  
  public Word(Sequence sequence, Alphabet alphabet) {
    super();
    if(!sequence.isNatural() || sequence.getMax() >= alphabet.size()) throw new IllegalArgumentException();
    for(var i : sequence) this.add(alphabet.get(i));
  }
  
  public Sequence toSequence(Alphabet alphabet) {
    var o = new Sequence();
    for(int i=0;i<this.size();i++) {o.add(alphabet.indexOf(this.get(i)));}
    return o;
  }
    
  public static Word agglutinate(Word first, Word second) {
    var o = new Word(first);
    o.addAll(second);
    return o;
  }

  public static Word rotate(Word r, int t) {
    var o = new Word(r);
    if(t<0) {
      do {
        o.add(o.remove(0));
      } while(++t < 0);
    }
    if(t>0) {
      do {
        o.add(0,o.removeLast());
      } while(--t > 0);
    }
    return o;
  }

  @Override
  public boolean equals(Object _other) {
    if(!(_other instanceof Word)) return false;
    var other = (Word) _other;
    if(this.size() != other.size()) return false;
    for(int i=0;i<this.size();i++) if(this.get(i) != other.get(i)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for(var c : this) sb.append(c);
    return sb.toString();
  }

  public static boolean equivalentUnderRotation(Word a, Word b) {
    if(a.size()!=b.size()) return false;
    for (int i = 0; i < a.size(); i++) {
      if (a.equals(rotate(b, i))) {
        return true;
      }
    }
    return false;
  }
}
