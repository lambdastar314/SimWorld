package lambdastar314.simworld.patterns

import java.awt.Dimension
import java.util.*

interface IPattern {

    data class TuringCell(var u: Double, var v: Double)

    fun get(x: Int, y: Int): Double

    fun tick()

    fun init(width: Int, height: Int)

    fun clear()

    fun fillSquare(x: Int, y: Int, width: Int, height: Int)

    fun fillCircle(x: Int, y: Int, radius: Double)

    fun fillRandomSquare(x: Int, y: Int, width: Int, height: Int, r: Random)

    fun fillRandomCircle(x: Int, y: Int, radius: Double, r: Random)

    fun getCurrentTick(): Int

    fun getSize(): Dimension
}