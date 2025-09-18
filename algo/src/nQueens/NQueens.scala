object NQueens {
  def main (args: Array[String]): Unit = {

  }

  def backtrack(row: Int, n: Int, state: List[List[String]], res: List[List[List[String]]], cols: Array[Boolean], diags1: Array[Boolean], diags2: Array[Boolean]): Unit = {
    if (row == n) {
      val copyState :List[List[String]] = List()
      for(s <- state) {
        copyState.::(s)
      }
      res.::(copyState)
      return
    }

    for (col <- 0 to n-1) {
      val diag1 = row - col + n - 1
      val diag2 = row + col
      if (!cols(col) && !diags1(diag1) && !diags2(diag2)){
        state(row).updated(col, "Q")
        cols.update(col, true)
        diags1.update(diag1, true)
        diags2.update(diag2,true)
        backtrack(row+1, n, state, res, cols, diags1, diags2)
        state(row).updated(col, "#")
        cols.update(col, false)
        diags1.update(diag1, false)
        diags2.update(diag2,false)
      }
    }
  }

  def nQueens(n: Int): List[List[List[String]]] = {
    val state : List[List[String]] = List()
    for (i <- 1 to n) {
      val row: List[String] = List()
      for(j <- 1 to n) {
        row.::("#")
      }
      state.::(row)
    }
    val cols:Array[Boolean] = new Array[Boolean](n)
    val diags1:Array[Boolean] = new Array[Boolean](2*n - 1)
    val diags2:Array[Boolean] = new Array[Boolean](2*n - 1)
    val res : List[List[List[String]]] = List()
    backtrack(0, n, state, res, cols, diags1, diags2)
    return res
  }
}