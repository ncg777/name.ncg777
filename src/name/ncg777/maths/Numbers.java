package name.ncg777.maths;

import java.util.Iterator;
import java.util.TreeMap;
import static com.google.common.math.IntMath.checkedPow;
import java.util.TreeSet;


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

  /**
   * Computes the q-factorial [n]_q! for given n and q using integer arithmetic.
   *
   * @param q The base q (must be positive).
   * @param n The integer n for the q-factorial.
   * 
   * @return The q-factorial [n]_q! as a long.
   * @throws ArithmeticException if the result overflows a long.
   */
  public static long qFactorial(int q, int n) {
      if (n < 0 || q <= 0) {
          throw new IllegalArgumentException("n must be non-negative and q must be positive.");
      }
      
      // Special case for q = 1: q-factorial becomes n!
      if (q == 1) {
          return factorial(n);
      }
      long qFactorial = 1; // Initialize q-factorial as 1

      for (int i = 1; i <= n; i++) {
          // Compute [i]_q = (q^i - 1) / (q - 1) using integer arithmetic
          long qPowerI = checkedPow(q, i); // q^i
          long numerator = qPowerI - 1; // q^i - 1
          long denominator = q - 1; // q - 1
          long qInteger = numerator / denominator; // [i]_q

          // Check for overflow
          if (qFactorial > Long.MAX_VALUE / qInteger) {
              throw new ArithmeticException("q-factorial overflows a long for n = " + n + ", q = " + q);
          }

          // Multiply the current q-integer to the factorial
          qFactorial *= qInteger;
      }

      return qFactorial;
  }
  
  /**
   * Computes the q-binomial (Gaussian binomial coefficient) for given n, k, and q.
   * 
   * @param q The base q (must be positive).
   * @param n The total number of items.
   * @param k The number of items to choose.
   * 
   * @return The Gaussian binomial coefficient as a long.
   * @throws ArithmeticException if the result overflows a long or if inputs are invalid.
   */
  public static long qBinomial(int q, int n, int k) {
      if (n < 0 || k < 0 || k > n || q <= 0) {
          throw new IllegalArgumentException("Invalid inputs: ensure n >= 0, 0 <= k <= n, and q > 0.");
      }
      
      // Compute [n]_q!, [k]_q!, and [n-k]_q!
      long nFactorial = qFactorial(q,n);
      long kFactorial = qFactorial(q, k);
      long nMinusKFactorial = qFactorial(q, n - k);

      // Compute Gaussian binomial coefficient
      if (kFactorial > 0 && nMinusKFactorial > 0 && nFactorial % (kFactorial * nMinusKFactorial) == 0) {
          return nFactorial / (kFactorial * nMinusKFactorial);
      } else {
          throw new ArithmeticException("Gaussian binomial coefficient computation failed due to overflow or division error.");
      }
  }
  
  public static boolean isPowerOfTwo(int n) {
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

  public static long gcd(long a0, long b0) {
    long a = a0;
    long b = b0;
    long t = 0;
    while (b != 0) {
      t = b;
      b = a % b;
      a = t;
    }
    return a;
  }

  public static long lcm(long a, long b) {
    return (a * b) / gcd(a, b);
  }

  public static long catalan(int n) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative.");
    }
  
    // Array to store the computed Catalan numbers
    long[] catalan = new long[n + 1];
  
    // Initialize the first Catalan number
    catalan[0] = 1;
  
    // Calculate Catalan numbers using dynamic programming
    for (int i = 1; i <= n; i++) {
        catalan[i] = 0;
        for (int j = 0; j < i; j++) {
            // Check for overflow before performing the multiplication
            if (catalan[i] > Long.MAX_VALUE / catalan[j]) {
                throw new ArithmeticException("Overflow detected.");
            }
  
            catalan[i] += catalan[j] * catalan[i - 1 - j];
  
            // Check for overflow after addition
            if (catalan[i] < 0) {
                throw new ArithmeticException("Overflow detected.");
            }
        }
    }
  
    return catalan[n];
  }
  
  public static long bell(int n) {
    long[][] bellTriangle = new long[n + 1][n + 1];

    // Initialize the first row
    bellTriangle[0][0] = 1;

    // Build the Bell triangle
    for (int i = 1; i <= n; i++) {
        // Start the row with the rightmost element from the previous row
        bellTriangle[i][0] = bellTriangle[i - 1][i - 1];

        // Calculate the rest of the row
        for (int j = 1; j <= i; j++) {
            bellTriangle[i][j] = bellTriangle[i][j - 1] + bellTriangle[i - 1][j - 1];
        }
    }

    // The Bell number is the leftmost element of the last row
    return bellTriangle[n][0];
  }
  
  public static long binomial(int n, int k) {
    if (n < 0) {
      throw new IllegalArgumentException("n must be non-negative.");
    }
    if (k < 0) {
        throw new IllegalArgumentException("k must be non-negative.");
    }
    if (k > n) {
        throw new IllegalArgumentException("k cannot be greater than n.");
    }
  
    // Since binomial(n, k) == binomial(n, n-k), use the smaller k for efficiency
    if (k > n - k) {
        k = n - k;
    }
  
    long result = 1;
  
    for (int i = 0; i < k; i++) {
        // Check for overflow before multiplying
        if (result > Long.MAX_VALUE / (n - i)) {
            throw new ArithmeticException("Overflow detected.");
        }
  
        result *= (n - i);
        result /= (i + 1);  // Division will not overflow, safe operation
    }
  
    return result;
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
    for(int i = 0; i < n.length; i++) {
      if (n[i] < 0) {
        throw new IllegalArgumentException();
      }
      sum += n[i];
    }

    long nf = factorial(sum);
    for (int i = 0; i < n.length; i++) {
      nf = nf / factorial(n[i]);
    }

    return nf;
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
  public static long triangularNumber(int n) {return binomial(n+1, 2);}
  public static long reverseTriangularNumber(int n) {
    return Double.valueOf(Math.floor((Math.sqrt((double)(1+8*n))-1.0)/2.0)).intValue();
  }
}
