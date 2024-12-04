package name.ncg777.computerScience.dataStructures;

import java.util.TreeMap;

import name.ncg777.maths.Trit;

/**
 * A class representing a word of 32 trits.
 * 
 * Operators defined by:https://louis-dr.github.io/ternlogic.html
 * 
 */
public class TritWord32 {
    private Long storage = 0l;
    
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
        o.set(i, Trit.unaryOperator(opname, this.get(i)));
      }
      return o;
    }
        
    private TritWord32 binop(String opname, TritWord32 other) {
      var o = new TritWord32();
      for(int i=0;i<32;i++) {
        o.set(i, Trit.binaryOperator(opname, this.get(i), other.get(i)));
      }
      return o;
    }
    
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
        if(!is64Bit) throw new IllegalArgumentException("Only 64 bit architectures supported");    
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
      
      for(int i=31;i<0;i--) {
        sb.append(symbols.get(this.get(i)));
      }
      
      return sb.toString();
    }
}
