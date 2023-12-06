package day_05

import println
import readInput

@Suppress("unused")
private enum class Elements {
    SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
}

fun main() {
    data class AlmanacConvertor(val range: LongRange, val source: Long)

    /**
     * Create the converters by zipping all elements and create a list
     * of converter names similar to the ones in the input file.
     */
    fun getAllConverters(): List<String> {
        return Elements.entries.zipWithNext().map { pair ->
            "${pair.first.name.lowercase()}-to-${pair.second.name.lowercase()} map:"
        }
    }

    /**
     * Parse the input file and convert to AlmanacConvertors. Iterate
     * through the converter names and when one is found take all entries
     * until a blank line is found.
     */
    fun calculateConverters(input: List<String>): List<List<AlmanacConvertor>> {
        return getAllConverters().map { converter ->
            input.asSequence().dropWhile { it != converter }.drop(1).takeWhile { it.isNotBlank() }
                .map { it -> it.split(" ").mapNotNull { it.toLongOrNull() } }
                .map { AlmanacConvertor(LongRange(it[1], it[1] + it[2]), it[0]) }
                .sortedBy { it.range.first }
                .toList()
        }
    }

    /**
     * Converts an item using the almanac convertor, if no match is
     * found it will return the same value for the item.
     */
    fun runCategoryConverterForItem(item: Long, ranges: List<AlmanacConvertor>): Long {
        return ranges.firstOrNull { it.range.contains(item) }?.let { rangeMatch ->
            item + rangeMatch.source - rangeMatch.range.first
        } ?: item
    }

    /**
     * Get all initial seeds for part 1 with the input file.
     */
    fun getAllSeeds(input: List<String>): List<Long> {
        return input.first().split(" ").drop(1).mapNotNull { it.toLongOrNull() }
    }

    /**
     * Get all the seeds, convert them in each category, and
     * return the lowest number.
     */
    fun part1(input: List<String>): Int {
        return calculateConverters(input).let { allConverters ->
            getAllSeeds(input).minOf { seed ->
                allConverters.fold(seed) { item, categoryConverters ->
                    runCategoryConverterForItem(item, categoryConverters)
                }
            }
        }.toInt()
    }

    /**
     * Take all seeds from the input file and return as pairs.
     */
    fun getAllSeedRanges(input: List<String>): List<LongRange> {
        return getAllSeeds(input)
            .chunked(2)
            .map { LongRange(it.first(), it.first() + it.last()) }
    }

    /**
     * Same as part 1 but instead using a lot more seeds. This
     * solution does not brute force the answer, but checks
     * the ranges of the categories.
     */
    fun part2(input: List<String>): Int {
        return calculateConverters(input).fold(getAllSeedRanges(input)) { categoryRanges, converters ->
            categoryRanges.map { categoryRange ->
                // Keep track of the remainder to handle overlapping ranges
                var remainderRange = categoryRange

                // Filter all converters that affect the category range
                converters.filter {
                    it.range.contains(categoryRange.first) || it.range.contains(categoryRange.last)
                }.map { converter ->
                    // If the category range starts in converter
                    if (converter.range.contains(remainderRange.first)) {
                        val conversion = converter.source - converter.range.first
                        // If category range ends in converter
                        if (converter.range.contains(remainderRange.last)) {
                            LongRange(remainderRange.first + conversion, remainderRange.last + conversion).also {
                                remainderRange = LongRange.EMPTY // Reset
                            }
                        } else { // Category range is overlapping with next converter
                            LongRange(remainderRange.first + conversion, converter.range.last + conversion).also {
                                remainderRange = LongRange(converter.range.last, remainderRange.last)
                            }
                        }
                    } else remainderRange // Category range starts outside any converter
                }.let {
                    it + listOfNotNull(
                        // Category range ends outside any converter
                        if (!remainderRange.isEmpty()) remainderRange
                        // No converter found
                        else if (it.isEmpty()) categoryRange
                        else null
                    )
                }
            }.flatten()
        }.minOf { it.first }.toInt()
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_05/Day05_test")
    check(part1(testInputPart1) == 35)

    val testInputPart2 = readInput("day_05/Day05_test")
    check(part2(testInputPart2) == 46)

    val input = readInput("day_05/Day05")
    part1(input).println()
    part2(input).println()
}
