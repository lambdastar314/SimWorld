package lambdastar314.simworld.lifegame

import java.util.function.Predicate

class LifeGame(var predicater: Predicate<Pair<Boolean, Int>>) {

    var pattern = Array(0){Array(0){false} }
    var width = 0
    var height = 0

    var cTick = 0

    fun init(width: Int, height: Int) {
        this.width = width
        this.height = height
        pattern =
            Array(width) { Array(height) { false } }
    }

    fun tick() {
        cTick++
        val n = Array(width) { Array(height) { false } }
        val regions = arrayOf(
            Pair(-1, -1), Pair(0, -1), Pair(1, -1),
            Pair(-1, 0), Pair(1, 0),
            Pair(-1, 1), Pair(0, 1), Pair(1, 1)
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                var lives = 0
                for(r in regions){
                    val neighbor = pattern[(width + x+r.first) % width][(height + y+r.second) % height]
                    if(neighbor)lives++
                }
                val canLives = predicater.test(Pair(pattern[x][y], lives))
                n[x][y] = canLives
            }
        }
        for (x in 0 until width) {
            for (y in 0 until height) {
                pattern[x][y] = n[x][y]
            }
        }
    }
}