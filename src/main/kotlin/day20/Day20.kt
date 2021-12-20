package day20

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day20) {
    testPart1(checkResult = 35)
//    testPart1(checkResult = 35, inputFileNameSuffix = "i")
    runPart1() // 5960 wrong!!!
    testPart2(checkResult = 3351)
    runPart2()
}

object Day20 : Aoc2021(debug = true) {

    private fun inputData(inputReader: InputStreamReader): Pair<Map<Int, Int>, List<IntArray>> =
        with(inputReader.buffered()) {
            val ieaLine = readLine() ?: error("No image enhancement algorithm line")
            check(readLine().isEmpty()) { "No separator in input" }
            val image = readLines().map { rowString ->
                rowString.map { char -> char.toBinary() }.toIntArray()
            }
            val enhancementAlgorithm = ieaLine.indices.associateWith { ieaLine[it].toBinary() }
            check(enhancementAlgorithm.size == 512) { "Invalid algorithm" }
            enhancementAlgorithm to image
        }

    override fun solutionPart1(inputReader: InputStreamReader) = inputData(inputReader).let { (iea, image) ->
        TrenchMap(iea, image).apply { solve(2) }.numberOfLitPixels
    }

    override fun solutionPart2(inputReader: InputStreamReader) = inputData(inputReader).let { (iea, image) ->
        TrenchMap(iea, image).apply { solve(50) }.numberOfLitPixels
    }

    class TrenchMap(private val iea: Map<Int, Int>, imageData: List<IntArray>) {
        private var image: BinaryImage = BinaryImage(imageData)
        private var advances = 0
        private var surroundings = 0

        val numberOfLitPixels: Int get() = image.noOfLitPixels

        private fun advance() {
            val newImageData = List(image.height + 2) { ny ->
                IntArray(image.width + 2) { nx ->
                    iea[image.wholeRectWithNeighbours(nx - 1, ny - 1, surroundings)]
                        ?: error("Error generating new image or referencing algorithm")
                }
            }
            advances++
            image = BinaryImage(newImageData)
            surroundings = if (surroundings == 1) iea[511]!! else iea[0]!!
        }

        fun solve(times: Int) = repeat(times) { advance() }.also { debug { image } }
    }

    class BinaryImage(private val image: List<IntArray>) {
        val height = image.size
        val width = image[0].size

        operator fun get(x: Int, y: Int) = if (y >= 0 && x >= 0 && y < height && x < width) image[y][x] else null

        val noOfLitPixels get() = image.sumOf { row -> row.count { it == 1 } }

        fun wholeRectWithNeighbours(x: Int, y: Int, surroundings: Int): Int = listOf( // @formatter:off
            get(x - 1, y - 1),   get(x, y - 1),   get(x + 1, y - 1),
            get(x - 1, y),          get(x, y),          get(x + 1, y),
            get(x - 1, y + 1),   get(x, y + 1),   get(x + 1, y + 1),
        ).fold(0) { a, b -> a * 2 + (b ?: surroundings) } // @formatter:on

        override fun toString(): String = image.debugString()
    }

    private fun Char.toBinary() = when (this) {
        '#' -> 1
        '.' -> 0
        else -> error("Invalid char in input")
    }

    // Debug functions
    fun List<IntArray>.debugString() = this.chunked(this[0].size).joinToString("", "\n") { lines ->
        lines.joinToString("\n") { row ->
            row.joinToString("") {
                when (it) {
                    1 -> "#"
                    0 -> "."
                    else -> error("Error printing")
                }
            }
        }
    }

}




