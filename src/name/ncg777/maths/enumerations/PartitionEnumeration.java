package name.ncg777.maths.enumerations;

import java.util.*;
import java.util.NoSuchElementException;

/**
 * Enumeration for generating all integer partitions of a given number n.
 * Each partition is represented as a List of integers in non-increasing order.
 */
public class PartitionEnumeration implements Enumeration<List<Integer>> {

    private final int[] p;    // Array to store the current partition
    private int k;            // Current index in p
    private boolean hasNext;  // Flag to indicate if more partitions are available
    private boolean first;    // Flag to handle the first partition
    private List<Integer> next;
    /**
     * Constructor to initialize the enumeration with a given number n.
     *
     * @param n the integer to partition
     */
    public PartitionEnumeration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative.");
        }
        // Maximum possible number of parts is n (all 1's)
        this.p = new int[n + 1];
        this.p[0] = n; // Start with the partition [n]
        this.k = 0;    // Start at the first index
        this.hasNext = n >= 0; // If n is 0, there is one partition: the empty partition
        this.first = true;
        next();
    }

    /**
     * Checks if there are more partitions to enumerate.
     *
     * @return true if more partitions are available, false otherwise
     */
    @Override
    public boolean hasMoreElements() {
        return next != null;
    }
    /**
     * Returns the next partition in the enumeration.
     *
     * @return the next partition as a List of integers
     * @throws NoSuchElementException if no more partitions are available
     */
    @Override
    public List<Integer> nextElement() {
      var o = List.copyOf(next);
      next();
      return o;
    }
    
    private void next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more partitions available.");
        }

        // Handle the first partition
        if (first) {
            first = false;
            if (p[0] == 0) {
                hasNext = false;
                next = Collections.emptyList();
                return;
            }
            next = Collections.singletonList(p[0]);
            return;
        }

        // Find the rightmost element that can be decreased
        int rem = 0;
        while (k >= 0 && p[k] == 1) {
            rem += p[k];
            k--;
        }

        // If k is negative, all elements were 1's and we are done
        if (k < 0) {
            hasNext = false;
            next = null; // No more partitions
            return;
        }

        // Decrease p[k] by 1
        p[k]--;

        // Calculate the new remainder to be distributed
        rem += 1;

        // Distribute the remainder among the following elements
        int i = k + 1;
        while (rem > p[k] && i < p.length) {
            p[i] = p[k];
            rem -= p[k];
            i++;
        }

        // Assign the remaining remainder
        if (i < p.length) {
            p[i] = rem;
            k = i; // Update the current index
        }

        // Construct the current partition to return
        List<Integer> currentPartition = new ArrayList<>();
        for (int j = 0; j <= k; j++) {
            currentPartition.add(p[j]);
        }

        this.next = currentPartition;
    }

    /**
     * Static factory method to create a PartitionEnumeration for a given number n.
     *
     * @param n the integer to partition
     * @return a new PartitionEnumeration instance
     */
    public static PartitionEnumeration of(int n) {
        return new PartitionEnumeration(n);
    }
}