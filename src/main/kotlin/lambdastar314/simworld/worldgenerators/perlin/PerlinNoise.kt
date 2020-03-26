package lambdastar314.simworld.worldgenerators.perlin

import lambdastar314.simworld.worldgenerators.perlin.ImprovedNoise

/**
 * パーリンノイズを生成するクラス
 */
object PerlinNoise/*(val noise: ImprovedNoise)*/ {

    /**
     * 256内で計算するクラス
     */
    fun generate(
        x: Int,
        y: Int,
        z: Double,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
        octave: Int = 16,
        base: Int = 64,
        amplitudeLower: Int = 32,
        amplitudeUpper: Int = 128
    ): Int {
        val origin = generateOrigin(
            x,
            y,
            z,
            scaleX,
            scaleY,
            octave
        );
        return (base + (if (origin > 0) origin * amplitudeUpper else if (origin < 0) origin * amplitudeLower else 0.0)).toInt()
    }

    /**
     * Doubleで計算するクラス
     */
    fun generateOrigin(
        x: Int,
        y: Int,
        z: Double,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
        octave: Int = 16
    ): Double {
        val rx = x.toDouble() / scaleX
        val ry = y.toDouble() / scaleY
        var a = 1.0
        var f = 1.0
        var maxValue = 0.0
        var totalValue = 0.0
        val per = 0.5
        for (i in 0 until octave) {
            totalValue += a * ImprovedNoise.noise((rx * f), (ry * f), z)
            maxValue += a
            a *= per
            f *= 2.0f
        }
        return totalValue / maxValue
    }

    /**
     * チャンクを返すクラス
     */
    fun generateChunk(
        chunkX: Int,
        chunkY: Int,
        z: Double,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
        octave: Int = 16,
        base: Int = 64,
        amplitudeLower: Int = 32,
        amplitudeUpper: Int = 128
    ): Array<Array<Int>> {
        val chunkSize = 16
        val chunk: Array<Array<Int>> = Array(chunkSize) { Array(chunkSize) { 0 } }
        val minX = chunkX * chunkSize
        val minY = chunkY * chunkSize
        for (x in 0 until chunkSize) {
            for (y in 0 until chunkSize) {
                val noise =
                    generate(
                        x + minX,
                        y + minY,
                        z,
                        scaleX,
                        scaleY,
                        octave,
                        base,
                        amplitudeLower,
                        amplitudeUpper
                    )
                chunk[x][y] = noise
            }
        }
        return chunk
    }

    /**指定範囲を生成
     */
    fun generateRegion(
        regionSize: Int, fromX: Int, fromY: Int,
        z: Double,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
        octave: Int = 16,
        base: Int = 64,
        amplitudeLower: Int = 32,
        amplitudeUpper: Int = 128
    ): Array<Array<Int>> {
        val region = Array(regionSize) { Array(regionSize) { 0 } }
        for (x in 0 until regionSize) {
            for (y in 0 until regionSize) {
                val noise =
                    generate(
                        x + fromX,
                        y + fromY,
                        z,
                        scaleX,
                        scaleY,
                        octave,
                        base,
                        amplitudeLower,
                        amplitudeUpper
                    )
                region[x][y] = noise
            }
        }
        return region
    }
}