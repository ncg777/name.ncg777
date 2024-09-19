package name.NicolasCoutureGrenier.Maths.Enumerations;
import java.util.Enumeration;

public class NGoodPathEnumeration implements Enumeration<int[]> {
  
    DyckWordEnumeration dw;

    public NGoodPathEnumeration(int n) {
        dw = new DyckWordEnumeration(n);
    }

    @Override
    public boolean hasMoreElements() {
      return dw.hasMoreElements();
    }

    @Override
    public int[] nextElement() {
      return convertDyckWordToPath(dw.nextElement());
    }

    
    private static int[] convertDyckWordToPath(String s) {
      int x=0;
      int y=0;
      int n = s.length()/2;
      
      int[] o = new int[n];
      
      for(int i=0;i<s.length();i++) {
        if(s.charAt(i) == '(') {
          o[x++] = y;
        } else {
          y++;
        }
      }
      return o;
      
    }
}