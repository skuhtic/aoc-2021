package day16

import shared.Aoc2021
import java.io.InputStreamReader

fun main() = with(Day16) {
    testPart1(checkResult = 31, inputFileNameSuffix = "4")
    runPart1()
    runPart2()
}

object Day16 : Aoc2021() {

    override fun solutionPart1(inputReader: InputStreamReader): Int =
        inputReader.use { hexStringToBitString(it.readText()) }.iterator().let { bitSequence ->
            DataPacket.parse(bitSequence).versionSum
        }

    override fun solutionPart2(inputReader: InputStreamReader): Long =
        inputReader.use { hexStringToBitString(it.readText()) }.iterator().let { bitSequence ->
            DataPacket.parse(bitSequence).value
        }

    private fun hexStringToBitString(hexString: String): String = hexString
        .map { it.digitToInt(16).toString(2).padStart(4, '0') }
        .flatMap { it.toList() }.joinToString("")

    sealed class DataPacket private constructor(val type: Int, val version: Int) {
        abstract val versionSum: Int
        abstract val value: Long

        companion object {
            fun parse(reader: Iterator<Char>): DataPacket = reader.readInt(3).let { version ->
                when (val readType = reader.readInt(3)) {
                    4 -> Literal.parse(version, reader)
                    in 0..7 -> Operator.parse(readType, version, reader)
                    else -> error("Invalid type")
                }
            }
        }

        class Operator(type: Int, version: Int, private val inside: List<DataPacket>) : DataPacket(type, version) {
            override val versionSum get() = version + inside.sumOf { it.versionSum }

            override val value: Long
                get() = when (type) {
                    0 -> inside.sumOf { it.value }
                    1 -> inside.fold(1) { product, nextPacket -> product * nextPacket.value }
                    2 -> inside.minOf { it.value }
                    3 -> inside.maxOf { it.value }
                    5 -> if (inside[0].value > inside[1].value) 1 else 0
                    6 -> if (inside[0].value < inside[1].value) 1 else 0
                    7 -> if (inside[0].value == inside[1].value) 1 else 0
                    else -> error("Unknown operator")
                }

            companion object {
                internal fun parse(type: Int, version: Int, reader: Iterator<Char>) = Operator(
                    type, version,
                    if (reader.readBoolean()) parseInsideWithPackets(reader) else parseInsideWithBits(reader)
                )

                private fun parseInsideWithPackets(reader: Iterator<Char>): List<DataPacket> =
                    mutableListOf<DataPacket>().apply {
                        repeat(reader.readInt(11)) {
                            add(parse(reader))
                        }
                    }

                private fun parseInsideWithBits(reader: Iterator<Char>): List<DataPacket> =
                    mutableListOf<DataPacket>().apply {
                        reader.readString(reader.readInt(15)).iterator().let { insideReader ->
                            while (true) {
                                add(parse(insideReader))
                                if (!insideReader.hasNext()) break
                            }
                        }
                    }

            }
        }

        class Literal(version: Int, override val value: Long) : DataPacket(4, version) {
            override val versionSum get() = version

            companion object {
                fun parse(version: Int, reader: Iterator<Char>): DataPacket {
                    var valueParsed = ""
                    while (true) {
                        val more = reader.readBoolean()
                        valueParsed += reader.readString(4)
                        if (!more) break
                    }
                    return Literal(version, valueParsed.toLong(2))
                }
            }
        }
    }

    fun Iterator<Char>.readBoolean(): Boolean = next().digitToInt() == 1
    fun Iterator<Char>.readString(size: Int): String = (1..size).map { next() }.joinToString("")
    fun Iterator<Char>.readInt(size: Int): Int = readString(size).toInt(2)
}
