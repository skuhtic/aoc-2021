import shared.Aoc2021


fun main() {
    Day14.solve(bothParts = true, checkPart1 = 1588, checkPart2 = 2188189693529)
}

object Day14 : Aoc2021(production = false, debug = true) {
    private val inputData: String
    private val rules: Map<String, String>

    init {
        inputReader().buffered().use { reader ->
            inputData = reader.readLine()
            check(reader.readLine().isEmpty()) { "Input not formatted correctly" }
            rules = mutableMapOf<String, String>().apply {
                reader.readLines().map { line ->
                    line.split(" -> ").let {
                        check(put(it[0], "${it[0][0]}${it[1][0]}") == null)
                    }
                }
            }
        }
    }

    private var data = inputData
    private var step: Int = 0

    private fun step() {
        debugToConsole { "Step ${++step}" }
        data = data.windowed(2, 1, true).map {
            rules[it] ?: it[0]
        }.joinToString("")
    }

    private val result
        get() = data.groupingBy { it }.eachCount().let { counts ->
            (counts.maxByOrNull { it.value }?.value ?: 0) - (counts.minByOrNull { it.value }?.value ?: 0)
        }

    override fun solvePart1(debug: Boolean): Int {
        repeat(10) { step() }
        return result
    }

    override fun solvePart2(debug: Boolean): Int {
        repeat(10) { step() }  // 40 times -> 10 from previous part + 30 more
        return result
    }
}
