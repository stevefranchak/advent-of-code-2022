class CrateStacks {
    companion object {
        private val EXTRACT_CRATE_ID_REGEX = Regex("""[A-Z]""")
    }

    private val stacks: MutableMap<Int, ArrayDeque<Char>> = mutableMapOf()

    fun addToStartingStacks(inputLine: String) {
        inputLine.chunked(4).asSequence()
            .map { crate ->
                EXTRACT_CRATE_ID_REGEX.find(crate)?.value?.get(0)
            }
            .forEachIndexed { index, crateId ->
                // Do this instead of filterNotNull'ing the sequence to retain the index
                if (crateId == null) return@forEachIndexed
                val key = index + 1
                stacks.computeIfAbsent(key) { ArrayDeque() }.add(crateId)
            }
    }

    fun executeMoveInstruction(craneSimulator: CraneSimulator, moveInstruction: MoveInstruction) {
        craneSimulator.moveCrates(
            moveInstruction.numCratesToMove,
            stacks[moveInstruction.fromStackIndex]!!,
            stacks[moveInstruction.toStackIndex]!!
        )
    }

    fun getMessage() = stacks.entries.sortedBy { it.key }.map { it.value.first() }.joinToString("")
}

class MoveInstruction(val numCratesToMove: Int, val fromStackIndex: Int, val toStackIndex: Int) {
    companion object {
        private val MOVE_INSTRUCTION_REGEX = Regex("""move (\d+) from (\d+) to (\d+)""")

        fun extractMoveInstruction(inputLine: String) =
            MOVE_INSTRUCTION_REGEX.find(inputLine)?.groupValues?.mapNotNull { it.toIntOrNull() }?.let {
                MoveInstruction(it[0], it[1], it[2])
            }
    }
}

interface CraneSimulator {
    fun moveCrates(numCratesToMove: Int, fromStack: ArrayDeque<Char>, toStack: ArrayDeque<Char>)
}

class CrateMover9000Simulator : CraneSimulator {
    override fun moveCrates(numCratesToMove: Int, fromStack: ArrayDeque<Char>, toStack: ArrayDeque<Char>) {
        (1..numCratesToMove).forEach { _ ->
            toStack.addFirst(fromStack.removeFirst())
        }
    }
}

class CrateMover9001Simulator : CraneSimulator {
    override fun moveCrates(numCratesToMove: Int, fromStack: ArrayDeque<Char>, toStack: ArrayDeque<Char>) {
        fromStack.steal(numCratesToMove)
            .reversed()
            .forEach {
                toStack.addFirst(it)
            }
    }

    /**
     * A mutable `take`.
     */
    private fun <E> ArrayDeque<E>.steal(n: Int): List<E> {
        require(n >= 1) { "n must be greater than or equal to 1" }
        return (1..n).map {
            removeFirst()
        }.toList()
    }
}

fun main() {
    fun processPart(input: List<String>, craneSimulator: CraneSimulator) = CrateStacks().apply {
        input.asSequence()
            // Also skip the "crate index" line - it's not needed with this approach
            .filter { it.isNotBlank() && !it.startsWith(" 1") }
            .forEach { inputLine ->
                MoveInstruction.extractMoveInstruction(inputLine)?.let {
                    executeMoveInstruction(craneSimulator, it)
                } ?: addToStartingStacks(inputLine)
            }
    }.getMessage()

    fun part1(input: List<String>) = processPart(input, CrateMover9000Simulator())

    fun part2(input: List<String>) = processPart(input, CrateMover9001Simulator())

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    val part1TestOutput = part1(testInput)
    val part1ExpectedOutput = "CMZ"
    check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
    val part2TestOutput = part2(testInput)
    val part2ExpectedOutput = "MCD"
    check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
