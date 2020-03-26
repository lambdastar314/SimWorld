package lambdastar314.simworld.patterns.grayscott

import lambdastar314.simworld.util.Gradation
import lambdastar314.simworld.util.MergedList
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.Timer

fun main() {
    val gs = GrayScott(256, 256, f = 0.03, k = 0.063
            )
    gs.initialize()
    val jf = JFrame("GrayScott")
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val canvas = TDCanvas(gs)
    jf.add(canvas)
    jf.isVisible = true
}

class TDCanvas(var gs: GrayScott) : Canvas() {
    var timer = Timer(1) {
        repaint()
    }

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ncg: Graphics?) {
        System.gc()
        gs.tick()
        val image = BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB)
        val g = image.graphics
        System.gc()
        g.color = Color(0x40, 0x00, 0x0)
        val colors = genColors()
        g.fillRect(0, 0, width, height)
        for (x in 0 until gs.width) {
            for (y in 0 until gs.height) {
                val c = gs.pattern[x][y]
                val u = cut((c.u * 255).toInt(), 0, 255)
//                val v = cut((c.v * 255).toInt(), 0, 255)
                val color = colors[u]//Color(0, u, 0)
                g.color = color
                g.fillRect(x, y, 1, 1)
            }
        }
        g.color = Color.WHITE
        g.drawString("${gs.currentTick} tick", 128, 256 - 8)
        ncg!!.drawImage(image, 0, 0, this)
        if (!timer.isRunning) timer.start()
    }

    private fun cut(i: Int, min: Int, max: Int): Int {
        if (i < min) return min
        if (i > max) return max
        return i
    }
    private fun genColors(): List<Color> {
        val front: List<Color> = Gradation.calc(Color.RED, Color.YELLOW, 64+32).asList()
        val center = Gradation.calc(Color.YELLOW, Color.GREEN, 64-32).asList()
        val back = Gradation.calc(Color.GREEN, Color.BLACK, 128).asList()
        return MergedList<Color>(front, MergedList(center, back))
    }
}