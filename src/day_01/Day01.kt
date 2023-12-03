package day_01

import findAllWithOverlap
import println
import readInput

fun main() {
    /**
     * The newly-improved calibration document consists of lines of text; each
     * line originally contained a specific calibration value that the Elves now
     * need to recover. On each line, the calibration value can be found by
     * combining the first digit and the last digit (in that order) to form a
     * single two-digit number.
     *
     * This solution concatenates the first and last found digit as a String
     */
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            if (line.any { char -> char.isDigit() }) {
                val first = line.first { first -> first.isDigit() }
                val last = line.last { last -> last.isDigit() }
                "$first$last".toInt()
            } else 0
        }
    }

    /**
     * Your calculation isn't quite right. It looks like some of the digits are
     * actually spelled out with letters: one, two, three, four, five, six, seven,
     * eight, and nine also count as valid "digits".
     *
     * This solution using Regex to find all matches using an extension function to
     * find overlapping matches. If the match is a written out digit I check the index
     * from the list and else I use the found digit.
     */
    fun part2(input: List<String>): Int {
        val numbers = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        val regex = Regex("\\d|${numbers.joinToString(separator = "|")}")
        return input.sumOf { line ->
            regex.findAllWithOverlap(line).let { matches ->
                matches.first().value.let { first ->
                    if (numbers.contains(first)) (numbers.indexOf(first) + 1).toString() else first
                } + matches.last().value.let { last ->
                    if (numbers.contains(last)) (numbers.indexOf(last) + 1).toString() else last
                }
            }.toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_01/Day01_test1")
    check(part1(testInputPart1) == 142)

    val testInputPart2 = readInput("day_01/Day01_test2")
    check(part2(testInputPart2) == 281)

    val input = readInput("day_01/Day01")
    part1(input).println()
    part2(input).println()
}
