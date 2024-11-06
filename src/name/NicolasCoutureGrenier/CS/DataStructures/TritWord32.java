package name.NicolasCoutureGrenier.CS.DataStructures;

import java.util.TreeMap;

public class TritWord32 {
    private Long storage = 0l;
       
    private static TreeMap<String,int[]> unaryOperators;
    private static TreeMap<String,int[][]> binaryOperators;
    
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
    
    private TritWord32 unop(String opname) {
      var o = new TritWord32();
      for(int i=0;i<32;i++) {
        o.set(i, unaryOperators.get(opname)[this.get(i) == -1 ? 0 : this.get(i) == 0 ? 1 : 2]);
      }
      return o;
    }
        
    private TritWord32 binop(String opname, TritWord32 other) {
      var o = new TritWord32();
      for(int i=0;i<32;i++) {
        o.set(i, binaryOperators.get(opname)[this.get(i) == -1 ? 0 : this.get(i) == 0 ? 1 : 2][other.get(i) == -1 ? 0 : other.get(i) == 0 ? 1 : 2]);
      }
      return o;
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
}
