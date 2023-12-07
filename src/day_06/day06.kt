package day_06

import println
import readInput

fun main() {
    /**
     * Calculates if a single entry beats the record
     */
    fun beatsRecord(seconds: Long, maxTime: Long, recordDistance: Long): Boolean {
        return (maxTime - seconds) * seconds > recordDistance
    }

    /**
     * Return the range of all possible records
     */
    fun calculateRangeForRecords(maxTime: Long, recordDistance: Long): LongRange {
        return (1..<maxTime).let { range ->
            range.first { beatsRecord(it, maxTime, recordDistance) }
                .rangeTo(range.last { beatsRecord(it, maxTime, recordDistance) })
        }
    }

    /**
     * Your toy boat has a starting speed of zero millimeters per millisecond.
     * For each whole millisecond you spend at the beginning of the race
     * holding down the button, the boat's speed increases by one millimeter
     * per millisecond. Determine the number of ways you can beat the record
     * in each race.
     */
    fun part1(input: List<String>): Int {
        return input
            .map { line -> line.split(" ").drop(1).filterNot { it.isBlank() }.map { it.toLong() } }
            .let { it.first().zip(it.last()) }
            .map { calculateRangeForRecords(it.first, it.second).count() }
            .reduce(Int::times)
    }

    /**
     * There's really only one race - ignore the spaces between the numbers on each line.
     */
    fun part2(input: List<String>): Int {
        return input.map { line -> line.filter { it.isDigit() }.toLong() }
            .let { calculateRangeForRecords(it.first(), it.last()).count() }
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_06/Day06_test")
    check(part1(testInputPart1) == 288)

    val testInputPart2 = readInput("day_06/Day06_test")
    check(part2(testInputPart2) == 71503)

    val input = readInput("day_06/Day06")
    part1(input).println()
    part2(input).println()
}
