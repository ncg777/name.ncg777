package name.ncg777.CS;

import java.util.Arrays;

public class KnapsackSolver {
  public static int[] solve(int[] profits, int[] weights, int capacity, double epsilon) {
      System.out.println("Parameters:");
      System.out.println("Profits: " + Arrays.toString(profits));
      System.out.println("Weights: " + Arrays.toString(weights));
      System.out.println("Capacity: " + capacity);
      System.out.println("Epsilon: " + epsilon);

      int n = profits.length;
      int[] scaledProfits = scaleProfits(profits, epsilon);
      int[][] dp = new int[n + 1][capacity + 1];

      for (int i = 1; i <= n; i++) {
          for (int w = 1; w <= capacity; w++) {
              if (weights[i - 1] <= w) {
                  dp[i][w] = Math.max(dp[i - 1][w], dp[i - 1][w - weights[i - 1]] + scaledProfits[i - 1]);
              } else {
                  dp[i][w] = dp[i - 1][w];
              }
          }
      }

      int[] solution = new int[n];
      int w = capacity;
      for (int i = n; i > 0; i--) {
          if (dp[i][w] != dp[i - 1][w]) {
              solution[i - 1] = 1;
              w -= weights[i - 1];
          }
      }

      System.out.println("Solution:");
      for (int i = 0; i < n; i++) {
          System.out.print(solution[i] + " ");
      }
      System.out.println();

      return solution;
  }

  private static int[] scaleProfits(int[] profits, double epsilon) {
      int U = computeUpperBound(profits);
      int K = Math.max(1, (int) (epsilon * U / (2 * profits.length)));
      int[] scaledProfits = new int[profits.length];
      for (int i = 0; i < profits.length; i++) {
          scaledProfits[i] = profits[i] / K;
      }
      return scaledProfits;
  }

  private static int computeUpperBound(int[] profits) {
      int sum = 0;
      for (int profit : profits) {
          sum += profit;
      }
      return sum;
  }
}