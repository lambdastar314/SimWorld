package lambdastar314.simworld.lifegame

import lambdastar314.simworld.patterns.PatternCanvas
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import java.util.*
import java.util.function.Predicate
import javax.swing.JFrame
import javax.swing.Timer

fun main() {
    val pattern = LifeGame(Predicate {
        if (it.first) {
            it.second == 2 || it.second == 3
        } else {
            it.second == 3
        }
    })
    pattern.init(128, 128)
    val r = Random()
    for (x in 0 until pattern.width) {
        for (y in 0 until pattern.height) {
            pattern.pattern[x][y] = r.nextBoolean()
        }
    }
    val jf = JFrame("Pattern")
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val canvas = LifeGameCanvas(pattern)
    jf.add(canvas)
    jf.isVisible = true
}

class LifeGameCanvas(val pattern: LifeGame) : Canvas() {
    val cellsize = 4
    val timer = Timer(100, ActionListener {
        repaint()
    })

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
        pattern.tick()
        System.gc()
        val buffer = BufferedImage(pattern.width * cellsize, height * cellsize, BufferedImage.TYPE_INT_RGB)
        val g = buffer.createGraphics()
        g.color = Color.BLACK
        g.fillRect(0, 0, buffer.width, buffer.height)
        g.color = Color.GREEN
        for (x in 0 until pattern.width) {
            for (y in 0 until pattern.height) {
                if (pattern.pattern[x][y]) g.fillRect(x * cellsize, y * cellsize, cellsize, cellsize)
            }
        }
        print("${pattern.cTick}\r")
        ng!!.drawImage(buffer, 0, 0, this)
        if (!timer.isRunning) timer.start()
    }
}