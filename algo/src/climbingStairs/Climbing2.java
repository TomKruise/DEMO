package climbingStairs;

import java.util.*;

public class Climbing2 {
    int climbingStairsDFSMem(int n) {
        // mem[i] 记录爬到第 i 阶的方案总数，-1 代表无记录
        int[] mem = new int[n+1];
        Arrays.fill(mem, -1);
        return dfs(n, mem);
    }

    private int dfs(int n, int[] mem) {
        if (n == 1 || n == 2) {
            return n;
        }
        if (mem[n] != -1) {
            return mem[n];
        }

        int count = dfs(n - 1, mem) + dfs(n - 2, mem);
        mem[n] = count;
        return count;
    }
}

