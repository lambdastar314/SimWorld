/*
 *http://www.fbs.osaka-u.ac.jp/labs/skondo/simulators/rd_new.html
 * より、方程式を引用
 */
package lambdastar314.simworld.patterns.turingpattern

import lambdastar314.simworld.worldgenerators.perlin.PerlinNoise
import java.util.*

/**
 *
 */
class TuringPattern(
    val width: Int, val height: Int, var dt: Double = 0.2,
    var ds: Double = 1.0,
    var pActAct: Double = 0.08, var pInhAct: Double = -0.08, var pActC: Double = 0.1,
    var pActInh: Double = 0.11, var pInhInh: Double = 0.0, var pInhC: Double = -0.15,
    var pActLimit: Double = 0.2, var pInhLimit: Double = 0.5,
    var diffConstA: Double = 0.02,

    var diffConstI: Double = 0.5,
    var decayConstA: Double = 0.03, var decayConstI: Double = 0.06
) {

    var currentTick = 0

    data class Cell(var activator: Double, var inhibitor: Double) {
    }

    var pattern: Array<Array<Cell>>

    init {
        pattern =
            Array(width) { Array(height) { Cell(0.0, 0.0) } }
    }

    fun initialize(
        scaleX: Double = 10.0,
        scaleY: Double = 10.0,
        z: Double
    ) {
//        val r = Random(1) //ランダムを使うときにコメントを外すこと
        for (x in 0 until width) {
            for (y in 0 until height) {
                //パーリンノイズで埋め尽くす
                pattern[x][y].activator = (PerlinNoise.generateOrigin(x, y, z, scaleX, scaleY, octave = 2) / 2) + 0.5
                //ランダムに埋める
//                pattern[x][y].activator = r.nextDouble() % 1
                //中央に四角形を作る
//                val xsize = width / 16
//                val xcenter = width / 2
//                val ysize = height / 16
//                val ycenter = height / 2
//                if (x in (xcenter - xsize)..(xcenter + xsize) && y in (ycenter - ysize)..(ycenter + ysize)) pattern[x][y].activator = 1.0
            }
        }
    }

    fun tick() {
        //成長させる
        grow()
        growinhibitor()
        //拡散させる
        diffucation()
        currentTick++
    }

    private fun grow() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                var synAct = pActAct * pattern[x][y].activator + pInhAct * pattern[x][y].inhibitor + pActC
                if (synAct < 0) synAct = 0.0
                if (synAct > pActLimit) synAct = pActLimit
                pattern[x][y].activator += (-decayConstA * pattern[x][y].activator + synAct) * dt
            }
        }
    }

    private fun growinhibitor() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                var synInh = pActInh * pattern[x][y].activator + pInhInh * pattern[x][y].inhibitor + pInhC
                if (synInh < 0) synInh = 0.0
                if (synInh > pInhLimit) synInh = pInhLimit
                pattern[x][y].inhibitor += (-decayConstI * pattern[x][y].inhibitor + synInh) * dt
            }
        }
    }

    private fun diffucation() {
        val n = Array(width) { Array(height) { Cell(0.0, 0.0) } }
        for (x in 0 until width) {
            for (y in 0 until height) {
                var rightCell = pattern[(x + 1) % width][y].activator
                var leftCell = pattern[(x + width - 1) % width][y].activator
                var upperCell = pattern[x][(y + 1) % height].activator
                var lowerCell = pattern[x][(y + height - 1) % height].activator
                n[x][y].activator += diffConstA * dt * (rightCell + leftCell + upperCell + lowerCell - 4 * pattern[x][y].activator) / ds / ds

                rightCell = pattern[(x + 1) % width][y].inhibitor
                leftCell = pattern[(x + width - 1) % width][y].inhibitor
                upperCell = pattern[x][(y + 1) % height].inhibitor
                lowerCell = pattern[x][(y + height - 1) % height].inhibitor
                n[x][y].inhibitor =
                    diffConstI * dt * (rightCell + leftCell + upperCell + lowerCell - 4 * pattern[x][y].inhibitor) / ds / ds
            }
        }
        for (x in 0 until width) {
            for (y in 0 until height) {
                pattern[x][y].activator += n[x][y].activator
                pattern[x][y].inhibitor += n[x][y].inhibitor
            }
        }
    }
}