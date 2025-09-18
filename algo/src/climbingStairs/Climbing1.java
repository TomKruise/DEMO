package climbingStairs;

public class Climbing1 {
    int climbingStairsDFS(int n) {
        return dfs(n);
    }

    private int dfs(int n) {
        if (n == 1 || n == 2) return n;
        int count = dfs(n - 1) + dfs(n - 2);
        return count;
    }
}
