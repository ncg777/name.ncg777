package name.NicolasCoutureGrenier.Maths.DataStructures;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class TreeNodeTests extends TestCase {
  private JaggedList<Integer> tn;
  
  public TreeNodeTests(String name) {
    super();
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void toStringTests() {
    String o = this.tn.toString();
    System.out.println(o);
    assertTrue(true);
  }
}