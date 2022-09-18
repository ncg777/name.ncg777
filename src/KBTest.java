import aima.core.logic.fol.domain.FOLDomain;
import aima.core.logic.fol.kb.FOLKnowledgeBase;

public class KBTest {
  
  public static void main(String[] args) {
    FOLDomain fd = new FOLDomain();
    fd.addPredicate("P");
    fd.addFunction("F");
    fd.addFunction("G");
    fd.addFunction("H");
    
    fd.addConstant("A");
    fd.addConstant("B");
    fd.addConstant("C");
    FOLKnowledgeBase kb = new FOLKnowledgeBase(fd);
    
    kb.tell("F(A) = G(B,H(C))");
    
    System.out.println(kb.ask("G(x,H(y)) = F(A)").getProofs());
  }

}
