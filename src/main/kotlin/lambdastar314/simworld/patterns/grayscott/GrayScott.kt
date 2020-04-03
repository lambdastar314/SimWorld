/*
 * オライリー社出版「作って動かすALife」の方程式を引用
 */
package lambdastar314.simworld.patterns.grayscott

import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Deprecated("親パッケージのGrayScottModelに移行")
class GrayScott(
    val width: Int, val height: Int,
    //補充及び減量
    val f: Double = 0.022, val k: Double = 0.051,
    //拡散係数
    val Du: Double = 0.00002, val Dv: Double = 0.00001,
    //空間スケール
    val Dx: Double = 0.01,
    //時間スピード
    val Dt: Double = 1.0
) {

    var currentTick = 0//現在のステップ数を表す

    data class Cell(var u: Double, var v: Double)//u, v濃度を格納するクラス


    var pattern: Array<Array<Cell>>

    init {
        pattern =
            Array(width) { Array(height) { Cell(1.0, 0.0) } }
    }

    fun initialize() {
        val r = Random()
        for (x in 0 until width) {
            for (y in 0 until height) {
                //正方形にする
//                val xsize = 8
//                val xcenter = width / 2
//                val ysize = 8
//                val ycenter = height / 2
//                if (x in (xcenter - xsize)..(xcenter + xsize) && y in (ycenter - ysize)..(ycenter + ysize)) pattern[x][y].u =
//                    0.5 //+ (r.nextInt(100) - 50) / 100
//                if (x in (xcenter - xsize)..(xcenter + xsize) && y in (ycenter - ysize)..(ycenter + ysize)) pattern[x][y].v =
//                    0.25 //+ (r.nextInt(100) - 50) / 100
                //円形にする
                val radius = 8.0
                val xcenter = width / 2
                val ycenter = height / 2
                if (abs(x - xcenter) in 0..radius.toInt() && abs(y - ycenter) in 0..sqrt(
                        radius.pow(2.0) - (x - xcenter).toDouble().pow(
                            2.0
                        )
                    ).toInt()
                ) {
                    pattern[x][y].u =
                        0.5
                    pattern[x][y].v =
                        0.25
                }
            }
        }
    }

    fun tick() {
        diffusion()
        currentTick++
    }

    private fun diffusion() {
        val laplacian = Array(width) { Array(height) { Cell(0.0, 0.0) } }
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
    }
}