import java.util.*

class File(val name: String, val size: Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as File
        if (name != other.name) return false
        return true
    }

    override fun hashCode() = name.hashCode()
}

class Directory(val name: String) {
    private val files: MutableSet<File> = mutableSetOf()
    private val childDirectories: MutableSet<Directory> = mutableSetOf()
    private var cachedSizeComputation: Long? = null

    val size: Long
        get() = cachedSizeComputation ?: files.sumOf { it.size }
            .plus(childDirectories.sumOf { it.size }).also { cachedSizeComputation = it }

    fun addChildDirectory(childDirectoryName: String) =
        childDirectories.add(Directory(childDirectoryName)).also { cachedSizeComputation = null }

    fun addFile(file: File) = files.add(file).also { cachedSizeComputation = null }

    fun getChildDirectory(childDirectoryName: String) = childDirectories.find { it.name == childDirectoryName }

    fun walkSequence(): Sequence<Directory> = sequence {
        yield(this@Directory)
        childDirectories.forEach { yieldAll(it.walkSequence()) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Directory
        if (name != other.name) return false
        return true
    }

    override fun hashCode() = name.hashCode()

    companion object {
        private const val ROOT_DIRECTORY_NAME = "/"

        fun isRoot(directoryName: String) = ROOT_DIRECTORY_NAME == directoryName

        fun makeRoot() = Directory(ROOT_DIRECTORY_NAME)
    }
}

class Filesystem(private val root: Directory = Directory.makeRoot()) {
    val unusedSpaceSize
        get() = TOTAL_DISK_SIZE - root.size

    fun populateFromTerminalOutput(lines: List<String>): Filesystem {
        val traversalStack = Stack<Directory>()
        lines.asSequence()
            .filter { it.isNotBlank() }
            .map { it.split(" ") }
            .forEach {
                if (it[0] == PROMPT_MARKER) {
                    handleCommandLine(it[1], it.getOrNull(2), traversalStack)
                } else if (it[0] == DIRECTORY_INDICATOR) {
                    traversalStack.peek().addChildDirectory(it[1])
                } else {
                    traversalStack.peek().addFile(File(it[1], it[0].toLong()))
                }
            }
        return this
    }

    fun walkSequence() = root.walkSequence()

    private fun handleCommandLine(command: String, arg: String?, traversalStack: Stack<Directory>) {
        // The only handled command is "cd"
        if (command != CHANGE_DIRECTORY_COMMAND) return
        // arg must be non-null for the "cd" command
        handleChangeDirectory(arg!!, traversalStack)
    }

    private fun handleChangeDirectory(directoryName: String, traversalStack: Stack<Directory>) {
        if (directoryName == GO_UP_ONE_LEVEL_DIRECTORY_NAME) {
            traversalStack.pop()
        } else if (Directory.isRoot(directoryName)) {
            traversalStack.clear()
            traversalStack.push(root)
        } else {
            traversalStack.push(traversalStack.peek().getChildDirectory(directoryName))
        }
    }

    companion object {
        private const val PROMPT_MARKER = "$"
        private const val GO_UP_ONE_LEVEL_DIRECTORY_NAME = ".."
        private const val CHANGE_DIRECTORY_COMMAND = "cd"
        private const val DIRECTORY_INDICATOR = "dir"
        private const val TOTAL_DISK_SIZE = 70000000L
    }
}

fun main() {
    fun part1(filesystem: Filesystem) =
        filesystem.walkSequence()
            .map { it.size }
            .filter { it <= 100000 }
            .sum()

    fun part2(filesystem: Filesystem): Long {
        val freeUpAtLeastSize = 30000000 - filesystem.unusedSpaceSize
        return filesystem.walkSequence()
            .map { it.size }
            .filter { it >= freeUpAtLeastSize }
            .min()
    }

    readInput("Day07_test").also { input ->
        val filesystem = Filesystem().populateFromTerminalOutput(input)
        val part1TestOutput = part1(filesystem)
        val part1ExpectedOutput = 95437L
        check(part1TestOutput == part1ExpectedOutput) { "$part1TestOutput != $part1ExpectedOutput" }
        val part2TestOutput = part2(filesystem)
        val part2ExpectedOutput = 24933642L
        check(part2TestOutput == part2ExpectedOutput) { "$part2TestOutput != $part2ExpectedOutput" }
    }

    readInput("Day07").also {
        val filesystem = Filesystem().populateFromTerminalOutput(it)
        println(part1(filesystem))
        println(part2(filesystem))
    }
}
