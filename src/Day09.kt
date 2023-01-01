import kotlin.math.abs

class HeadMotionParser {
    companion object {
        fun parse(input: List<String>) =
            input.asSequence().filter { it.isNotBlank() }.map(HeadMotionInstruction::fromString)
                .flatMap { it.iterator().asSequence() }
    }
}

data class HeadMotionInstruction(val direction: HeadMotionDirection, val steps: Int) : Iterable<HeadMotionDirection> {
    companion object {
        fun fromString(input: String) =
            input.split(" ").let {
                require(it.size == 2)
                HeadMotionInstruction(HeadMotionDirection.fromString(it[0]), it[1].toInt())
            }
    }

    override fun iterator() = StepIterator(this)

    class StepIterator(private val instruction: HeadMotionInstruction) : Iterator<HeadMotionDirection> {
        var currentStep: Int = 0

        override fun hasNext() = currentStep < instruction.steps

        override fun next() = instruction.direction.also { currentStep++ }
    }
}

enum class HeadMotionDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    companion object {
        fun fromString(input: String) = when (input) {
            "L" -> LEFT
            "R" -> RIGHT
            "U" -> UP
            "D" -> DOWN
            else -> throw IllegalArgumentException("Unknown direction string: $input")
        }
    }
}

class RopeModeler(numKnots: Int) {
    private val positionsVisitedByTail = mutableSetOf<Position>()
    private val knotPositions = Array(numKnots) { Position.default() }
    private val knotPositionsIndexWindows = knotPositions.indices.windowed(2, 1)
    private var currentHeadPosition
        get() = knotPositions.first()
        set(value) {
            knotPositions[0] = value
        }
    private val currentTailPosition
        get() = knotPositions.last()
    val numPositionsVisitedByTail
        get() = positionsVisitedByTail.size

    init {
        require(numKnots > 1)
        recordCurrentTailPosition()
    }

    fun step(direction: HeadMotionDirection) {
        moveHead(direction)
        knotPositionsIndexWindows.forEach {
            val (priorPositionIndex, currentPositionIndex) = it
            knotPositions[currentPositionIndex] = calculateNewKnotPosition(
                knotPositions[currentPositionIndex], knotPositions[priorPositionIndex]
            )
        }
        recordCurrentTailPosition()
        // println("After step $direction, positions are: [${knotPositions.joinToString(", ")}]")
    }

    private fun moveHead(direction: HeadMotionDirection) {
        currentHeadPosition = when (direction) {
            HeadMotionDirection.LEFT -> currentHeadPosition.shiftXBy(-1)
            HeadMotionDirection.RIGHT -> currentHeadPosition.shiftXBy(1)
            HeadMotionDirection.UP -> currentHeadPosition.shiftYBy(1)
            HeadMotionDirection.DOWN -> currentHeadPosition.shiftYBy(-1)
        }
    }

    private fun calculateNewKnotPosition(currentKnotPosition: Position, priorKnotPosition: Position): Position {
        val diff = priorKnotPosition - currentKnotPosition
        if (diff.xDistance <= 1 && diff.yDistance <= 1) {
            // Current knot is either overlapping or right next to the prior knot, don't update position
            return currentKnotPosition
        }
        return currentKnotPosition.shiftEachDimensionByAtMostOne(diff)
    }

    private fun recordCurrentTailPosition() {
        positionsVisitedByTail.add(currentTailPosition)
    }

    data class Position(val x: Int, val y: Int) {
        fun shiftXBy(num: Int) = Position(this.x + num, this.y)

        fun shiftYBy(num: Int) = Position(this.x, this.y + num)

        fun shiftEachDimensionByAtMostOne(diff: PositionDifference) = Position(
            this.x + diff.x.compareTo(0),
            this.y + diff.y.compareTo(0)
        )

        operator fun minus(other: Position) = PositionDifference(this.x - other.x, this.y - other.y)

        companion object {
            fun default() = Position(0, 0)
        }
    }

    data class PositionDifference(val x: Int, val y: Int) {
        val xDistance
            get() = abs(x)
        val yDistance
            get() = abs(y)
    }
}

fun main() {
    fun processPart(input: List<String>, numKnots: Int) =
        RopeModeler(numKnots).let { modeler ->
            HeadMotionParser.parse(input).forEach { modeler.step(it) }
            modeler.numPositionsVisitedByTail
        }

    fun part1(input: List<String>) = processPart(input, 2)

    fun part2(input: List<String>) = processPart(input, 10)

    readInput("Day09_test").let {
        val part1TestOutput = part1(it)
        val part1ExpectedOutput = 13
        check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
        val part2TestOutput = part2(it)
        val part2ExpectedOutput = 1
        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }
    readInput("Day09_test2").let {
        val part2TestOutput = part2(it)
        val part2ExpectedOutput = 36
        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
