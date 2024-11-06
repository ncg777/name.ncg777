package name.NicolasCoutureGrenier.Maths;

import java.util.TreeMap;

public class Trit {
  private static TreeMap<Integer,Integer> trit_index= new TreeMap<Integer,Integer>();
  private static TreeMap<String,int[]> unaryOperators;
  private static TreeMap<String,int[][]> binaryOperators;
  
  public static int AND(int t, int u) { return binop("AND", t, u); }
  public static int NAND(int t, int u) { return binop("NAND", t, u); }
  public static int OR(int t, int u) { return binop("OR", t, u); }
  public static int NOR(int t, int u) { return binop("NOR", t, u); }
  public static int CONS(int t, int u) { return binop("CONS", t, u); }
  public static int NCONS(int t, int u) { return binop("NCONS", t, u); }
  public static int ANY(int t, int u) { return binop("ANY", t, u); }
  public static int NANY(int t, int u) { return binop("NANY", t, u); }
  public static int MUL(int t, int u) { return binop("MUL", t, u); }
  public static int NMUL(int t, int u) { return binop("NMUL", t, u); }
  public static int SUM(int t, int u) { return binop("SUM", t, u); }
  public static int NSUM(int t, int u) { return binop("NSUM", t, u); }
  
  static {
    int[] BUF = {-1,0,1};
    int[] NOT = {1,0,-1};
    int[] PNOT = {1,1,-1};
    int[] NNOT = {1,-1,-1};
    int[] ABS = {1,0,1};
    int[] CLU = {0,0,1};
    int[] CLD = {-1,0,0};
    int[] INC = {0,1,1};
    int[] DEC = {-1,-1,0};
    int[] RTU = {0,1,-1};
    int[] RTD = {1,-1,0};
    int[] ISP = {-1,-1,1};
    int[] ISZ = {-1,1,-1};
    int[] ISN = {1,-1,-1};
    
    unaryOperators = new TreeMap<>();
    unaryOperators.put("BUF", BUF);
    unaryOperators.put("NOT", NOT);
    unaryOperators.put("PNOT", PNOT);
    unaryOperators.put("NNOT", NNOT);
    unaryOperators.put("ABS", ABS);
    unaryOperators.put("CLU", CLU);
    unaryOperators.put("CLD", CLD);
    unaryOperators.put("INC", INC);
    unaryOperators.put("DEC", DEC);
    unaryOperators.put("RTU", RTU);
    unaryOperators.put("RTD", RTD);
    unaryOperators.put("ISP", ISP);
    unaryOperators.put("ISZ", ISZ);
    unaryOperators.put("ISN", ISN);
    
    int[][] AND = {{-1,-1,-1},{-1,0,0},{-1,0,1}};
    int[][] NAND = {{1,1,1},{1,0,0},{1,0,-1}};
    int[][] OR = {{-1,0,1},{0,0,1},{1,1,1}};
    int[][] NOR = {{1,0,-1},{0,0,-1},{-1,-1,-1}};
    int[][] CONS = {{-1,0,0},{0,0,0},{0,0,1}};
    int[][] NCONS = {{1,0,0},{0,0,0},{0,0,-1}};
    int[][] ANY = {{-1,-1,0},{-1,0,1},{0,1,1}};
    int[][] NANY = {{1,1,0},{1,0,-1},{0,-1,-1}};
    int[][] MUL = {{1,0,-1},{0,0,0},{-1,0,1}};
    int[][] NMUL = {{-1,0,1},{0,0,0},{1,0,-1}};
    int[][] SUM = {{1,-1,0},{-1,0,1},{0,1,-1}};
    int[][] NSUM = {{-1,1,0},{1,0,-1},{0,-1,1}};
    
    binaryOperators = new TreeMap<>();
    binaryOperators.put("AND", AND);
    binaryOperators.put("NAND", NAND);
    binaryOperators.put("OR", OR);
    binaryOperators.put("NOR", NOR);
    binaryOperators.put("CONS", CONS);
    binaryOperators.put("NCONS", NCONS);
    binaryOperators.put("ANY", ANY);
    binaryOperators.put("NANY", NANY);
    binaryOperators.put("MUL", MUL);
    binaryOperators.put("NMUL", NMUL);
    binaryOperators.put("SUM", SUM);
    binaryOperators.put("NSUM", NSUM);
    
    trit_index.put(-1, 0);
    trit_index.put(0, 1);
    trit_index.put(1, 2);
  }
  
  
  public static int unop(String opname, int t) {
    if(!unaryOperators.containsKey(opname)) throw new RuntimeException("Unknown op.");
    if(t < -1 || t > 1) throw new RuntimeException("Invalid input.");
    return unaryOperators.get(opname)[trit_index.get(t)];
  }
  
  public static int BUF(int t) { return unop("BUF",t); }
  public static int NOT(int t) { return unop("NOT",t); }
  public static int PNOT(int t) { return unop("PNOT",t); }
  public static int NNOT(int t) { return unop("NNOT",t); }
  public static int ABS(int t) { return unop("ABS",t); }
  public static int CLU(int t) { return unop("CLU",t); }
  public static int CLD(int t) { return unop("CLD",t); }
  public static int INC(int t) { return unop("INC",t); }
  public static int DEC(int t) { return unop("DEC",t); }
  public static int RTU(int t) { return unop("RTU",t); }
  public static int RTD(int t) { return unop("RTD",t); }
  public static int ISP(int t) { return unop("ISP",t); }
  public static int ISZ(int t) { return unop("ISZ",t); }
  public static int ISN(int t) { return unop("ISN",t); }
  
  public static int binop(String opname, int t, int u) {
    if(!binaryOperators.containsKey(opname)) throw new RuntimeException("Unknown op.");
    if((t < -1 || t > 1) || (u < -1 || u > 1)) throw new RuntimeException("Invalid input.");
    
    return binaryOperators.get(opname)[trit_index.get(t)][trit_index.get(u)];
  }
}
