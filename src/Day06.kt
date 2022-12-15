fun main() {
    fun processPart(input: String, numDistintCharactersRequired: Int) =
        input.windowedSequence(numDistintCharactersRequired).mapIndexedNotNull { index, substring ->
            if (substring.toSet().size != numDistintCharactersRequired) null else index + numDistintCharactersRequired
        }.first()

    fun part1(input: String) = processPart(input, 4)

    fun part2(input: String) = processPart(input, 14)

    fun checkInputAgainstExpectedResults(input: String, part1Result: Int, part2Result: Int) {
        part1(input).let { check(it == part1Result) { "$input expected to equal part 1 result $part1Result, got $it" } }
        part2(input).let { check(it == part2Result) { "$input expected to equal part 2 result $part2Result, got $it" } }
    }

    // Test examples provided in description (no multiline file input)
    checkInputAgainstExpectedResults("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 7, 19)
    checkInputAgainstExpectedResults("bvwbjplbgvbhsrlpgdmjqwftvncz", 5, 23)
    checkInputAgainstExpectedResults("nppdvjthqldpwncqszvftbrmjlhg", 6, 23)
    checkInputAgainstExpectedResults("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 10, 29)
    checkInputAgainstExpectedResults("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 11, 26)

    // Input is a single line for this Day
    val input = readInput("Day06")[0]
    println(part1(input))
    println(part2(input))
}