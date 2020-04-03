/*
 * http://www.fbs.osaka-u.ac.jp/labs/skondo/simulators/rd_new.html
 * より、方程式を引用
 */
package lambdastar314.simworld.patterns

import java.awt.Dimension
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class TuringPattern(
    var pActAct: Double = 0.08, var pInhAct: Double = -0.08, var pActC: Double = 0.1,
    var pActInh: Double = 0.11, var pInhInh: Double = 0.0, var pInhC: Double = -0.15,
    var pActLimit: Double = 0.2, var pInhLimit: Double = 0.5,
    var decayConstA: Double = 0.03, var decayConstI: Double = 0.06,
    //拡散係数
    var diffConstA: Double = 0.02, var diffConstI: Double = 0.5,
    //空間スケール
    var dt: Double = 0.2,
    //時間スピード
    var ds: Double = 1.0
) : IPattern {

    private var cTick = 0
    private var width = 0
    private var height = 0

    var pattern: Array<Array<IPattern.TuringCell>> =
        Array(0) { Array(0) { IPattern.TuringCell(0.0, 0.0) } }

    override fun get(x: Int, y: Int): Double = pattern[x][y].u * 0.1953125

    override fun init(width: Int, height: Int) {
        this.width = width
        this.height = height
        pattern =
            Array(width) { Array(height) { IPattern.TuringCell(0.0, 0.0) } }
    }

    override fun getCurrentTick(): Int = cTick

    override fun clear() {
        pattern =
            Array(width) { Array(height) { IPattern.TuringCell(0.0, 0.0) } }
    }

    override fun fillSquare(x: Int, y: Int, width: Int, height: Int) {
        for (ix in x..(width + x)) {
            for (iy in y..(height + y)) {
                pattern[ix][iy].u = 1.0
            }
        }
    }

    override fun fillCircle(x: Int, y: Int, radius: Double) {
        val xcenter = x + radius / 2
        val ycenter = y + radius / 2
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (abs(x - xcenter) <= radius.toInt() && abs(y - ycenter) <= sqrt(
                        radius.pow(2.0) - (x - xcenter).toDouble().pow(
                            2.0
                        )
                    ).toInt()
                ) {
                    pattern[x][y].u = 1.0
                }
            }
        }
    }

    override fun fillRandom(x: Int, y: Int, width: Int, height: Int, r: Random) {
        for (ix in x..(width + x)) {
            for (iy in y..(height + y)) {
                pattern[ix][iy].u = r.nextDouble() % 1.0
            }
        }
    }

    override fun tick() {
        diffusion()
        grow()
        cTick++
    }

    private fun grow() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                var synAct = pActAct * pattern[x][y].u + pInhAct * pattern[x][y].v + pActC
                if (synAct < 0) synAct = 0.0
                if (synAct > pActLimit) synAct = pActLimit

                var synInh = pActInh * pattern[x][y].u + pInhInh * pattern[x][y].v + pInhC
                if (synInh < 0) synInh = 0.0
                if (synInh > pInhLimit) synInh = pInhLimit
                pattern[x][y].u += (-decayConstA * pattern[x][y].u + synAct) * dt
                pattern[x][y].v += (-decayConstI * pattern[x][y].v + synInh) * dt
            }
        }
    }

    private fun diffusion() {
        val n = Array(width) { Array(height) { IPattern.TuringCell(0.0, 0.0) } }
        for (x in 0 until width) {
            for (y in 0 until height) {
                var rightCell = pattern[(x + 1) % width][y].u
                var leftCell = pattern[(x + width - 1) % width][y].u
                var upperCell = pattern[x][(y + 1) % height].u
                var lowerCell = pattern[x][(y + height - 1) % height].u
                n[x][y].u += diffConstA * dt * (rightCell + leftCell + upperCell + lowerCell - 4 * pattern[x][y].u) / ds / ds

                rightCell = pattern[(x + 1) % width][y].v
                leftCell = pattern[(x + width - 1) % width][y].v
                upperCell = pattern[x][(y + 1) % height].v
                lowerCell = pattern[x][(y + height - 1) % height].v
                n[x][y].v =
                    diffConstI * dt * (rightCell + leftCell + upperCell + lowerCell - 4 * pattern[x][y].v) / ds / ds
            }
        }
        for (x in 0 until width) {
            for (y in 0 until height) {
                pattern[x][y].u += n[x][y].u
                pattern[x][y].v += n[x][y].v
            }
        }
    }

    override fun getSize(): Dimension = Dimension(width, height)
}