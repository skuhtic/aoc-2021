package day14

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day14) {
    testPart1(checkResult = 1588L)
    testPart2(checkResult = 2188189693529L)
    runPart1()
    runPart2()
}

object Day14 : Aoc2021(debug = false) {

    override fun solutionPart1(inputReader: InputStreamReader): Any {
        val ep = ExtendedPolymerization(inputReader)
        val groups = ep.solveTier5(ep.inputData, 10, 1)
        return groups.maxMinusMin()
    }

    override fun solutionPart2(inputReader: InputStreamReader): Any {
        val ep = ExtendedPolymerization(inputReader)
        val groups = ep.solveTier5(ep.inputData, 20, 2)
        return groups.maxMinusMin()
    }  // 33.416 seconds... ??? 217.825 seconds with debug


    class ExtendedPolymerization(inputReader: InputStreamReader) {
        val inputData: String
        private val rules: Map<String, String>
        private val rulesRecursive: Map<String, Pair<String, String>>

        init {
            val inputLines = inputReader.buffered().use { it.readLines() }
            inputData = inputLines.first()
            check(inputLines[1].isEmpty()) { "Input not formatted correctly" }
            val ruleLines = inputLines.drop(2)

            rules = mutableMapOf<String, String>().apply {
                ruleLines.map { line ->
                    line.split(" -> ").let {
                        check(put(it[0], "${it[0][0]}${it[1][0]}") == null)
                    }
                }
            }
            rulesRecursive = mutableMapOf<String, Pair<String, String>>().apply {
                ruleLines.map { line ->
                    line.split(" -> ").let {
                        check(put(it[0], "${it[0][0]}${it[1][0]}" to "${it[1][0]}${it[0][1]}") == null)
                    }
                }
            }
        }

        private val resultRecursive = mutableMapOf<Char, Long>()
        private val progressRecursive = mutableMapOf<Int, Int>()

        private fun processOneStep(input: String) = input.windowed(2)
            .joinToString(separator = "", postfix = input.last().toString()) {
                rules[it] ?: error("xxx")
            }

        fun solveTier5(template: String, levels: Int, steps: Int): Map<Char, Long> {
//            val steps = count
            val rulesTier = mutableMapOf<String, Pair<String, Map<Char, Int>>>()

            rules.forEach { pairToPairs ->
                var res = pairToPairs.key
                debug { "Pair: $res" }
                repeat(levels) { res = processOneStep(res) }
                val counts = res.dropLast(1).groupByFirstLetterAndCount()
                rulesTier[pairToPairs.key] = Pair(res, counts)
            }

            fun stepTier5(part: String, level: Int) {
                if (level < 3) {
                    progressRecursive.increment(level)
                    debug { progressRecursive.values.joinToString() }
                }
                if (level < steps) {
                    val pairResult = rulesTier[part]?.first ?: error("Tier x1")
                    pairResult.windowed(2).forEach {
                        stepTier5(it, level + 1)
                    }
                } else {
                    val pairCount = rulesTier[part]?.second ?: error("Tier x2")
                    resultRecursive.add(pairCount)
                }
            }

            resultRecursive.clear()
            progressRecursive.clear()
            template.windowed(2).forEach {
                stepTier5(it, 1)
            }
            resultRecursive.increment(template.last())
            return resultRecursive
        }
    }


    private fun String.groupByFirstLetterAndCount() = groupingBy { it }.eachCount()

//    private fun <T> Map<T, Int>.maxMinusMin() = maxOf { it.value } - minOf { it.value }
    private fun <T> Map<T, Long>.maxMinusMin() = maxOf { it.value } - minOf { it.value }

    private fun <T> MutableMap<T, Int>.increment(pos: T, value: Int = 1) = getOrPut(pos) { 0 }.let { old ->
        replace(pos, old, old + value)
    }

    @JvmName("incrementLong")
    private fun <T> MutableMap<T, Long>.increment(pos: T, value: Int = 1) = getOrPut(pos) { 0 }.let { old ->
        replace(pos, old, old + value)
    }

//    private fun <T> MutableMap<T, Int>.add(values: Map<T, Int>) = values.forEach { item ->
//        increment(item.key, item.value)
//    }

    @JvmName("addLong")
    private fun <T> MutableMap<T, Long>.add(values: Map<T, Int>) = values.forEach { item ->
        increment(item.key, item.value)
    }

}
