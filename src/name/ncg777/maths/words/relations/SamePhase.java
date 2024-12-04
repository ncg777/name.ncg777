package name.ncg777.maths.words.relations;


import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class SamePhase implements 
Relation<WordBinary, WordBinary>   {
 
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    int na = a.nextSetBit(0);
    int nb = b.nextSetBit(0);
    
    if(na == -1 || nb == -1) return false;
    
    return na == nb;
  }
}