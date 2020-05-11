package lambdastar314.simworld.worldgenerators

import lambdastar314.simworld.util.hash.XORHash
import lambdastar314.simworld.worldgenerators.biomes.BiomeDecliner
import lambdastar314.simworld.worldgenerators.biomes.BiomeDecliner.Biome.*
import lambdastar314.simworld.worldgenerators.noises.FBM
import lambdastar314.simworld.worldgenerators.noises.ValueNoise
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener
import java.awt.image.BufferedImage
import java.lang.Integer.max
import java.lang.Integer.min
import javax.swing.JFrame
import javax.swing.Timer

fun main() {
    val jf = JFrame("Noise")
    val canvas = LinearNoiseCanvas()
    jf.add(canvas)
    jf.setSize(512, 512)
    jf.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    jf.isVisible = true
}

class WorldCanvas : MouseMotionListener, MouseWheelListener, Canvas() {
    init {

        addMouseMotionListener(this)
        addMouseWheelListener(this)
    }

    var map = Array(512) { Array(512) { NDEFINED } }
    var point = Point(0, 0)
    val z = 1.0
    var scale = 400.0
    var fromX = 0
    var fromY = 0
    var hgenerator = WorldGenerator(FBM(ValueNoise(XORHash(), 314159265, ValueNoise.SinFunction()), 16))
    var tgenerator = WorldGenerator(FBM(ValueNoise(XORHash(), 141421356, ValueNoise.SinFunction()), 8))

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
        if (map[0][0] == NDEFINED) {
            ng!!.font = Font("Noto Sans CJK JP Regular", Font.PLAIN, 11)
            ng.drawString("ただ今更新中です", 128, 128)
            updateMap()
        }
        System.gc()
        val g = ng!!
        val image = BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB)
        val ig = image.createGraphics()
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                ig.color = when (map[x][y]) {
                    MOUNTAIN -> Color(0, 0xff, 0x80)
                    NDEFINED -> Color.BLACK
                    DEEP_FROZEN_OCEAN -> Color(0, 0x30, 0xf0)
                    DEEP_COLD_OCEAN -> Color(0, 0x40, 0x80)
                    DEEP_OCEAN -> Color(0, 0, 0x80)
                    DEEP_WARM_OCEAN -> Color(0, 0x80, 0xa0)
                    FROZEN_OCEAN -> Color(0, 0x70, 0xf0)
                    COLD_OCEAN -> Color(0, 0x0, 0xc0)
                    OCEAN -> Color(0, 0, 0xff)
                    WARM_OCEAN -> Color(0, 0xa0, 0xc0)
                    FROZEN_PLAIN -> Color(0xc0, 0xc0, 0xff)
                    SNOW_PLAIN -> Color(0xff, 0xff, 0xff)
                    PLAIN -> Color(0, 0x80, 0)
                    SAVANNA -> Color(0xff, 0x80, 0)
                }
                ig.fillRect(x, y, 1, 1)
//                g.drawImage(image, 0, 0, this)
            }
        }

        ig.font = Font("Noto Sans CJK JP Regular", Font.PLAIN, 11)
        ig.color = Color.RED
        ig.drawString(map[point.x][point.y].getName(), 256, 512 - (32 + 8 + 11))
        ig.drawString(
            "高度: ${hgenerator.generate(
                point.x + fromX,
                point.y + fromY,
                z,
                scaleX = scale,
                scaleY = scale
            )}, 温度:${tgenerator.generate(
                point.x + fromX, point.y + fromY, z + (Integer.MAX_VALUE - 1),
                base = 128,
                amplitudeLower = 128,
                amplitudeUpper = 128, scaleX = scale / 2, scaleY = scale / 2
            )}度", 256, 512 - (32 + 8)
        )
        g.drawImage(image, 0, 0, this)
    }

    fun updateMap() {
        println("$fromX, $fromY")
        val regionSize = 512
        val reg = hgenerator.generateRegion(
            regionSize,
            fromX,
            fromY,
            z,
            scaleX = scale,
            scaleY = scale
        )

        val regT = tgenerator.generateRegion(
            regionSize, fromX, fromY, z + (Integer.MAX_VALUE - 1),
            base = 128,
            amplitudeLower = 128,
            amplitudeUpper = 128, scaleX = scale / 2, scaleY = scale / 2
        )

        for (x in 0 until 512) {
            for (y in 0 until 512) {
                val h = reg[x][y]
                val t = regT[x][y]
                map[x][y] =
                    BiomeDecliner.declineByBothHT(h, t)
            }
        }
    }

    override fun mouseMoved(p0: MouseEvent?) {
        point = p0!!.point
        repaint()
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

class NoiseCanvas : Canvas() {
    var z = 0.0
    val timer = Timer(0) { repaint() }

        var generator = WorldGenerator(ValueNoise(XORHash(), 1, ValueNoise.QuinticFunction()).toFractal(16))
//    var generator = WorldGenerator(ImprovedNoise().toFractal(16))
//    var generator = WorldGenerator(BlockNoise(XORHash(), 1).toFractal(16))

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(ng: Graphics?) {
        val mapsize = min(width, height)
        val image = BufferedImage(mapsize, mapsize, BufferedImage.TYPE_INT_RGB)
        val map = generator.generateRegion(
            (width + height) / 2,
            0,
            0,
            z,
            base = 128,
            amplitudeUpper = 128,
            amplitudeLower = 128
        )
        for (x in 0 until mapsize) {
            for (y in 0 until mapsize) {
                val v = cut(map[x][y], 0, 255)
                image.setRGB(x, y, Color(v, v, v).rgb)
            }
        }
        ng!!.drawImage(image, 0, 0, this)
        z += 1 / 50.0
        if (!timer.isRunning) timer.start()
    }

    private fun cut(value: Int, min: Int, max: Int): Int {
        return max(min(value, max), min)
    }
}

class LinearNoiseCanvas : Canvas() {
    var offset = 0
    var yoff = 0.0
    var z = 0.0
    val timer = Timer(10) { repaint() }
    var noise = ValueNoise(XORHash(), 1, ValueNoise.SinFunction())
//    var generator = WorldGenerator(MyNoise(XORHash(), 1, MyNoise.CubicFunction()))

    override fun update(g: Graphics?) {
        paint(g)
    }

    override fun paint(g: Graphics?) {
        val mapsize = min(width, height)
        val amplitudes = mapsize / 2
        val image = BufferedImage(mapsize, mapsize, BufferedImage.TYPE_INT_RGB)
        val ig = image.createGraphics()
        val scale = 32.0
        ig.color = Color.BLACK
        ig.fillRect(0, 0, mapsize, mapsize)
        for (i in 0 until mapsize) {
            val v = (noise.noise((offset + i) / scale, yoff, z) * amplitudes) + amplitudes
            val before = (noise.noise((offset + i - 1) / scale, yoff, z) * amplitudes) + amplitudes
            ig.color = Color.GREEN
            ig.drawLine(i - 1, before.toInt(), i, v.toInt())
//            if((i+offset) % scale.toInt() == 0){
//                ig.color = Color.BLUE
//                ig.drawLine(i,0,i,mapsize)
//            }
        }
        offset--
//        z += 0.01
        g!!.drawImage(image, 0, 0, this)
        if (!timer.isRunning) timer.start()
    }
}