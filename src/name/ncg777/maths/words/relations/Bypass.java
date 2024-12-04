package name.ncg777.maths.words.relations;

import name.ncg777.maths.objects.WordBinary;
import name.ncg777.maths.relations.Relation;


public class Bypass implements 
Relation<WordBinary, WordBinary>   {
  
  @Override
  public boolean apply(WordBinary a, WordBinary b) {
    return true;
  }
}
