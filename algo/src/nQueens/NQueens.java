package nQueens;

import java.util.ArrayList;
import java.util.List;

public class NQueens {
    public void backtrack(int row, int n, List<List<String>> state, List<List<List<String>>> res, boolean[] cols, boolean[] diags1, boolean[] diags2) {
        if (row == n) {
            List<List<String>> copyState = new ArrayList<>();
            for (List<String> strings : state) {
                copyState.add(new ArrayList<>(strings));
            }
            res.add(copyState);
            return;
        }

        for (int col = 0; col < n; col++) {
            int diag1 = row - col +  n - 1;
            int diag2 = row + col;
            if (!cols[col] && !diags1[diag1] && !diags2[diag2]) {
                state.get(row).set(col, "Q");
                cols[col] = diags1[diag1] = diags2[diag2] = true;
                backtrack(row + 1, n , state, res, cols, diags1, diags2);
                state.get(row).set(col, "#");
                cols[col] = diags1[diag1] = diags2[diag2] = false;
            }
        }
    }

    List<List<List<String>>> nQueens(int n) {
        // 初始化 n*n 大小的棋盘，其中 'Q' 代表皇后，'#' 代表空位
        List<List<String>> state = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                row.add("#");
            }
            state.add(row);
        }

        boolean[] cols = new boolean[n];
        boolean[] diags1 = new boolean[2*n-1];
        boolean[] diags2 = new boolean[2*n - 1];
        List<List<List<String>>> res = new ArrayList<>();
        backtrack(0, n, state, res, cols, diags1, diags2);
        return res;
    }
}
