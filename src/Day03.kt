class RucksacksAnalyzer {
    companion object {
        private const val CODE_PRIOR_TO_LOWERCASE_A = 96
        private const val CODE_PRIOR_TO_UPPERCASE_A = 64
        private const val ENGLISH_ALPHABET_LENGTH = 26
        private const val ELVES_PER_GROUP = 3

        fun sumPrioritiesOfCommonItems(input: List<String>) =
            input.asSequence()
                .filter { it.isNotBlank() }
                .map { Rucksack(it).getCommonElementsInComponents() }
                .flatten()
                .map { it.toPriority() }
                .sum()

        fun sumPrioritiesOfGroupBadges(input: List<String>) =
            input.asSequence()
                .filter { it.isNotBlank() }
                .map { Rucksack(it).allUniqueItems }
                .chunked(ELVES_PER_GROUP)
                .map { it.reduce(Set<Char>::intersect) }
                .flatten()
                .map { it.toPriority() }
                .sum()

        private fun Char.toPriority() =
            if (this.isLowerCase()) {
                this.code - CODE_PRIOR_TO_LOWERCASE_A
            } else {
                this.code - CODE_PRIOR_TO_UPPERCASE_A + ENGLISH_ALPHABET_LENGTH
            }
    }
}

class Rucksack(items: String) {
    private val componentOne: Set<Char>
    private val componentTwo: Set<Char>
    val allUniqueItems
        get() = componentOne.plus(componentTwo)

    init {
        items.chunked(items.length / 2).also {
            componentOne = it[0].toSet()
            componentTwo = it[1].toSet()
        }
    }

    fun getCommonElementsInComponents() = componentOne.intersect(componentTwo)
}

fun main() {
    fun part1(input: List<String>) = RucksacksAnalyzer.sumPrioritiesOfCommonItems(input)

    fun part2(input: List<String>) = RucksacksAnalyzer.sumPrioritiesOfGroupBadges(input)

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = 157
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
    val part2TestOutput = part2(testInput)
    val part2ExpectedOutput = 70
    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
