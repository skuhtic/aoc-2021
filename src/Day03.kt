fun main() {

    fun part1(input: List<String>): Int {
        var gamma = 0
        var epsilon = 0
        val chars = input.map { it.toCharArray() }
        val half = chars.size / 2
        repeat(chars[0].size) { index ->
            gamma *= 2
            epsilon *= 2
            if (chars.count { it[index] == '1' } > half)
                gamma++
            else
                epsilon++
        }
        return gamma * epsilon
    }


    fun List<CharArray>.filterOxygen(index: Int) = if (this.size == 1)
        this
    else
        partition { it[index] == '1' }.let {
            when {
                it.first.size > it.second.size -> it.first
                it.first.size < it.second.size -> it.second
                else -> it.first
            }
        }

    fun List<CharArray>.filterCO2(index: Int) = if (this.size == 1)
        this
    else
        partition { it[index] == '0' }.let {
            when {
                it.first.size < it.second.size -> it.first
                it.first.size > it.second.size -> it.second
                else -> it.first
            }
        }

    fun part2(input: List<String>): Int {
        var oxygenList = input.map { it.toCharArray() }
        repeat(oxygenList[0].size) { index ->
            oxygenList = oxygenList.filterOxygen(index)
        }
        check(oxygenList.size == 1)
        val oxygen = oxygenList[0].joinToString("").toInt(2)

        var co2List = input.map { it.toCharArray() }
        repeat(co2List[0].size) { index ->
            co2List = co2List.filterCO2(index)
        }
        check(co2List.size == 1)
        val co2 = co2List[0].joinToString("").toInt(2)

        return oxygen * co2
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))

}