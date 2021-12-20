package day04

import shared.Aoc2021
import java.io.InputStreamReader

const val CARD_SIZE = 5

fun main() = with(Day04) { // checkPart1 = 4512, checkPart2 = 1924
    testPart1(checkResult = 4512)
    testPart2(checkResult = 1924)
    runPart1()
    runPart2()
}

object Day04 : Aoc2021(debug = false) {
    override fun solutionPart1(inputReader: InputStreamReader): Any = Bingo(inputReader.readLines()).solve()
    override fun solutionPart2(inputReader: InputStreamReader): Any = Bingo(inputReader.readLines()).solve(true)


    class Bingo constructor(input: List<String>) {
        private val draws = input[0].split(',').map { it.toInt() }
        private var drownNumbersCount = 0
        private var step = 0
        private val cardsCount = (input.size - 1) / (CARD_SIZE + 1)
        private val cards = mutableListOf<Card>()
        private val winOrder = mutableListOf<Card>()

        val drownNumbers get() = draws.take(drownNumbersCount)

        init {
            repeat(cardsCount) { cards.add(Card(input, it)) }
        }

        private fun step() {
            drownNumbersCount++
            step++
        }

        fun solve(last: Boolean = false): Int {
            do {
                step()
                cards.filterNot { it.winner }.forEach { card ->
                    if (card.isWinning()) {
                        card.winner = true
                        winOrder.add(card)
                    }
                }
                val run = if (last) cards.count() != winOrder.count() else winOrder.isEmpty()
            } while (run)

            if (last) {
                winOrder.last()
            } else {
                winOrder.first()
            }.let { cardScored ->
                debug {  "Step: $step, drown: $drownNumbersCount" + drownNumbers.joinToString(", ", " (", ")")}
                debug {"Order: " + winOrder.joinToString(", ") { it.index.toString() }}
                val unmarkedNumbers = cardScored.numbers.filterNot { drownNumbers.contains(it) }
                val unmarkedSum = unmarkedNumbers.sum()
                val lastDrownNumber = drownNumbers.last()
                return unmarkedSum * lastDrownNumber
            }

        }

        inner class Card constructor(input: List<String>, val index: Int) {
            private val lines = mutableListOf<List<Int>>()
            val numbers = mutableListOf<Int>()
            var winner = false

            init {
                val startLine = index * 6 + 2
                repeat(5) { lineOffset ->
                    val test = input[startLine + lineOffset].split(" ").filterNot { it.isBlank() }
                    val test2 = test.map { it.toInt() }
                    lines.add(test2)
                    numbers.addAll(test2)
                }
                check(lines.size == 5)
                repeat(5) {
                    lines.add(listOf(lines[0][it], lines[1][it], lines[2][it], lines[3][it], lines[4][it]))
                }
                check(lines.size == 10)
                debug{"Card:" + lines.joinToString("\n", "\n", "\n")}
            }

            fun isWinning() = drownNumbers.let { drown ->
                this.lines.forEach { line ->
                    if (drown.containsAll(line))
                        return@let true
                }
                false
            }
        }
    }
}