const val CARD_SIZE = 5


class Bingo constructor(input: List<String>) {
    private val draws = input[0].split(',').map { it.toInt() }
    var drownNumbersCount = 0
    val drownNumbers get() = draws.take(drownNumbersCount)
    var step = 0
    private val cardsCount = (input.size - 1) / (CARD_SIZE + 1)
    private val cards = mutableListOf<Card>()

    //    private val winners get() = cards.filter { it.isWinning() }
    val winOrder = mutableListOf<Card>()

    init {
        println("Reading $cardsCount cards...")
        repeat(cardsCount) {
            cards.add(Card(input, it))
        }
        println("Reading done...")
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
            println("Step: $step, drown: $drownNumbersCount" + drownNumbers.joinToString(", ", " (", ")"))
            println("Order: " + winOrder.joinToString(", ") { it.index.toString() })
            val unmarkedNumbers = cardScored.numbers.filterNot { drownNumbers.contains(it) }
            val unmarkedSum = unmarkedNumbers.sum()
            val lastDrownNumber = drownNumbers.last()
            return unmarkedSum * lastDrownNumber
        }

    }


    inner class Card constructor(input: List<String>, index: Int) {
        private val lines = mutableListOf<List<Int>>()
        val index = index
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
            println("Card:" + lines.joinToString("\n", "\n", "\n"))
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


fun main() {

    fun part1(input: List<String>) = with(Bingo(input)) {
        solve()
    }


    fun part2(input: List<String>) = with(Bingo(input)) {
        solve(true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))

}