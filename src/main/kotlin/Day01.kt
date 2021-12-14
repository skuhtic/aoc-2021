import shared.Aoc2021

fun main() {
    Day01.solve(bothParts = true, checkPart1 = 7, checkPart2 = 5)
}

object Day01 : Aoc2021(production = true, debug = false) {
    private val data = inputReader().readLines().map { it.toInt() }

    override fun solvePart1(debug: Boolean) = data
        .zipWithNext { a, b -> a < b }
        .count { it }

    override fun solvePart2(debug: Boolean) = data
        .windowed(3) { it.sum() }
        .zipWithNext { a, b -> a < b }
        .count { it }
}
