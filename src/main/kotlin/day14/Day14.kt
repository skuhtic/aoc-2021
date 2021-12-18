package day14

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day14) {
    testPart1(checkResult = 1588L)
    testPart2(checkResult = 2188189693529L)
}

object Day14 : Aoc2021() {

    //    test 5 steps = 33 -> NBBNBBNBBBNBBNBBCNCCNBBBCCNBCNCCNBBNBBNBBNBBNBBNBNBBNBBNBBNBBNBBCHBHHBCHBHHNHCNCHBCHBNBBCHBHHBCHB
//    N -> 23
//    B -> 46
//    C -> 15
//    H -> 13
//    test 10 steps = 1588
//    N -> 865
//    B -> 1749
//    C -> 298
//    H -> 161
    override fun solutionPart1(inputReader: InputStreamReader): Any {
        val ep = ExtendedPolymerization(inputReader)
        val groups = ep.solveTier5(ep.inputData, 10, 1)
        return groups.maxMinusMin()
    }

    override fun solutionPart2(inputReader: InputStreamReader): Any {
        val ep = ExtendedPolymerization(inputReader)
        val groups = ep.solveTier5(ep.inputData, 20, 2)

        return groups.maxMinusMin()

    }  // 217.825 seconds, without debug 33.416 seconds... ???
    //2188189693529

    class ExtendedPolymerization(inputReader: InputStreamReader) {
        val inputData: String
        private val rules: Map<String, String>
        private val rulesRecursive: Map<String, Pair<String, String>>

        init {
            inputReader.buffered().use { reader ->
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
            inputReader.buffered().use { reader ->
                reader.readLine()
                check(reader.readLine().isEmpty()) { "Input not formatted correctly" }
                rulesRecursive = mutableMapOf<String, Pair<String, String>>().apply {
                    reader.readLines().map { line ->
                        line.split(" -> ").let {
                            check(put(it[0], "${it[0][0]}${it[1][0]}" to "${it[1][0]}${it[0][1]}") == null)
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

        private val result get() = data.groupingBy { it }.eachCount().maxMinusMin()

        // RECURSIVE WAY

        private val resultRecursive = mutableMapOf<Char, Long>()
        private val progressRecursive = mutableMapOf<Int, Int>()

        private fun solveRecursive(template: String, finalLevel: Int): Long {
            resultRecursive.clear()
            progressRecursive.clear()
            debugToConsole { "Start with $data" }
            data.windowed(2).forEach {
                solveRecursive(it, finalLevel, 0)
            }
            resultRecursive.increment(template.last())
            return resultRecursive.maxOf { it.value } - resultRecursive.minOf { it.value }
        }

        private fun solveRecursive(pair: String, finalLevel: Int, level: Int) {
            if (level < 20) {
                progressRecursive.increment(level)
                debugToConsole { progressRecursive.values.joinToString() }
            }  // End: 3, 6, 12, 24, 48, 96, 192, 384, 768, 1536, 3072
            if (level < finalLevel) {
                solveRecursive(rulesRecursive[pair]?.first ?: error("X1"), finalLevel, level + 1)
                solveRecursive(rulesRecursive[pair]?.second ?: error("X2"), finalLevel, level + 1)
            } else {
//            pair.forEach { resultRecursive.increment(it) }
                resultRecursive.increment(pair[0])
            }
        }

        // xxx

        private fun processOneStep(input: String) = input.windowed(2)
//        .joinToString(separator = "") {
            .joinToString(separator = "", postfix = input.last().toString()) {
                rules[it] ?: error("xxx")
            }


        private fun processSteps(input: String, noOfSteps: Int): String {
            var result = input
            repeat(noOfSteps) {
                result = processOneStep(result)
            }
            return result
        }

        // TIER 5 WAY

        fun solveTier5(template: String, levels: Int, count: Int): Map<Char, Long> {
            val steps = count
            val rulesTier = mutableMapOf<String, Pair<String, Map<Char, Int>>>()

            rules.forEach { pairToPairs ->
                var res = pairToPairs.key
                debugToConsole { "Pair: $res" }
                repeat(levels) { res = processOneStep(res) }
                val counts = res.dropLast(1).groupByFirstLetterAndCount()
                rulesTier[pairToPairs.key] = Pair(res, counts)
            }

            fun stepTier5(part: String, level: Int) {
                if (level < 3) {
                    progressRecursive.increment(level)
                    debugToConsole { progressRecursive.values.joinToString() }
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


}

private fun String.groupByFirstLetterAndCount() = groupingBy { it }.eachCount()

private fun <T> Map<T, Int>.maxMinusMin() = maxOf { it.value } - minOf { it.value }

private fun <T> Map<T, Long>.maxMinusMin() = maxOf { it.value } - minOf { it.value }

private fun <T> MutableMap<T, Int>.increment(pos: T, value: Int = 1) = getOrPut(pos) { 0 }.let { old ->
    replace(pos, old, old + value)
}

@JvmName("incrementLong")
private fun <T> MutableMap<T, Long>.increment(pos: T, value: Int = 1) = getOrPut(pos) { 0 }.let { old ->
    replace(pos, old, old + value)
}

private fun <T> MutableMap<T, Int>.add(values: Map<T, Int>) = values.forEach { item ->
    increment(item.key, item.value)
}

@JvmName("addLong")
private fun <T> MutableMap<T, Long>.add(values: Map<T, Int>) = values.forEach { item ->
    increment(item.key, item.value)
}


