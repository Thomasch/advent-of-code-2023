package day_07

import println
import readInput

private enum class HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
}

fun main() {
    data class CardValue(val card: List<Int>, val type: HandType, val bid: Int)

    val jokerValue = 13

    /**
     * Map of all the cards and its values.
     */
    fun getCardValues(useJoker: Boolean): Map<Char, Int> {
        return mapOf('A' to 0, 'K' to 1, 'Q' to 2, 'J' to if (useJoker) jokerValue else 3, 'T' to 4) +
                List(8) { (9 - it).digitToChar() to it + 5 }
    }

    /**
     * Apply all Jokers to the biggest group in the card and return it.
     */
    fun applyJokers(card: List<Int>): List<Int> {
        return card.filterNot { it == jokerValue }.let { nonJokerCards ->
            nonJokerCards + List(card.size - nonJokerCards.size) {
                nonJokerCards.groupBy { it }.let { group ->
                    if (group.isEmpty()) card.first() else group.maxBy { it.value.size }.key
                }
            }
        }
    }

    /**
     * Convert a card into a HandType by checking the size of
     * the biggest group. Also converts jokers if they exist.
     */
    fun getCardValue(card: List<Int>): HandType {
        return applyJokers(card).groupBy { it }.let { groups ->
            groups.firstNotNullOfOrNull { if (it.value.size >= 4) it else null }?.let {
                when (it.value.size) {
                    5 -> HandType.FIVE_OF_A_KIND
                    4 -> HandType.FOUR_OF_A_KIND
                    else -> null
                }
            } ?: groups.firstNotNullOfOrNull { if (it.value.size == 3) it else null }?.let {
                when (groups.size) {
                    2 -> HandType.FULL_HOUSE
                    3 -> HandType.THREE_OF_A_KIND
                    else -> null
                }
            } ?: groups.filter { it.value.size == 2 }.let {
                when (it.values.size) {
                    2 -> HandType.TWO_PAIR
                    1 -> HandType.ONE_PAIR
                    else -> HandType.HIGH_CARD
                }
            }
        }
    }

    /**
     * Convert the input file to cards and bids, get the card
     * value and sort by the HandType and then card numbers.
     * Returns the sum of the bid times sorting position.
     */
    fun calculateTotalWinnings(input: List<String>, useJoker: Boolean): Int {
        return getCardValues(useJoker).let { cardValues ->
            input
                .map { line ->
                    line.split(" ").let { split ->
                        split.first().map { cardValues.getValue(it) } to
                                split.last().toInt()
                    }
                }
                .map { CardValue(it.first, getCardValue(it.first), it.second) }
                .sortedWith(compareBy(
                    { it.type }, { it.card[0] }, { it.card[1] },
                    { it.card[2] }, { it.card[3] }, { it.card[4] }
                ))
                .reversed()
                .mapIndexed { index, value -> (index + 1) * value.bid }
                .sum()
        }
    }

    fun part1(input: List<String>): Int {
        return calculateTotalWinnings(input, false)
    }

    fun part2(input: List<String>): Int {
        return calculateTotalWinnings(input, true)
    }

    // test if implementation meets criteria from the description, like:
    val testInputPart1 = readInput("day_07/Day07_test")
    check(part1(testInputPart1) == 6440)

    val testInputPart2 = readInput("day_07/Day07_test")
    check(part2(testInputPart2) == 5905)

    val input = readInput("day_07/Day07")
    part1(input).println()
    part2(input).println()
}
