package day16

import shared.Aoc2021
import java.io.InputStreamReader
import java.io.StringReader

fun main() = with(Day16) {
    testPart1(checkResult = 16, inputFileNameSuffix = "1")
    testPart1(checkResult = 12, inputFileNameSuffix = "2")
    testPart1(checkResult = 23, inputFileNameSuffix = "3")
    testPart1(checkResult = 31, inputFileNameSuffix = "4")
    runPart1()
//    testPart2(checkResult = 1)
}

object Day16 : Aoc2021() {

    override fun solutionPart1(inputReader: InputStreamReader): Any = inputReader.use { reader ->
        var counter = 0
        val message = StringBuilder().apply {
            while (true) {
                val c = reader.read().let { if (it < 0) null else it }?.toChar() ?: break
                counter++
                append(c.digitToInt(16).toString(2).padStart(4, '0'))
            }
        }.toString()
        check(message.length == counter * 4) { "Lengths wrong" }
        debugToConsole { "(${message.length}) $message" }
        val stringReader = StringReader(message)
        val dp = DataPacket.parse(stringReader)
        val result = dp.versionSum
        check(result == DataPacket.versionSumRead)
        return result
    }

    override fun solutionPart2(inputReader: InputStreamReader): Any = 1

    sealed class DataPacket private constructor(val type: Int, val version: Int) {
        abstract val versionSum: Int

        companion object {
            private var readerPosition: Int = 0
            var versionSumRead: Int = 0

            fun parse(reader: StringReader): DataPacket {
                readerPosition = 0
                versionSumRead = 0
                return parseWithSize(reader).let { (_, packet) ->
                    packet
                }
            }

            private fun parseWithSize(reader: StringReader): Pair<Int, DataPacket> {
                val (size, packet) = reader.takeAsInt(3).let { version ->
                    versionSumRead += version
                    when (val type = reader.takeAsInt(3)) {
                        4 -> Literal.parseWith(6, version, reader)
                        in 0..7 -> Operator.parseWithSize(6, type, version, reader)
                        else -> error( "Invalid type" )
                    }
                }
                return (size + 6) to packet
            }

            private fun StringReader.take1asBoolean(): Boolean {
                val ret = read().let { if (it >= 0) it.toChar().digitToInt() == 1 else error("Reader end") }
                readerPosition++
                debugToConsole { "Read 1: $ret ($readerPosition)" }
                return ret
            }

            private fun StringReader.takeAsInt(n: Int) = CharArray(n).let { charArray ->
                check(n < 16) { "Can't read more than 15 bits" }
                check(read(charArray) == n) { "Reader end/error" }
                val ret = charArray.joinToString("").toInt(2)
                readerPosition += n
                debugToConsole { "Read $n: $ret ($readerPosition)" }
                ret
            }

            private fun StringReader.skipToNext() = CharArray(8).let { charArray ->
                val n = (readerPosition.div(8) + 1) * 8 - readerPosition
                check(read(charArray, 0, n) == n) { "Reader end/error" }
                readerPosition += n
                debugToConsole { "Skipped $n ($readerPosition)" }
            }

        }

        class Operator(type: Int, version: Int, private val inside: List<DataPacket>) : DataPacket(type, version) {
            override val versionSum get() = version + inside.sumOf { it.versionSum }

            companion object {
                internal fun parseWithSize(
                    parsed: Int,
                    type: Int,
                    version: Int,
                    reader: StringReader
                ): Pair<Int, DataPacket> {
                    check(type != 4) { "Operator can't be with type 4" }
                    val (size, inside) = if (reader.take1asBoolean())
                        parseInsideWithPackets(parsed + 1, reader)
                    else
                        parseInsideWithBits(parsed + 1, reader)
                    return size to Operator(type, version, inside)
                }

                private fun parseInsideWithPackets(parsed: Int, reader: StringReader): Pair<Int, List<DataPacket>> {
                    check(parsed == 7) { "Operation (packets): parsed should be 7" }
                    val noOfPackets = reader.takeAsInt(11)
//                    check(noOfPackets <= 6) { "More than 6 packets" }
                    val insideParsed = mutableListOf<DataPacket>()
                    var sizeSum = 11
                    repeat(noOfPackets) {
                        val (size, packet) = parseWithSize(reader)
                        insideParsed.add(packet)
                        sizeSum += size
                    }
                    return 1 + sizeSum to insideParsed
                }

                private fun parseInsideWithBits(parsed: Int, reader: StringReader): Pair<Int, List<DataPacket>> {
                    val noOfBits = reader.takeAsInt(15)
//                    check(noOfBits <= 96) { "Length more than 96 bits" }
                    val insideParsed = mutableListOf<DataPacket>()
                    var sizeSum = 0
                    while (true) {
                        val (size, packet) = parseWithSize(reader)
                        insideParsed.add(packet)
                        sizeSum += size
                        if (sizeSum >= noOfBits) break
                    }
                    return 1 + sizeSum to insideParsed
                }
            }
        }

        class Literal(version: Int, val value: Int) : DataPacket(4, version) {
            override val versionSum get() = version

            companion object {
                fun parseWith(parsed: Int, version: Int, reader: StringReader): Pair<Int, DataPacket> {
                    check(parsed == 6) { "Literal: parsed should be 6" }
                    var value = 0
                    var sizeSum = 0
                    while (true) {
                        val more = reader.take1asBoolean()
                        val valuePart = reader.takeAsInt(4)
                        sizeSum += 5
                        value = value.shl(4) + valuePart
                        if (!more) break
                    }
                    return (sizeSum + parsed) to Literal(version, value)
                }
            }
        }

    }

}

/*

SINGLE OUTER PACKET + packets
...
example Type 4 - Literal
D2FE28
->
110100101111111000101000
VVVTTTxAAAAxBBBBxCCCC---
->
Version=6
Type=4 (Literal)
x -> 1 - not last
A=bits of number
x -> 1 - not last
B=bits of number
x ->  - THESE ARE LAST BITS
C=bits of number
--- -> filler to end (not used)

A B C = 011111100101 -> 2021



Type != 4 -> Operators

Two modes:
1. length type ID == 0 -> next 15 bits total = length in bits of sub-packets
2. length type ID == 1 -> next 11 bits total = number of sub-packets

38006F45291200
->
00111000000000000110111101000101001010010001001000000000
VVVTTTILLLLLLLLLLLLLLLAAAAAAAAAAABBBBBBBBBBBBBBBB-------
Version=1
Type=6 (Operator)
I==0 -> length in bits
Length=27
A -> first sub-packet
B -> second (last) sub-packet
--- length(a) + length(B) == 27

EE00D40C823060
->
11101110000000001101010000001100100000100011000001100000
VVVTTTILLLLLLLLLLLAAAAAAAAAAABBBBBBBBBBBCCCCCCCCCCC-----
Version=7
Type=3 (Operator)
I==1 -> length in number of sub-packets
Length=3
A -> first sub-packet
B -> second sub-packet
C -> third (last) sub-packet

*/
