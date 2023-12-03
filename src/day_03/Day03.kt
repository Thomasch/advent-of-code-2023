package day_03

import println
import readInput

fun main() {
    data class PartNumber(val number: Int, val indices: List<Int>)

    /**
     * It's probably more efficient to iterate through the engine schematic once
     * instead of twice, but this was easier. This method returns all part numbers
     * and its indices.
     */
    fun getAllPartNumbers(input: List<String>): List<PartNumber> {
        return input.mapIndexed { lineIndex, line ->
            var currentPartNumber = ""
            var currentIndices = mutableListOf<Int>()
            line.mapIndexedNotNull { charIndex, char ->
                val index = lineIndex * line.length + charIndex
                if (char.isDigit()) {
                    currentPartNumber += char
                    currentIndices += index
                }
                // Return part number if end of line or end of digit is found
                if ((char.isDigit() && line.length == charIndex + 1) ||
                    (!char.isDigit() && currentPartNumber.isNotEmpty())
                ) {
                    PartNumber(currentPartNumber.toInt(), currentIndices).also {
                        currentPartNumber = ""
                        currentIndices = mutableListOf()
                    }
                } else null
            }
        }.flatten()
    }

    /**
     * The engine schematic (your puzzle input) consists of a visual
     * representation of the engine. There are lots of numbers and symbols you
     * don't really understand, but apparently any number adjacent to a symbol,
     * even diagonally, is a "part number" and should be included in your sum.
     * (Periods (.) do not count as a symbol.) What is the sum of all the
     * part numbers in the engine schematic?
     *
     * In this solution I'm checking all neighboring characters for a digit when
     * a symbol is found. I gather the found part numbers and make it unique by
     * creating a set. Then I return the sum of the part numbers.
     */
    fun part1(input: List<String>): Int {
        val allPartNumbers = getAllPartNumbers(input)
        return input.mapIndexed { lineIndex, line ->
            line.mapIndexedNotNull { charIndex, char ->
                // Check for a symbol that's not a period
                if (!char.isDigit() && char != '.') {
                    // Check neighboring characters for digits, index 4 is not a neighbor so will be ignored
                    List(9) { neighborIndex ->
                        if (input.getOrNull(lineIndex + neighborIndex.floorDiv(3) - 1)
                                ?.getOrNull(charIndex + neighborIndex % 3 - 1)?.isDigit() == true
                        ) {
                            // Search for index and return found part number
                            allPartNumbers.first {
                                (lineIndex + neighborIndex.floorDiv(3) - 1) * line.length +
                                        (charIndex + neighborIndex % 3 - 1) in it.indices
                            }
                        } else null
                    }
                } else null
            }.flatten().filterNotNull()
        }.flatten().toSet().sumOf { it.number }
    }

    /**
     * The missing part wasn't the only issue - one of the gears in the engine is
     * wrong. A gear is any * symbol that is adjacent to exactly two part numbers.
     * Its gear ratio is the result of multiplying those two numbers together.
     * This time, you need to find the gear ratio of every gear and add them all
     * up so that the engineer can figure out which gear needs to be replaced.
     *
     * Similar approach to part 1, I'm checking all neighboring characters for at
     * least two digits when an asterisk is found. When two part numbers are found
     * I make them unique by creating a set. The gear ratio is calculated by multiplying
     * the part numbers. Then I return the sum of the gear ratios.
     */
    fun part2(input: List<String>): Int {
        val allPartNumbers = getAllPartNumbers(input)
        val items = input.mapIndexed { lineIndex, line ->
            line.mapIndexedNotNull { charIndex, char ->
                if (char == '*') {
                    // Check neighboring characters for digits, index 4 is not a neighbor so will be ignored
                    val partNumbers = List(9) { repeatIndex ->
                        if (input.getOrNull(lineIndex + repeatIndex.floorDiv(3) - 1)
                                ?.getOrNull(charIndex + repeatIndex % 3 - 1)?.isDigit() == true
                        ) {
                            // Search for index and return found part number
                            allPartNumbers.first {
                                (lineIndex + repeatIndex.floorDiv(3) - 1) * line.length +
                                        (charIndex + repeatIndex % 3 - 1) in it.indices
                            }
                        } else null
                    }.filterNotNull().toSet()
                    // Look for multiple matches for the gear ratio
                    if (partNumbers.size > 1) {
                        // Calculate the gear ratio by multiplying part numbers
                        partNumbers.map { it.number }.reduce(Int::times)
                    } else null
                } else null
            }
        }
        return items.flatten().sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_03/Day03_test")
    check(part1(testInputPart1) == 4361)

    val testInputPart2 = readInput("day_03/Day03_test")
    check(part2(testInputPart2) == 467835)

    val input = readInput("day_03/Day03")
    part1(input).println()
    part2(input).println()
}
