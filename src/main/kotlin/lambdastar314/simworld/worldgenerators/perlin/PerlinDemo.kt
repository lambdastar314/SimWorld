package lambdastar314.simworld.worldgenerators.perlin

import lambdastar314.simworld.worldgenerators.biomes.BiomeDecliner
import lambdastar314.simworld.worldgenerators.biomes.BiomeDecliner.Biome.*
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.image.BufferedImage
import javax.swing.JFrame

fun main() {
    val jf = JFrame("Perlin")
    val canvas = MCanvas()
    canvas.addMouseMotionListener(canvas)
    canvas.addMouseWheelListener(canvas)
    jf.add(canvas)
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jf.isVisible = true
}

class MCanvas : MouseMotionListener, MouseWheelListener, Canvas() {

    var map = Array(512) { Array(512) { BiomeDecliner.Biome.NDEFINED } }
    var point = Point(0, 0)
    val z = 1.0
    var scale = 400.0
    var fromX = 0
    var fromY = 0

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
        if (map[0][0] == NDEFINED) updateMap()
        System.gc()
        val g = ng!!
        val image = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB)
        val ig = image.createGraphics()
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                ig.color = when (map[x][y]) {
                    MOUNTAIN -> Color(0, 0xff, 0x80)
                    NDEFINED -> Color.BLACK
                    DEEP_FLOZEN_OCEAN -> Color(0, 0x30, 0xf0)
                    DEEP_COLD_OCEAN -> Color(0, 0x40, 0x80)
                    DEEP_OCEAN -> Color(0, 0, 0x80)
                    DEEP_WARM_OCEAN -> Color(0, 0x80, 0xa0)
                    FLOZEN_OCEAN -> Color(0, 0x70, 0xf0)
                    COLD_OCEAN -> Color(0, 0x0, 0xc0)
                    OCEAN -> Color(0, 0, 0xff)
                    WARM_OCEAN -> Color(0, 0xa0, 0xc0)
                    FLOZEN_PLAIN -> Color(0xc0, 0xc0, 0xff)
                    SNOW_PLAIN -> Color(0xff, 0xff, 0xff)
                    PLAIN -> Color(0, 0x80, 0)
                    SAVVANNA -> Color(0xff, 0x80, 0)
                }
                ig.fillRect(x, y, 1, 1)
//                g.drawImage(image, 0, 0, this)
            }
        }

        ig.font = Font("Noto Sans CJK JP Regular", Font.PLAIN, 11)
        ig.color = Color.RED
        ig.drawString(map[point.x][point.y].getName(), 256, 512 - (32 + 8 + 11))
        ig.drawString(
            "高度: ${PerlinNoise.generate(
                point.x + fromX,
                point.y + fromY,
                z,
                scaleX = scale,
                scaleY = scale
            )}, 温度:${PerlinNoise.generate(
                point.x + fromX, point.y + fromY, z + (Integer.MAX_VALUE - 1),
                base = 128,
                amplitudeLower = 128,
                amplitudeUpper = 128, octave = 4, scaleX = scale / 2, scaleY = scale / 2
            )}度", 256, 512 - (32 + 8)
        )
        g.drawImage(image, 0, 0, this)
    }

    fun updateMap() {
        val xx = width / 16
        val xy = height / 16
        println("$fromX, $fromY")
        val regionSize = 512
        val reg = PerlinNoise.generateRegion(
            regionSize,
            fromX,
            fromY,
            z,
            scaleX = scale,
            scaleY = scale
        )
        val regT = PerlinNoise.generateRegion(
            regionSize, fromX, fromY, z + (Integer.MAX_VALUE - 1),
            base = 128,
            amplitudeLower = 128,
            amplitudeUpper = 128, octave = 4, scaleX = scale / 2, scaleY = scale / 2
        )

        for (x in 0 until 512) {
            for (y in 0 until 512) {
//                val n = PerlinNoise.generate(x, y, z, octave=16, scaleX = 100.0, scaleY = 100.0)
                val h = reg[x][y]
                val t = regT[x][y]
                map[x][y] =
                    BiomeDecliner.declineByBothHT(h, t)
            }
        }
        val biome = map[point.x][point.y]
    }

    override fun mouseMoved(p0: MouseEvent?) {
        point = p0!!.point
        repaint();
    }

    override fun mouseDragged(p0: MouseEvent?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mouseWheelMoved(p0: MouseWheelEvent?) {
        if (p0!!.preciseWheelRotation < 0) {
            scale *= 2.0
        } else {
            scale /= 2.0
        }
        if (p0.preciseWheelRotation < 0) {
            fromX += (p0.point.x - 256) / 2
            fromY += (p0.point.y - 256) / 2
        } else {
            fromX += (p0.point.x - 256) / 2
            fromY += (p0.point.y - 256) / 2
        }
        updateMap()
        repaint()
    }
}