/*
 * オライリー社出版「作って動かすALife」の方程式を引用
 */
package lambdastar314.simworld.patterns

import java.awt.Dimension
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class GrayScottModel(
    //補充及び減量
    val f: Double = 0.022, val k: Double = 0.051,
    //拡散係数
    val Du: Double = 0.00002, val Dv: Double = 0.00001,
    //空間スケール
    val Dx: Double = 0.01,
    //時間スピード
    val Dt: Double = 1.0
) : IPattern {

    private var cTick = 0
    var width = 0
    var height = 0

    var pattern: Array<Array<IPattern.TuringCell>> =
        Array(0) { Array(0) { IPattern.TuringCell(1.0, 0.0) } }

    override fun get(x: Int, y: Int): Double = 1.0 - pattern[x][y].u

    override fun tick() {
        //ラプラシアンの計算
        val laplacian = Array(width) { Array(height) { IPattern.TuringCell(0.0, 0.0) } }
        for (x in 0 until width) {
            for (y in 0 until height) {
                val rightU = pattern[(x + 1) % width][y].u
                val leftU = pattern[(x + width - 1) % width][y].u
                val upperU = pattern[x][(y + 1) % height].u
                val lowerU = pattern[x][(y + height - 1) % height].u
                laplacian[x][y].u = (rightU + leftU + upperU + lowerU - 4.0 * pattern[x][y].u) / (Dx * Dx)
                val rightV = pattern[(x + 1) % width][y].v
                val leftV = pattern[(x + width - 1) % width][y].v
                val upperV = pattern[x][(y + 1) % height].v
                val lowerV = pattern[x][(y + height - 1) % height].v
                laplacian[x][y].v = (rightV + leftV + upperV + lowerV - 4.0 * pattern[x][y].v) / (Dx * Dx)
            }
        }
        //それぞれに当てていく
        for (x in 0 until width) {
            for (y in 0 until height) {
                val u = pattern[x][y].u
                val v = pattern[x][y].v
                val DuDt = (laplacian[x][y].u * Du) - (u * v * v) + (f * (1.0 - u))
                val DvDt = (laplacian[x][y].v * Dv) + (u * v * v) - ((f + k) * v)
                pattern[x][y].u += DuDt * Dt
                pattern[x][y].v += DvDt * Dt
            }
        }
        cTick++
    }

    override fun init(width: Int, height: Int) {
        this.width = width
        this.height = height
        pattern =
            Array(width) { Array(height) { IPattern.TuringCell(1.0, 0.0) } }
    }

    override fun clear() {
        pattern =
            Array(width) { Array(height) { IPattern.TuringCell(1.0, 0.0) } }
    }

    override fun fillSquare(x: Int, y: Int, width: Int, height: Int) {
        for (ix in x..(width + x)) {
            for (iy in y..(height + y)) {
                pattern[ix][iy].u = 0.5
                pattern[ix][iy].v = 0.25
            }
        }
    }

    override fun fillCircle(x: Int, y: Int, radius: Double) {
        val xcenter = x + radius / 2
        val ycenter = y + radius / 2
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (abs(x - xcenter) <= radius.toInt() && abs(y - ycenter) <= sqrt(
                        radius.pow(2.0) - (x - xcenter).pow(
                            2.0
                        )
                    ).toInt()
                ) {
                    pattern[x][y].u = 0.5
                    pattern[x][y].v = 0.25
                }
            }
        }
    }

    override fun fillRandomSquare(x: Int, y: Int, width: Int, height: Int, r: Random) {
        for (ix in x..(width + x)) {
            for (iy in y..(height + y)) {
                pattern[ix][iy].u = 0.5 + (r.nextDouble() % 1.0) / 100.0
                pattern[ix][iy].v = 0.25 + (r.nextDouble() % 1.0) / 100.0
            }
        }
    }

    override fun fillRandomCircle(x: Int, y: Int, radius: Double, r: Random) {
        val xcenter = x + radius / 2
        val ycenter = y + radius / 2
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (abs(x - xcenter) <= radius.toInt() && abs(y - ycenter) <= sqrt(
                        radius.pow(2.0) - (x - xcenter).pow(
                            2.0
                        )
                    ).toInt()
                ) {
                    pattern[x][y].u = 0.5 + (r.nextDouble() % 1.0) / 100.0
                    pattern[x][y].v = 0.25 + (r.nextDouble() % 1.0) / 100.0
                }
            }
        }
    }

    override fun getCurrentTick(): Int = cTick
    override fun getSize(): Dimension = Dimension(width, height)

    override fun resize(newWidth: Int, newHeight: Int) {
        val wScale = newWidth / width.toDouble()
        val hScale = newHeight / height.toDouble()
        val newPattern = Array(newWidth) { Array(newHeight) { IPattern.TuringCell(0.0, 0.0) } }
        for (ix in 0 until newWidth) {
            for (iy in 0 until newHeight) {
//                newPattern[ix][iy] = pattern[ix / wScale][iy / hScale]
                val xOrigin = ix / wScale
                val yOrigin = iy / hScale
                val dx = xOrigin % 1.0
                val dy = yOrigin % 1.0
                val xInt = (xOrigin - dx).toInt()
                val yInt = (yOrigin - dy).toInt()
                var xInt1 = xInt + 1
                if (xInt1 >= width) xInt1 = width - 1
                var yInt1 = yInt + 1
                if (yInt1 >= height) yInt1 = height - 1

                newPattern[ix][iy].u = pattern[xInt][yInt].u * (1 - dx) * (1 - dy) +
                        pattern[xInt][yInt1].u * (1 - dx) * dy +
                        pattern[xInt1][yInt].u * dx * (1 - dy) +
                        pattern[xInt1][yInt1].u * dx * dy
                newPattern[ix][iy].v = pattern[xInt][yInt].v * (1 - dx) * (1 - dy) +
                        pattern[xInt][yInt1].v * (1 - dx) * dy +
                        pattern[xInt1][yInt].v * dx * (1 - dy) +
                        pattern[xInt1][yInt1].v * dx * dy
            }
        }
        pattern = newPattern
        width = newWidth
        height = newHeight
    }
}