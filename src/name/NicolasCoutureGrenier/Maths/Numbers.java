package name.NicolasCoutureGrenier.Maths;

import java.util.Iterator;
import java.util.TreeMap;
// import static com.google.common.math.IntMath.checkedPow;
import java.util.TreeSet;

import name.NicolasCoutureGrenier.Maths.DataStructures.Matrix;


public class Numbers {

  public static boolean divides(int k, int n) {
    return n%k==0;
  }
  
  public static TreeSet<Integer> factors(int n) {
    if(n < 1) throw new RuntimeException("factors:: invalid n");
    TreeSet<Integer> o = new TreeSet<Integer>();
    o.add(1);
    o.add(n);
    int u = (int) Math.floor(Math.sqrt(n));
    
    for(int i=2; i<=u;i++) {
      if(divides(i,n)) { o.add(i); o.add(n/i);}
    }
    return o;
  }
  
  public static boolean isPowerOfTwo(int n) {
    /*
    if(n==0) return false;
    if(n < 0) n*= -1;
    
    int pOf2 = 1;
    while(pOf2 < n) pOf2 *= 2;
    return pOf2 == n;
    */
    return ((int)Math.round(Math.pow(2.0, Math.round(Math.log(n)/Math.log(2.0)))) == n);
  }
  
  public static int minDistMod12(int a, int b) {
    int d1 = a-b;
    if(d1 < 0) d1 += 12;
    int d2 = b-a;
    if(d2 < 0) d2 += 12;
    return Math.min(d1, d2);
  }
  
  public static int correctMod(int a, int b) {
    if(b < 0) throw new RuntimeException("Number.CorrectMod: invalid parameters.");
    if(a >= 0) return a%b;
    
    int a0 = a;
    while(a0 < 0) a0 += b;
    return a0 % b;
  }
  
  public static boolean prime(int n0) {
    int n = Math.abs(n0);
    if (n < 2) {
      return false;
    }
    int s = (int) Math.floor(Math.sqrt(n));
    for (int i = 2; i <= s; i++) {
      if (n % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static Matrix<Integer> primeFactorization(int n0) {
    TreeMap<Integer, Integer> tm = new TreeMap<Integer, Integer>();
    int t = Math.abs(n0);
    if (t < 2) {
      throw new RuntimeException("primeFactorization: |n| < 2");
    }
    int p = 2;

    while (t != 1) {
      if (t % p == 0) {
        if (!tm.containsKey(p)) {
          tm.put(p, 0);
        }
        tm.put(p, tm.get(p) + 1);
        t = t / p;
      } else {
        do {
          p++;
        } while (!prime(p));
      }
    }

    int ps = tm.keySet().size();
    Matrix<Integer> m = new Matrix<Integer>(2, ps);

    Iterator<Integer> it = tm.keySet().iterator();
    int c = 0;
    while (it.hasNext()) {
      int k = it.next();
      m.set(0, c, k);
      m.set(1, c, tm.get(k));
      c++;
    }
    return m;
  }

  public static int totient(int n) {
    Matrix<Integer> m = primeFactorization(n);
    double d = (double) n;
    int k = m.columnCount();

    for (int i = 0; i < k; i++) {
      d = d * (1 - (1 / ((double) m.get(0, i))));
    }

    return (int) Math.round(d);

  }

  public static int gcd(int a0, int b0) {
    int a = a0;
    int b = b0;
    int t = 0;
    while (b != 0) {
      t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

  public static int lcm(int a, int b) {
    return (a * b) / gcd(a, b);
  }

  public static int binomial(int n, int k) {
    long num = n;
    long den = 1;
    for (int i = 2; i <= k; i++) {
      den *= i;
      num *= (n - i + 1);
    }

    return (int) (num / den);
  }

  /**
   * Multinomial coefficient. The number of objects is assumed to be the sum of the int array.
   * 
   * @param n
   * @return
   */
  public static long multinomial(int[] n) {
    if (n == null) {
      throw new IllegalArgumentException();
    }
    int sum = 0;
    for (int i = 0; i < n.length; i++) {
      if (n[i] < 0) {
        throw new IllegalArgumentException();
      }
      sum += n[i];
    }

    long nf = factorial(sum);
    for (int i = 0; i < n.length; i++) {
      nf = nf / factorial(n[i]);
    }

    return (int) nf;
  }

  /**
   * Factorial function.
   * 
   * @param n
   * @return n!
   */
  public static long factorial(int n) {
    if (n < 0) {
      throw new IllegalArgumentException();
    }
    long o = 1;
    for (int i = 2; i <= n; i++) {
      o *= i;
    }
    return o;
  }
  
  public static int reverseTriangularNumber(int n) {
    return Double.valueOf(Math.floor((Math.sqrt((double)(1+8*n))-1.0)/2.0)).intValue();
  }
}
