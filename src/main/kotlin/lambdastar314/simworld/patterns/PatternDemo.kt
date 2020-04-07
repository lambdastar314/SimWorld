package lambdastar314.simworld.patterns

import lambdastar314.simworld.util.Gradation
import lambdastar314.simworld.util.MergedList
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.JFrame

fun main() {
    val pattern = GrayScottModel()
    pattern.init(256, 256)
    val radius = 8.0
//    pattern.fillCircle((256 / 2 - radius).toInt(), (256 / 2 - radius).toInt(), radius)
//    pattern.fillSquare(120, 120, 8, 8)
    val r = Random()
//    pattern.fillRandom(0, 0, 255, 255, r)
//    pattern.fillRandomSquare(112, 112, 16,16,r)
    pattern.fillRandomCircle((256 / 2 - radius).toInt(), (256 / 2 - radius).toInt(), radius, r)
    val jf = JFrame("GrayScott")
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val canvas = PatternCanvas(pattern)
    jf.add(canvas)
    jf.isVisible = true
}

class PatternCanvas(private var pattern: IPattern) : Canvas() {
    var doPrint = false
    val colors = genColors()

    val loopthread = Thread(Runnable {
        while (true)
            repaint()
    })

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
//        val g = ng!!
        pattern.tick()
        System.gc()
        val image = BufferedImage(pattern.getSize().width, pattern.getSize().height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until pattern.getSize().width) {
            for (y in 0 until pattern.getSize().height) {
                val u = cut((pattern.get(x, y) * 255).toInt(), 0, 255)
                image.setRGB(x, y, colors[u].rgb)
            }
        }
        val buffer = BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB)
        val g = buffer.createGraphics()
        g.color = Color(0x40, 0x00, 0x0)
        g.fillRect(0, 0, 512, 512)
        g.drawImage(image, 0, 0, this)
        g.color = Color.WHITE
        g.drawString("${pattern.getCurrentTick()} tick", pattern.getSize().width / 2, pattern.getSize().height + 16)
        ng!!.drawImage(buffer, 0, 0, this)
        if (pattern.getCurrentTick() == 1) {
            loopthread.start()
        }
    }


    private fun cut(i: Int, min: Int, max: Int): Int {
        if (i < min)
            return min
        if (i > max)
            return max
        return i
    }

    private fun genColors(): List<Color> {
        val front = Gradation.calc(Color.BLACK, Color.GREEN, 128).asList()
        val center = Gradation.calc(Color.GREEN, Color.YELLOW, 64 - 32).asList()
        val back: List<Color> = Gradation.calc(Color.YELLOW, Color.RED, 64 + 32).asList()
        return MergedList(front, MergedList(center, back))
//        return Gradation.calc(Color.BLACK, Color.GREEN, 256).asList()
    }
}