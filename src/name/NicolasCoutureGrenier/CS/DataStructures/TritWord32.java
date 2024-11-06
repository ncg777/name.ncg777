package name.NicolasCoutureGrenier.CS.DataStructures;

import java.util.TreeMap;
/**
 * A class representing a word of 32 trits.
 * 
 * Operators defined by:https://louis-dr.github.io/ternlogic.html
 * 
 */
public class TritWord32 {
    private Long storage = 0l;
       
    private static TreeMap<String,int[]> unaryOperators;
    private static TreeMap<String,int[][]> binaryOperators;
    private static TreeMap<Integer,Integer> trit_index= new TreeMap<Integer,Integer>();
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
    
    public TritWord32 BUF() { return unop("BUF"); }
    public TritWord32 NOT() { return unop("NOT"); }
    public TritWord32 PNOT() { return unop("PNOT"); }
    public TritWord32 NNOT() { return unop("NNOT"); }
    public TritWord32 ABS() { return unop("ABS"); }
    public TritWord32 CLU() { return unop("CLU"); }
    public TritWord32 CLD() { return unop("CLD"); }
    public TritWord32 INC() { return unop("INC"); }
    public TritWord32 DEC() { return unop("DEC"); }
    public TritWord32 RTU() { return unop("RTU"); }
    public TritWord32 RTD() { return unop("RTD"); }
    public TritWord32 ISP() { return unop("ISP"); }
    public TritWord32 ISZ() { return unop("ISZ"); }
    public TritWord32 ISN() { return unop("ISN"); }
    
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
    
    
    public static int unop(String opname, int t) {
      if(t < -1 || t > 1) throw new RuntimeException("Invalid input.");
      return unaryOperators.get(opname)[trit_index.get(t)];
    }
    
    private TritWord32 unop(String opname) {
      var o = new TritWord32();
      for(int i=0;i<32;i++) {
        o.set(i, unaryOperators.get(opname)[trit_index.get(this.get(i))]);
      }
      return o;
    }
        
    private TritWord32 binop(String opname, TritWord32 other) {
      var o = new TritWord32();
      for(int i=0;i<32;i++) {
        o.set(i, binaryOperators.get(opname)[trit_index.get(this.get(i))][trit_index.get(other.get(i))]);
      }
      return o;
    }
    
    private static int binop(String opname, int t, int u) {
      return binaryOperators.get(opname)[trit_index.get(t)][trit_index.get(u)];
    }
    
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
    
    public TritWord32 AND(TritWord32 other) { return binop("AND", other); }
    public TritWord32 OR(TritWord32 other) { return binop("OR", other); }
    public TritWord32 NOR(TritWord32 other) { return binop("NOR", other); }
    public TritWord32 CONS(TritWord32 other) { return binop("CONS", other); }
    public TritWord32 NCONS(TritWord32 other) { return binop("NCONS", other); }
    public TritWord32 ANY(TritWord32 other) { return binop("ANY", other); }
    public TritWord32 NANY(TritWord32 other) { return binop("NANY", other); }
    public TritWord32 MUL(TritWord32 other) { return binop("MUL", other); }
    public TritWord32 NMUL(TritWord32 other) { return binop("NMUL", other); }
    public TritWord32 SUM(TritWord32 other) { return binop("SUM", other); }
    public TritWord32 NSUM(TritWord32 other) { return binop("NSUM", other); }
    
    public static int AND(int t, int u) { return binop("AND", t, u); }
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
    
    public TritWord32() {
        boolean is64Bit = System.getProperty("os.arch").contains("64");
        if(!is64Bit) throw new RuntimeException("Only 64 bit architectures supported");    
    }

    private int encodeTrit(int trit) {
        switch (trit) {
            case -1: return 0b10;  // 2 in binary
            case 0: return 0b00;   // 0 in binary
            case 1: return 0b01;   // 1 in binary
            default: throw new IllegalArgumentException("Invalid trit value");
        }
    }
    
    private int decodeTrit(int bits) {
        switch (bits) {
            case 0b10: return -1;
            case 0b00: return 0;
            case 0b01: return 1;
            default: throw new IllegalArgumentException("Invalid bit pattern");
        }
    }

    public void set(int index, int trit) {
        if (trit != -1 && trit != 0 && trit != 1) {
            throw new IllegalArgumentException("Trit value must be -1, 0, or 1");
        }

        int bitOffset = index * 2;

        storage &= ~(0b11L << bitOffset);
        storage |= ((long) encodeTrit(trit)) << bitOffset;
        
    }

    public int get(int index) {
        int bitOffset = index  * 2;

        long bits = (storage >> bitOffset) & 0b11L;
        return decodeTrit((int) bits);
    }
    
    public Long getLong() {
      return storage;
    }
    
    public String toString() {
      final TreeMap<Integer, String> symbols = new TreeMap<>();
      
      symbols.put(-1, "T");
      symbols.put(0, "0");
      symbols.put(1, "1");
      
      StringBuilder sb = new StringBuilder();
      
      for(int i=0;i<32;i++) {
        sb.append(symbols.get(this.get(i)));
      }
      
      return sb.toString();
    }
}
