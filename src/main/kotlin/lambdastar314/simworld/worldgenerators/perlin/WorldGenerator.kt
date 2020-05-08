package lambdastar314.simworld.worldgenerators.perlin

import lambdastar314.simworld.worldgenerators.noises.Noise

class WorldGenerator(val algorithm: Noise) {

    /**
     * Doubleで計算するクラス
     */
    fun generateOrigin(
        x: Int,
        y: Int,
        z: Double = 0.0,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0
    ): Double {
        return algorithm.noise(x / scaleX, y / scaleY, z)
    }

    /**
     * 256内で計算するクラス
     */
    fun generate(
        x: Int,
        y: Int,
        z: Double = 0.0,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
        base: Int = 64,
        amplitudeLower: Int = 32,
        amplitudeUpper: Int = 128
    ): Int {
        val origin = generateOrigin(
            x,
            y,
            z,
            scaleX,
            scaleY
        );
        return (base + (if (origin > 0) origin * amplitudeUpper else if (origin < 0) origin * amplitudeLower else 0.0)).toInt()
    }

    /**
     * チャンクを返すクラス
     */
    fun generateChunk(
        chunkX: Int,
        chunkY: Int,
        z: Double = 0.0,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
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
        z: Double = 0.0,
        scaleX: Double = 50.0,
        scaleY: Double = 50.0,
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