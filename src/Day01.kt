fun main() {
    class CalorieTracker(val n: Int) {
        private var topNMaxCalories = List(n) { 0 }
        private var runningTotalCaloriesForCurrentElf = 0

        fun tally(input: List<String>): CalorieTracker {
            input.forEach {
                if (it.isBlank()) {
                    stopTrackingCurrentElf()
                } else {
                    addCaloriesToCurrentElfTotal(it)
                }
            }
            return this
        }

        fun sumTopNMaxCalories() = topNMaxCalories.sum()

        private fun addCaloriesToCurrentElfTotal(calories: Int) {
            check(calories >= 0) { "calories must be greater than or equal to 0" }
            runningTotalCaloriesForCurrentElf += calories
        }

        private fun addCaloriesToCurrentElfTotal(calories: String) {
            addCaloriesToCurrentElfTotal(calories.toInt())
        }

        private fun stopTrackingCurrentElf() {
            topNMaxCalories = topNMaxCalories.plus(runningTotalCaloriesForCurrentElf).sortedDescending().take(n)
            runningTotalCaloriesForCurrentElf = 0
        }
    }

    fun part1(input: List<String>) = CalorieTracker(1).tally(input).sumTopNMaxCalories()

    fun part2(input: List<String>) = CalorieTracker(3).tally(input).sumTopNMaxCalories()

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = 24000
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
    val part2TestOutput = part2(testInput)
    val part2ExpectedOutput = 45000
    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
