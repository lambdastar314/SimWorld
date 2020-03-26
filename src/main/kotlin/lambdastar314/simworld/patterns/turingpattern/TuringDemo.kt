package lambdastar314.simworld.patterns.turingpattern

import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics
import java.awt.Point
import java.awt.event.*
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.Timer
import kotlin.math.absoluteValue
import kotlin.math.pow

fun main() {
    val turing = TuringPattern(256, 256)
    turing.initialize(z = 0.0)
    val jf = JFrame("Turing")
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val canvas = TDCanvas(turing)
    canvas.addMouseListener(canvas)
    jf.add(canvas)
    jf.isVisible = true
}

class PDCanvas(private var turing: TuringPattern) : Canvas() {
    override fun paint(ng: Graphics?) {
        while (true) {
            val image = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB)
            val g = image.graphics
            System.gc()
            g.color = Color(0x40, 0x00, 0x0)
            g.fillRect(0, 0, width, height)
            turing.tick()
            val activator = (turing.pattern[0][0].activator * 64).toInt()
            val inhibitor = (turing.pattern[0][0].inhibitor * 64).toInt()
            print("${turing.currentTick} tick : $activator, $inhibitor\r")
            g.color = Color.GREEN
            g.fillRect(196, 0, 16, activator)
            g.color = Color.BLUE
            g.fillRect(212, 0, 16, inhibitor)

            g.color = Color.BLACK
            g.drawString("${turing.currentTick} tick", 256, 256)
            ng!!.drawImage(image, 0, 0, this)
        }
    }
}

class ODCanvas(private var turing: TuringPattern) : Canvas() {

    override fun paint(ng: Graphics?) {
        val image = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB)
        val g = image.graphics
        while (true) {
            System.gc()
            g.color = Color(0xff, 0x80, 0x0)
            g.fillRect(0, 0, width, height)
            turing.tick()
            var oldU = 0
            var oldV = 0
            for (x in 0 until turing.width) {
                g.color = Color.GREEN
                val activator = (turing.pattern[x][0].activator * 32).toInt()
                g.fillRect(2 * x, 0, 2, activator)
                oldU = activator
                g.color = Color(0, 0, 0xff, 0x50)
                val inhibitors = (turing.pattern[x][0].inhibitor * 32).toInt()
                g.fillRect(2 * x, 0, 2, inhibitors)
                oldV = inhibitors
            }
            g.color = Color.BLACK
            g.drawString("${turing.currentTick} tick", 256, 256)
            ng!!.drawImage(image, 0, 0, this)
        }
    }
}

class TDCanvas(private var turing: TuringPattern) : MouseListener, Canvas() {
    private var removesize = 4
    private var timer = Timer(0) {
        repaint()
    }

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
        System.gc()
        turing.tick()
        //描画する
        val image = BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB)
        val g = image.graphics
        g.color = Color(0x40, 0x00, 0x0)
        g.fillRect(0, 0, width, height)
        for (x in 0 until turing.width) {
            for (y in 0 until turing.height) {
                val v = turing.pattern[x][y]
                val per = 2.0.pow(8.0).toInt()
                val cell = v.activator * per
//                val inhibitor = v.inhibitor * per
                val c: Int = cut(cell.toInt(), 0, 255)
//                val inh: Int = cut(inhibitor.toInt(), 0, 255)
                g.color = Color(0, c, 0)
                g.fillRect(x, y, 1, 1)
            }
        }

        g.color = Color.WHITE
        g.drawString("${turing.currentTick} tick", 128, 256 - 8)
        ng!!.drawImage(image, 0, 0, this)
        if (!timer.isRunning) timer.start()
        //一つ一つ撮って動画とかにしたい場合はここのコメントを消す
//            if(turing.currentTick % 2 == 1) {
//                val file = File("/home/betelgeuse/画像/rd/pic/${turing.currentTick}.png")
//                file.createNewFile()
//                ImageIO.write(image, "png", file)
//            }
    }


    private fun cut(i: Int, min: Int, max: Int): Int {
        if (i < min) return min
        if (i > max) return max
        return i
    }

    override fun mouseReleased(p0: MouseEvent?) {
    }

    override fun mouseEntered(p0: MouseEvent?) {
    }

    override fun mouseClicked(p0: MouseEvent?) {
    }

    override fun mouseExited(p0: MouseEvent?) {
    }

    override fun mousePressed(p0: MouseEvent?) {
        //マウスでパターンを崩させる
        when (p0!!.button) {
            MouseEvent.BUTTON1 -> {
                //範囲を小さく
                removesize--
            }
            MouseEvent.BUTTON3 -> {
                //範囲を大きく
                removesize++
            }
            MouseEvent.BUTTON2 -> {
                //崩す
                timer.stop()
                Thread.sleep(10)
                val p = p0.point
                val point = Point(p.x % turing.width, p.y % turing.height)
                val xmin = point.x - width / 2.0.pow(removesize).toInt()
                val xmaj = point.x + width / 2.0.pow(removesize).toInt()
                val ymin = point.y - height / 2.0.pow(removesize).toInt()
                val ymaj = point.y + height / 2.0.pow(removesize).toInt()

                for (x in xmin..xmaj) {
                    for (y in ymin..ymaj) {
                        turing.pattern[(x % turing.width).absoluteValue][(y % turing.height).absoluteValue].activator =
                            0.0
                        turing.pattern[(x % turing.width).absoluteValue][(y % turing.height).absoluteValue].inhibitor =
                            0.0
                    }
                }
                timer.start()
            }
        }
    }
}