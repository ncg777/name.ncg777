package name.NicolasCoutureGrenier.Maths.Enumerations;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * https://www.researchgate.net/publication/228566729_Efficient_generation_of_set_partitions
 */
public class SetPartitionEnumeration implements Enumeration<int[]> {
    private final int n;
    private int[] k;
    private int[] m;
    private boolean hasMore;

    public SetPartitionEnumeration(int n) {
        this.n = n;
        this.k = new int[n];
        this.m = new int[n];
        this.hasMore = true;

        initializeFirstPartition();
    }

    private void initializeFirstPartition() {
        for (int i = 0; i < n; i++) {
            k[i] = 0;
            m[i] = 0;
        }
    }

    private boolean nextPartition() {
        for (int i = n - 1; i > 0; i--) {
            if (k[i] <= m[i - 1]) {
                k[i]++;
                m[i] = Math.max(m[i], k[i]);

                for (int j = i + 1; j < n; j++) {
                    k[j] = 0;
                    m[j] = m[i];
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasMoreElements() {
        return hasMore;
    }

    @Override
    public int[] nextElement() {
        if (!hasMore) {
            throw new NoSuchElementException();
        }

        int[] currentPartition = k.clone();
        if(n==0) {this.hasMore=false;return new int[0];}
        else {hasMore = nextPartition();}

        return currentPartition;
    }

}
