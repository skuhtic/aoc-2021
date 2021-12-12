fun main() {

    fun part1(input: List<String>) = input
        .map { it.substringBefore(' ') to it.substringAfter(' ').toInt() }
        .fold(0 to 0) { depthDistance, instruction ->
            when (instruction.first) {
                "forward" -> depthDistance.first to depthDistance.second + instruction.second
                "down" -> depthDistance.first + instruction.second to depthDistance.second
                "up" -> depthDistance.first - instruction.second to depthDistance.second
                else -> throw IllegalArgumentException("Unknown instruction")
            }
        }.let { it.first * it.second }

    fun part2(input: List<String>) = input
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
            }.also { println(it) }
        }.let { it.second * it.third }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))

}