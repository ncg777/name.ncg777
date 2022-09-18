package name.ncg.Music;

import java.util.ArrayList;

public class PCS12Sequence extends ArrayList<PCS12> {

  private static final long serialVersionUID = 1L;

  public PCS12Sequence transpose(int n) {
    PCS12Sequence o = new PCS12Sequence();
    for (int i = 0; i < this.size(); i++) {
      o.add(this.get(i).transpose(n));
    }
    return o;
  }

}
