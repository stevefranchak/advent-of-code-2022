import java.util.function.Predicate

class SectionAssignment(
    private val start: Int,
    private val end: Int,
) {
    fun contains(other: SectionAssignment) = other.start >= start && other.end <= end

    fun overlaps(other: SectionAssignment) = end >= other.start && start <= other.end

    companion object {
        fun fromString(input: String) =
            input.split("-").map(String::toInt).let { SectionAssignment(it[0], it[1]) }
    }
}

fun main() {
    fun processPart(input: List<String>, filter: Predicate<Pair<SectionAssignment, SectionAssignment>>) =
        input.asSequence()
            .filter(String::isNotBlank)
            .map {
                it.split(",").map(SectionAssignment::fromString).let { assignments ->
                    Pair(assignments[0], assignments[1])
                }
            }
            .filter(filter::test)
            .count()

    fun part1(input: List<String>) = processPart(input) { it.first.contains(it.second) || it.second.contains(it.first) }

    fun part2(input: List<String>) = processPart(input) { it.first.overlaps(it.second) }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = 2
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
    val part2TestOutput = part2(testInput)
    val part2ExpectedOutput = 4
    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
