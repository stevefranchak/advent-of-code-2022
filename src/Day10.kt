class VideoSystemCPU {
    companion object {
        const val NEXT_CYCLE_NUMBER_TO_RECORD_INCREMENT = 40
    }

    private var xRegister: Int = 1
    private var completedCycles: Int = 0
    private val signalStrengthRecords = mutableListOf<SignalStrengthRecord>()
    private var nextCycleNumberToRecord = 20

    val signalStrengthSum
        get() = signalStrengthRecords.sumOf { it.signalStrength }.also { println(signalStrengthRecords) }

    fun execute(input: List<String>): VideoSystemCPU {
        input.asSequence().filter(String::isNotBlank).forEach { execute(Instruction.fromString(it)) }
        return this
    }

    private fun execute(instruction: Instruction) {
        completedCycles += instruction.numCycles
        recordSignalStrengthRecordBasedOnCycle()
        if (instruction is AddxInstruction) {
            xRegister += instruction.increment
        }
    }

    private fun recordSignalStrengthRecordBasedOnCycle() {
        if (completedCycles >= nextCycleNumberToRecord) {
            signalStrengthRecords.add(SignalStrengthRecord(nextCycleNumberToRecord, xRegister))
            nextCycleNumberToRecord += NEXT_CYCLE_NUMBER_TO_RECORD_INCREMENT
        }
    }

    abstract class Instruction {
        abstract val numCycles: Int

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
        override val numCycles: Int
            get() = 1
    }

    class AddxInstruction(val increment: Int) : Instruction() {
        override val numCycles: Int
            get() = 2
    }

    data class SignalStrengthRecord(val cycleNumber: Int, val xRegisterValue: Int) {
        val signalStrength
            get() = cycleNumber * xRegisterValue
    }
}


fun main() {
    fun part1(input: List<String>) = VideoSystemCPU().execute(input).signalStrengthSum

    fun part2(input: List<String>) = ""

    readInput("Day10_test").let {
        val part1TestOutput = part1(it)
        val part1ExpectedOutput = 13140
        check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
//        val part2TestOutput = part2(it)
//        val part2ExpectedOutput =
//            """##..##..##..##..##..##..##..##..##..##..
//            ###...###...###...###...###...###...###.
//            ####....####....####....####....####....
//            #####.....#####.....#####.....#####.....
//            ######......######......######......####
//            #######.......#######.......#######.....""".trimIndent()
//        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}