package shared

data class Position(val x: Int, val y: Int) {

    companion object {
        var minX: Int = 0
        var maxX: Int = 0
        var minY: Int = 0
        var maxY: Int = 0
    }

    init {
        maxX = maxOf(maxX, x)
        minX = maxOf(minX, x)
        maxY = maxOf(maxY, y)
        minY = maxOf(minY, y)
    }

    val toLeft = if (x > 0) Position(x-1, y) else null

    val toUp = if (y > 0) Position(x, y-1) else null
}
