package name.ncg777.maths.enumerations;
import java.util.*;

public class PartitionEnumeration implements Enumeration<List<Integer>> {
  private final List<Integer> currentPartition;
  private final List<List<Integer>> partitions;
  private int index = 0;

  public PartitionEnumeration(int n) {
      this.currentPartition = new ArrayList<>();
      this.partitions = new ArrayList<>();
      generatePartitions(n, currentPartition);
  }

  private void generatePartitions(int n, List<Integer> currentPartition) {
      if (n == 0) {
          partitions.add(new ArrayList<>(currentPartition));
          return;
      }

      for (int i = 1; i <= n; i++) {
          if (currentPartition.isEmpty() || i <= currentPartition.get(currentPartition.size() - 1)) {
              currentPartition.add(i);
              generatePartitions(n - i, currentPartition);
              currentPartition.remove(currentPartition.size() - 1);
          }
      }
  }

  @Override
  public boolean hasMoreElements() {
      return index < partitions.size();
  }

  @Override
  public List<Integer> nextElement() {
      return partitions.get(index++);
  }

  public static PartitionEnumeration of(int n) {
      return new PartitionEnumeration(n);
  }
}