package day_02

import println
import readInput
import kotlin.math.max

fun main() {
    data class Cubes(val red: Int, val green: Int, val blue: Int)

    data class Game(val id: Int, val cubes: Cubes)

    /**
     * Parse a line of a game, extract the number of cubes for each color and
     * reduce the highest possible number of cubes
     */
    fun calculateMaxCubes(line: String): Game {
        return line.split(":").let { colon ->
            Game(
                id = colon.first().filter { it.isDigit() }.toInt(),
                cubes = colon.last().split(";").map { it ->
                    it.split(",").let { subset ->
                        listOf("red", "green", "blue").map { color ->
                            subset
                                .firstOrNull { color in it }
                                ?.filter { it.isDigit() }
                                ?.toIntOrNull() ?: 0
                        }.let { Cubes(it[0], it[1], it[2]) }
                    }
                }.reduce { l, r ->
                    Cubes(max(l.red, r.red), max(l.green, r.green), max(l.blue, r.blue))
                }
            )
        }
    }

    /**
     * Determine which games would have been possible if the bag had been loaded
     * with only 12 red cubes, 13 green cubes, and 14 blue cubes. What is the sum
     * of the IDs of those games?
     */
    fun part1(input: List<String>): Int {
        return Cubes(red = 12, green = 13, blue = 14).let { condition ->
            input.map { calculateMaxCubes(it) }.filter { game ->
                game.cubes.red <= condition.red &&
                        game.cubes.green <= condition.green &&
                        game.cubes.blue <= condition.blue
            }.sumOf { it.id }
        }
    }

    /**
     * For each game, find the minimum set of cubes that must have been present.
     * What is the sum of the power of these sets?
     */
    fun part2(input: List<String>): Int {
        return input.sumOf {
            calculateMaxCubes(it).let { game ->
                game.cubes.red * game.cubes.green * game.cubes.blue
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_02/Day02_test")
    check(part1(testInputPart1) == 8)

    val testInputPart2 = readInput("day_02/Day02_test")
    check(part2(testInputPart2) == 2286)

    val input = readInput("day_02/Day02")
    part1(input).println()
    part2(input).println()
}
