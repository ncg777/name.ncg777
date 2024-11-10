package name.ncg777.CS;

import org.junit.Test;

import junit.framework.TestCase;
import name.ncg777.CS.KnapsackSolver;

import static org.junit.Assert.*;

public class KnapsackSolverTests extends TestCase {

    @Test
    public void testExample1() {
        int[] profits = {60, 100, 120};
        int[] weights = {10, 20, 30};
        int capacity = 50;
        double epsilon = 0.1;
        int[] expectedSolution = {0, 1, 1};
        int[] solution = KnapsackSolver.solve(profits, weights, capacity, epsilon);
        assertArrayEquals(expectedSolution, solution);
    }

    @Test
    public void testExample2() {
        int[] profits = {10, 20, 30, 40};
        int[] weights = {5, 10, 15, 20};
        int capacity = 30;
        double epsilon = 0.1;
        int[] expectedSolution = {1, 1, 1, 0};
        int[] solution = KnapsackSolver.solve(profits, weights, capacity, epsilon);
        assertArrayEquals(expectedSolution, solution);
    }

    @Test
    public void testExample3() {
        int[] profits = {5, 10, 15, 20, 25};
        int[] weights = {2, 4, 6, 8, 10};
        int capacity = 20;
        double epsilon = 0.1;
        int[] expectedSolution = {1, 1, 1, 1, 0};
        int[] solution = KnapsackSolver.solve(profits, weights, capacity, epsilon);
        assertArrayEquals(expectedSolution, solution);
    }

    @Test
    public void testEdgeCase1() {
        int[] profits = {10};
        int[] weights = {10};
        int capacity = 5;
        double epsilon = 0.1;
        int[] expectedSolution = {0};
        int[] solution = KnapsackSolver.solve(profits, weights, capacity, epsilon);
        assertArrayEquals(expectedSolution, solution);
    }

    @Test
    public void testEdgeCase2() {
        int[] profits = {10, 20};
        int[] weights = {5, 10};
        int capacity = 0;
        double epsilon = 0.1;
        int[] expectedSolution = {0, 0};
        int[] solution = KnapsackSolver.solve(profits, weights, capacity, epsilon);
        assertArrayEquals(expectedSolution, solution);
    }
    
    @Test
    public void testMultipleItems() {
        int[] profits = {60, 100, 120};
        int[] weights = {10, 20, 30};
        int capacity = 50;
        double epsilon = 0.01;
        int[] expectedSolution = {0,1,1};
        assertArrayEquals(expectedSolution, KnapsackSolver.solve(profits, weights, capacity, epsilon));
    }

    @Test
    public void testLargeCapacity() {
        int[] profits = {10, 20, 30, 40, 50};
        int[] weights = {5, 10, 15, 20, 25};
        int capacity = 100;
        double epsilon = 0.01;
        int[] expectedSolution = {1,1,1,1,1};
        assertArrayEquals(expectedSolution, KnapsackSolver.solve(profits, weights, capacity, epsilon));
    }

    @Test
    public void testSmallEpsilon() {
        int[] profits = {10, 20, 30};
        int[] weights = {5, 10, 15};
        int capacity = 20;
        double epsilon = 0.0001;
        int[] expectedSolution = {1,0,1};
        assertArrayEquals(expectedSolution, KnapsackSolver.solve(profits, weights, capacity, epsilon));
    }

    @Test
    public void testDuplicateItems() {
        int[] profits = {10, 10, 20, 20, 30};
        int[] weights = {5, 5, 10, 10, 15};
        int capacity = 30;
        double epsilon = 0.01;
        int[] expectedSolution = {1,1,1,1,0};
        assertArrayEquals(expectedSolution, KnapsackSolver.solve(profits, weights, capacity, epsilon));
    }
}
