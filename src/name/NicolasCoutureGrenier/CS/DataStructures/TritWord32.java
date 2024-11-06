package name.NicolasCoutureGrenier.CS.DataStructures;

public class TritWord32 {
    private Long storage = 0l;
    
    // UNARY OPERATORS
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
    
    // BINARY OPERATORS
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
    
    public TritWord32() {
        boolean is64Bit = System.getProperty("os.arch").contains("64");
        if(!is64Bit) throw new RuntimeException("Only 64 bit architectures supported");    
    }

    // Encode a trit into 2 bits
    private int encodeTrit(int trit) {
        switch (trit) {
            case -1: return 0b10;  // 2 in binary
            case 0: return 0b00;   // 0 in binary
            case 1: return 0b01;   // 1 in binary
            default: throw new IllegalArgumentException("Invalid trit value");
        }
    }

    // Decode 2 bits into a trit
    private int decodeTrit(int bits) {
        switch (bits) {
            case 0b10: return -1;
            case 0b00: return 0;
            case 0b01: return 1;
            default: throw new IllegalArgumentException("Invalid bit pattern");
        }
    }

    // Set a trit at a specific index
    public void set(int index, int trit) {
        if (trit != -1 && trit != 0 && trit != 1) {
            throw new IllegalArgumentException("Trit value must be -1, 0, or 1");
        }

        int bitOffset = index * 2;

        storage &= ~(0b11L << bitOffset);
        storage |= ((long) encodeTrit(trit)) << bitOffset;
        
    }

    // Get a trit at a specific index
    public int get(int index) {
        int bitOffset = index  * 2;

        long bits = (storage >> bitOffset) & 0b11L;
        return decodeTrit((int) bits);
    
    }
}
