class TreeGrid(private val grid: List<List<Int>>) : List<List<Int>> by grid {
    private val height
        get() = this.size

    // Assumption: all rows have the same length
    private val width
        get() = this[0].size

    private fun goUpFrom(rowIndex: Int, columnIndex: Int) =
        if (rowIndex == 0) emptyList()
        else (rowIndex - 1 downTo 0).map { this[it][columnIndex] }

    private fun goDownFrom(rowIndex: Int, columnIndex: Int) =
        if (rowIndex == height - 1) emptyList()
        else (rowIndex + 1 until height).map { this[it][columnIndex] }

    private fun goLeftFrom(rowIndex: Int, columnIndex: Int) =
        if (columnIndex == 0) emptyList()
        else (columnIndex - 1 downTo 0).map { this[rowIndex][it] }

    private fun goRightFrom(rowIndex: Int, columnIndex: Int) =
        if (columnIndex == width - 1) emptyList()
        else (columnIndex + 1 until width).map { this[rowIndex][it] }

    fun isTreeAtPositionVisible(rowIndex: Int, columnIndex: Int): Boolean {
        if (isPositionOnEdge(rowIndex, columnIndex)) return true
        val treeHeightAtPosition = this[rowIndex][columnIndex]
        if (goLeftFrom(rowIndex, columnIndex).all { it < treeHeightAtPosition }) return true
        if (goRightFrom(rowIndex, columnIndex).all { it < treeHeightAtPosition }) return true
        if (goUpFrom(rowIndex, columnIndex).all { it < treeHeightAtPosition }) return true
        if (goDownFrom(rowIndex, columnIndex).all { it < treeHeightAtPosition }) return true
        return false
    }

    private fun isPositionOnEdge(rowIndex: Int, columnIndex: Int) =
        rowIndex == 0 || columnIndex == 0 || rowIndex == height - 1 || columnIndex == width - 1

    fun getScenicScoreAtPosition(rowIndex: Int, columnIndex: Int): Int {
        // If at least one viewing distance is 0, and each viewing distance is a factor in the multiplication,
        // then the score will be 0
        if (isPositionOnEdge(rowIndex, columnIndex)) return 0
        val treeHeightAtPosition = this[rowIndex][columnIndex]
        val leftViewingDistance = calculateViewingDistance(goLeftFrom(rowIndex, columnIndex), treeHeightAtPosition)
        val rightViewingDistance = calculateViewingDistance(goRightFrom(rowIndex, columnIndex), treeHeightAtPosition)
        val upViewingDistance = calculateViewingDistance(goUpFrom(rowIndex, columnIndex), treeHeightAtPosition)
        val downViewingDistance = calculateViewingDistance(goDownFrom(rowIndex, columnIndex), treeHeightAtPosition)
        return leftViewingDistance * rightViewingDistance * upViewingDistance * downViewingDistance
    }

    companion object {
        fun fromInput(input: List<String>) =
            TreeGrid(
                input
                    .filter(String::isNotBlank)
                    .map { row -> row.toList().map(Char::digitToInt) }
                    .toList()
            )

        private fun calculateViewingDistance(treeHeights: List<Int>, heightOfCurrentTree: Int): Int {
            var count = 0
            for (treeHeight in treeHeights) {
                count++
                if (treeHeight >= heightOfCurrentTree) {
                    break
                }
            }
            return count
        }
    }

    override fun toString(): String {
        return "TreeGrid[height=$height, width=$width, grid=$grid]"
    }
}

fun main() {
    fun Boolean.toInt() =
        if (this) 1 else 0

    fun part1(grid: TreeGrid) =
        grid.mapIndexed { rowIndex, row ->
            List(row.size) { columnIndex ->
                grid.isTreeAtPositionVisible(rowIndex, columnIndex).toInt()
            }.sum()
        }.sum()

    fun part2(grid: TreeGrid) =
        grid.mapIndexed { rowIndex, row ->
            List(row.size) { columnIndex ->
                grid.getScenicScoreAtPosition(rowIndex, columnIndex)
            }.max()
        }.max()

    readInput("Day08_test").also { input ->
        val grid = TreeGrid.fromInput(input)
        val part1TestOutput = part1(grid)
        val part1ExpectedOutput = 21
        check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
        val part2TestOutput = part2(grid)
        val part2ExpectedOutput = 8
        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }

    readInput("Day08").also { input ->
        val grid = TreeGrid.fromInput(input)
        println(part1(grid))
        println(part2(grid))
    }
}
