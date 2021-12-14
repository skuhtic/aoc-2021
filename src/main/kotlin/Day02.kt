import shared.Aoc2021

fun main() {
    Day02.solve(bothParts = true, checkPart1 = 150, checkPart2 = 900)
}

object Day02 : Aoc2021(production = true, debug = false) {
    private val data = inputReader().readLines()

    override fun solvePart1(debug: Boolean) = data
        .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
        .fold(0 to 0) { depthDistance, instruction ->
            when (instruction.first) {
                "forward" -> depthDistance.first to depthDistance.second + instruction.second
                "down" -> depthDistance.first + instruction.second to depthDistance.second
                "up" -> depthDistance.first - instruction.second to depthDistance.second
                else -> throw IllegalArgumentException("Unknown instruction")
            }
        }.let { it.first * it.second }

    override fun solvePart2(debug: Boolean) = data
        .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
        .fold(Triple(0, 0, 0)) { aimDepthDistance, instruction ->
            when (instruction.first) {
                "forward" -> Triple(
                    aimDepthDistance.first,
                    aimDepthDistance.second + aimDepthDistance.first * instruction.second,
                    aimDepthDistance.third + instruction.second
                )
                "down" -> Triple(
                    aimDepthDistance.first + instruction.second,
                    aimDepthDistance.second,
                    aimDepthDistance.third
                )
                "up" -> Triple(
                    aimDepthDistance.first - instruction.second,
                    aimDepthDistance.second,
                    aimDepthDistance.third
                )
                else -> throw IllegalArgumentException("Unknown instruction")
            }.also { debugToConsole { it } }
        }.let { it.second * it.third }
}
