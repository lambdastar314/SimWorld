package lambdastar314.simworld.util

import java.awt.Color

object Gradation {
    fun calc(from: Color, to: Color, length: Int): Array<Color> {
        val diffr = to.red - from.red
        val diffg = to.green - from.green
        val diffb = to.blue - from.blue
        val stepr = diffr.toDouble() / length
        val stepg = diffg.toDouble() / length
        val stepb = diffb.toDouble() / length
        val array = Array<Color>(length) { Color.BLACK }
        for (i in 0 until length) {
            val red = from.red + (stepr * i).toInt()
            val green = from.green + (stepg * i).toInt()
            val blue = from.blue + (stepb * i).toInt()
            array[i] = Color(red, green, blue)
        }
        return array
    }
}