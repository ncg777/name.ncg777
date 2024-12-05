package name.ncg777.maths.words.relations;

import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.words.BinaryWord;


public class Bypass implements 
Relation<BinaryWord, BinaryWord>   {
  
  @Override
  public boolean apply(BinaryWord a, BinaryWord b) {
    return true;
  }
}
