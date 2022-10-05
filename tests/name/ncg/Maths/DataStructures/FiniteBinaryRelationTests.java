package name.ncg.Maths.DataStructures;

import java.util.TreeSet;

import junit.framework.TestCase;
import name.ncg.Maths.DataStructures.FiniteBinaryRelation;
import name.ncg.Maths.DataStructures.HeterogeneousPair;

public class FiniteBinaryRelationTests extends TestCase {
  FiniteBinaryRelation<String,String> r;
  FiniteBinaryRelation<String,String> s;
  FiniteBinaryRelation<String,String> rr_R;
  FiniteBinaryRelation<String,String> rr_S;
  FiniteBinaryRelation<String,String> lr_R;
  FiniteBinaryRelation<String,String> lr_S;
  
  public FiniteBinaryRelationTests(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    r = new FiniteBinaryRelation<String,String>();
    r.add("a","b");
    r.add("a","c");
    r.add("b","c");
    r.add("d","e");
    r.add("d","f");
    
    s = new FiniteBinaryRelation<String,String>();
    s.add("b","c");
    s.add("b","d");
    s.add("c","d");
    s.add("e","g");
    s.add("f","g");
    
    rr_R = new FiniteBinaryRelation<String,String>();
    rr_R.add("p1","p3");
    rr_R.add("p2","p3");
    rr_S = new FiniteBinaryRelation<String,String>();
    rr_S.add("p1","o");
    rr_S.add("p2","o");
    lr_S = new FiniteBinaryRelation<String,String>();
    lr_S.add("o","p1");
    lr_S.add("o","p2");
    lr_R = new FiniteBinaryRelation<String,String>();
    lr_R.add("p3","p1");
    lr_R.add("p3","p2");

  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public final void testCompose() {

    FiniteBinaryRelation<String,String> o = r.compose(s);

    assertTrue(o.contains(HeterogeneousPair.makeHeterogeneousPair("a", "c")));
    assertTrue(o.contains(HeterogeneousPair.makeHeterogeneousPair("a", "c")));
    assertTrue(o.contains(HeterogeneousPair.makeHeterogeneousPair("b", "d")));
    assertTrue(o.contains(HeterogeneousPair.makeHeterogeneousPair("d", "g")));
    assertTrue(o.size()==4);
  }

  public final void testRight_residual() {
    FiniteBinaryRelation<String,String> e = rr_R.rightResidual(rr_S);
    assertTrue(e.contains(HeterogeneousPair.makeHeterogeneousPair("p3", "o")));
    assertTrue(e.size()==1);
    
  }

  public final void testLeft_residual() {
    FiniteBinaryRelation<String,String> e = lr_S.leftResidual(lr_R);
    
    assertTrue(e.contains(HeterogeneousPair.makeHeterogeneousPair("o", "p3")));
    assertTrue(e.size()==1);
  }

  public final void testDomain() {
    TreeSet<String> d = r.domain();
    assertTrue(d.size()==3);
    d.contains("a");
    d.contains("b");
    d.contains("d");
  }

  public final void testCodomain() {
    TreeSet<String> c = r.codomain();
    assertTrue(c.size()==4);
    c.contains("b");
    c.contains("c");
    c.contains("e");
    c.contains("f");
  }

}
