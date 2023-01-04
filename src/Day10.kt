interface VideoSystemCPUObserver {
    fun notify(cycleNumber: Int, xRegisterValue: Int)
}

class VideoSystemCPU(programInput: List<String>, private val observer: VideoSystemCPUObserver) {
    private var xRegister = 1
    private var currentCycleNumber = 0
    private var currentInstruction: Instruction? = null
    private val program =
        programInput.asSequence().filter(String::isNotBlank).map { Instruction.fromString(it) }.iterator()

    fun runToCompletion() {
        while (runCycle()) {
        }
    }

    private fun runCycle(): Boolean {
        currentCycleNumber++
        if (currentInstruction == null || currentInstruction!!.hasCompleted()) {
            if (!program.hasNext()) {
                return false
            }
            currentInstruction = program.next()
        }
        observer.notify(currentCycleNumber, xRegister)

        currentInstruction?.let {
            if (it.onLastCycle()) {
                if (it is AddxInstruction) {
                    xRegister += it.increment
                }
            }
            it.incrementCycle()
        }

        return true
    }

    abstract class Instruction {
        abstract val numCyclesToComplete: Int
        private var cycleCounter = 0

        fun hasCompleted() = cycleCounter == numCyclesToComplete

        fun onLastCycle() = cycleCounter + 1 == numCyclesToComplete

        fun incrementCycle() {
            cycleCounter++
        }

        companion object {
            fun fromString(input: String) = input.split(" ").let {
                when (it[0]) {
                    "noop" -> NoopInstruction()
                    "addx" -> AddxInstruction(it[1].toInt())
                    else -> throw IllegalArgumentException("Unknown instruction provided: $it")
                }
            }
        }
    }

    class NoopInstruction : Instruction() {
        override val numCyclesToComplete: Int
            get() = 1
    }

    class AddxInstruction(val increment: Int) : Instruction() {
        override val numCyclesToComplete: Int
            get() = 2
    }
}

class SignalStrengthSampler : VideoSystemCPUObserver {
    private val signalStrengthRecords = mutableListOf<SignalStrengthRecord>()
    val signalStrengthSum
        get() = signalStrengthRecords.sumOf { it.signalStrength }.also { println(signalStrengthRecords) }

    override fun notify(cycleNumber: Int, xRegisterValue: Int) {
        if ((cycleNumber + CYCLE_NUMBER_OFFSET) % SAMPLE_EVERY_THIS_MANY_CYCLES == 0) {
            signalStrengthRecords.add(SignalStrengthRecord(cycleNumber, xRegisterValue))
        }
    }

    data class SignalStrengthRecord(val cycleNumber: Int, val xRegisterValue: Int) {
        val signalStrength
            get() = cycleNumber * xRegisterValue
    }

    companion object {
        private const val CYCLE_NUMBER_OFFSET = 20
        private const val SAMPLE_EVERY_THIS_MANY_CYCLES = 40
    }
}

class CRT : VideoSystemCPUObserver {
    private val screen = CharArray(NUM_PIXELS_ON_SCREEN)

    override fun notify(cycleNumber: Int, xRegisterValue: Int) {
        val position = cycleNumber - 1
        screen[position] = if (position % PIXELS_PER_ROW in xRegisterValue - 1..xRegisterValue + 1) {
            '#'
        } else {
            '.'
        }
    }

    override fun toString(): String {
        return screen.asSequence().windowed(PIXELS_PER_ROW, PIXELS_PER_ROW).map { it.joinToString("") }
            .joinToString("\n")
    }

    companion object {
        private const val NUM_PIXELS_ON_SCREEN = 240
        private const val PIXELS_PER_ROW = 40
    }
}

fun main() {
    fun part1(input: List<String>) = SignalStrengthSampler().let {
        VideoSystemCPU(input, it).runToCompletion()
        it.signalStrengthSum
    }

    fun part2(input: List<String>) = CRT().let {
        VideoSystemCPU(input, it).runToCompletion()
        it.toString()
    }

    readInput("Day10_test").let {
        val part1TestOutput = part1(it)
        val part1ExpectedOutput = 13140
        check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
        val part2TestOutput = part2(it)
        val part2ExpectedOutput = """
            ##..##..##..##..##..##..##..##..##..##..
            ###...###...###...###...###...###...###.
            ####....####....####....####....####....
            #####.....#####.....#####.....#####.....
            ######......######......######......####
            #######.......#######.......#######.....""".trimIndent()
        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}