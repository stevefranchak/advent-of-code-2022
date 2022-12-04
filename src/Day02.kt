class StrategyGuideParser {
    companion object {
        fun sumScores(input: List<String>) =
            input.asSequence().map(::scoreLine).sum()

        private fun scoreLine(line: String): Int {
            if (line.isBlank()) {
                return 0
            }
            val (otherPlayed, selfPlayed) = line.split(" ").map(Play::fromString)
            return selfPlayed.score + RoundOutcome.fromPlays(selfPlayed, otherPlayed).score
        }
    }

    private enum class Play(val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        companion object {
            fun fromString(input: String) =
                when (input.trim()) {
                    "A", "X" -> ROCK
                    "B", "Y" -> PAPER
                    "C", "Z" -> SCISSORS
                    else -> throw IllegalArgumentException("Unknown play input provided: $input")
                }
        }
    }

    private enum class RoundOutcome(val score: Int) {
        LOSS(0),
        DRAW(3),
        WIN(6);

        companion object {
            fun fromPlays(self: Play, other: Play) =
                when (self) {
                    Play.ROCK -> when (other) {
                        Play.ROCK -> DRAW
                        Play.PAPER -> LOSS
                        Play.SCISSORS -> WIN
                    }

                    Play.PAPER -> when (other) {
                        Play.ROCK -> WIN
                        Play.PAPER -> DRAW
                        Play.SCISSORS -> LOSS
                    }

                    Play.SCISSORS -> when (other) {
                        Play.ROCK -> LOSS
                        Play.PAPER -> WIN
                        Play.SCISSORS -> DRAW
                    }
                }
        }
    }
}

fun main() {
    fun part1(input: List<String>) = StrategyGuideParser.sumScores(input)

    fun part2(input: List<String>) = input.size

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = 15
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
//    val part2TestOutput = part2(testInput)
//    val part2ExpectedOutput = 45000
//    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
