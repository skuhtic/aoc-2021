package shared

data class Position(val x: Int, val y: Int) {
    private val toLeft get() = Position(x - 1, y)
    private val toUp get() = Position(x, y - 1)
    private val toRight get() = Position(x + 1, y)
    private val toDown get() = Position(x, y + 1)
    private fun crossNeighbours(width: Int, height: Int) = listOf(toLeft, toRight, toUp, toDown).filter {
        it.x in 0 until width && it.y in 0 until height
    }

    fun crossNeighbours(squareSize: Int) = crossNeighbours(squareSize, squareSize)

}

