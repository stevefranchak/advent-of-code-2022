class StrategyGuideParser(private val scorer: Scorer) {
    fun sumScores(input: List<String>) =
        input.asSequence().map(::scoreLine).sum()

    private fun scoreLine(line: String): Int {
        if (line.isBlank()) {
            return 0
        }
        return scorer.score(line.split(" "))
    }
}

interface Scorer {
    fun score(lineParts: List<String>): Int
}

class Part1Scorer : Scorer {
    override fun score(lineParts: List<String>): Int {
        val (otherPlayed, selfPlayed) = lineParts.map(Play::fromString)
        return selfPlayed.score + RoundOutcome.fromPlays(selfPlayed, otherPlayed).score
    }
}

class Part2Scorer : Scorer {
    override fun score(lineParts: List<String>): Int {
        val otherPlayed = Play.fromString(lineParts[0])
        val desiredOutcome = RoundOutcome.fromString(lineParts[1])
        return desiredOutcome.toPlayForOutcome(otherPlayed).score + desiredOutcome.score
    }
}

enum class Play(val score: Int) {
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

enum class RoundOutcome(val score: Int) {
    LOSS(0),
    DRAW(3),
    WIN(6);

    fun toPlayForOutcome(otherPlay: Play) =
        when (this) {
            LOSS -> when (otherPlay) {
                Play.ROCK -> Play.SCISSORS
                Play.PAPER -> Play.ROCK
                Play.SCISSORS -> Play.PAPER
            }

            WIN -> when (otherPlay) {
                Play.ROCK -> Play.PAPER
                Play.PAPER -> Play.SCISSORS
                Play.SCISSORS -> Play.ROCK
            }

            DRAW -> otherPlay
        }

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

        fun fromString(input: String) =
            when (input.trim()) {
                "X" -> LOSS
                "Y" -> DRAW
                "Z" -> WIN
                else -> throw IllegalArgumentException("Unknown round outcome input provided: $input")
            }
    }
}

fun main() {
    fun part1(input: List<String>) = StrategyGuideParser(Part1Scorer()).sumScores(input)

    fun part2(input: List<String>) = StrategyGuideParser(Part2Scorer()).sumScores(input)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = 15
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
    val part2TestOutput = part2(testInput)
    val part2ExpectedOutput = 12
    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
