package name.NicolasCoutureGrenier.CS.DataStructures;

public class TritSet {
    private final Object storage;
    private final int tritsPerInt;
    private final int size;

    public TritSet(int size) {
        this.size = size;
        boolean is64Bit = System.getProperty("os.arch").contains("64");

        if (is64Bit) {
            // Use 64-bit integers
            tritsPerInt = 32; // 64 bits / 2 bits per trit
            int numLongs = (int) Math.ceil((double) size / tritsPerInt);
            storage = new long[numLongs];
        } else {
            // Use 32-bit integers
            tritsPerInt = 16; // 32 bits / 2 bits per trit
            int numInts = (int) Math.ceil((double) size / tritsPerInt);
            storage = new int[numInts];
        }
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

        int intIndex = index / tritsPerInt;
        int bitOffset = (index % tritsPerInt) * 2;

        if (storage instanceof long[]) {
            long[] longStorage = (long[]) storage;
            longStorage[intIndex] &= ~(0b11L << bitOffset);
            longStorage[intIndex] |= ((long) encodeTrit(trit)) << bitOffset;
        } else {
            int[] intStorage = (int[]) storage;
            intStorage[intIndex] &= ~(0b11 << bitOffset);
            intStorage[intIndex] |= (encodeTrit(trit) << bitOffset);
        }
    }

    // Get a trit at a specific index
    public int get(int index) {
        int intIndex = index / tritsPerInt;
        int bitOffset = (index % tritsPerInt) * 2;

        if (storage instanceof long[]) {
            long[] longStorage = (long[]) storage;
            long bits = (longStorage[intIndex] >> bitOffset) & 0b11L;
            return decodeTrit((int) bits);
        } else {
            int[] intStorage = (int[]) storage;
            int bits = (intStorage[intIndex] >> bitOffset) & 0b11;
            return decodeTrit(bits);
        }
    }

    // Get the size of the TritSet (number of trits)
    public int size() {
        return this.size;
    }

    // Quantize method with beta cut threshold
    public static TritSet quantize(Iterable<Double> values, double threshold) {
        int count = 0;
        for (@SuppressWarnings("unused") Double ignored : values) {
            count++;
        }

        TritSet tritSet = new TritSet(count);
        int index = 0;

        for (Double value : values) {
            if (value > threshold) {
                tritSet.set(index, 1);
            } else if (value < -threshold) {
                tritSet.set(index, -1);
            } else {
                tritSet.set(index, 0);
            }
            index++;
        }

        return tritSet;
    }
}
