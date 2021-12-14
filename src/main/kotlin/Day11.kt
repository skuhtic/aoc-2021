import shared.readInput

const val EXPLOSION = 'X'
const val EXPLODED = '+'
const val FLASHING = '0'

class Board private constructor(private val data: CharArray, private val columns: Int, private val rows: Int) {

    private var _step = 0
    val step: Int get() = _step

    private var _explosions = 0
    val explosions: Int get() = _explosions

    constructor(input: List<String>) : this(
        input.joinToString("").toCharArray(),
        input.firstOrNull()?.length ?: 0,
        input.size
    ) {
        check(data.size == columns * rows)
    }

    private fun explode(index: Int) {
        check(data[index] == EXPLOSION)
        _explosions++
        data[index] = EXPLODED
        neighbours(index).forEach {
            data[it] = data[it].levelUp()
            if (data[it] == EXPLOSION) explode(it)
        }
    }

    fun step() {
        data.indices.forEach { i ->
            data[i] = data[i].levelUp()
            if (data[i] == EXPLOSION) explode(i)
        }
        data.indices.forEach { i -> if (data[i] == EXPLODED) data[i] = FLASHING }
        _step++
    }

    private fun neighbours(index: Int): List<Int> {
        val ret = mutableListOf<Int>()
        val x = index.rem(columns)
        val y = index.div(columns)
        if (x > 0) { // Not 1st column
            ret.add(index - 1) // left
            if (y > 0) ret.add(index - columns - 1) // left-up
            if (y < rows - 1) ret.add(index + columns - 1) // left-down
        }
        if (x < columns - 1) { // Not last column
            ret.add(index + 1) // right
            if (y > 0) ret.add(index + 1 - columns) // right-up
            if (y < rows - 1) ret.add(index + 1 + columns) // right-down
        }
        if (y > 0) ret.add(index - columns) // up
        if (y < rows - 1) ret.add(index + columns) // down
        return ret
    }

    fun isAllFlashing() = data.all { it == FLASHING }

    fun printToConsole() = println(this)

    override fun toString() = data.let {
        "Board after step $step ($_explosions explosions)" +
                it.joinToString("").chunked(columns).joinToString("\n", "\n", "\n")
    }
}

private fun Char.levelUp(): Char = when (this) {
    '0' -> '1'
    '1' -> '2'
    '2' -> '3'
    '3' -> '4'
    '4' -> '5'
    '5' -> '6'
    '6' -> '7'
    '7' -> '8'
    '8' -> '9'
    '9' -> EXPLOSION
    else -> this
}


fun main() {

    fun part1(input: List<String>): Int {
        val board = Board(input)
        board.printToConsole()
        repeat(100) {
            board.step()
            board.printToConsole()
        }
        return board.explosions
    }

    fun part2(input: List<String>): Int {
        val board = Board(input)
        board.printToConsole()
        do {
            board.step()
            board.printToConsole()
        } while (!board.isAllFlashing())
        return board.step
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))

}
