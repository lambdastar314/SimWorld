package lambdastar314.simworld.worldgenerators.noises

import lambdastar314.simworld.util.hash.Hash
import java.util.function.Function
import kotlin.math.pow

class MyNoise(var hash: Hash, var seed: Int, var f: Function<Double, Double>) : Noise {
    override fun noise(x: Double, y: Double, z: Double): Double {
        val dx = x % 1.0
        val dy = y % 1.0
        val dz = z % 1.0

        val X = (x - dx).toInt()
        val Y = (y - dy).toInt()
        val Z = (z - dz).toInt()

        val xyz = hash.hash3DDouble(X, Y, Z, seed, 1.0)
        val xyZ = hash.hash3DDouble(X, Y, Z + 1, seed, 1.0)
        val xYz = hash.hash3DDouble(X, Y + 1, Z, seed, 1.0)
        val xYZ = hash.hash3DDouble(X, Y + 1, Z + 1, seed, 1.0)
        val Xyz = hash.hash3DDouble(X + 1, Y, Z, seed, 1.0)
        val XyZ = hash.hash3DDouble(X + 1, Y, Z + 1, seed, 1.0)
        val XYz = hash.hash3DDouble(X + 1, Y + 1, Z, seed, 1.0)
        val XYZ = hash.hash3DDouble(X + 1, Y + 1, Z + 1, seed, 1.0)
        return (xyz * (1 - dx) * (1 - dy) * (1 - dz)) + (xyZ * (1 - dx) * (1 - dy) * dz) + (xYz * (1 - dx) * dy * (1 - dz)) + (xYZ * (1 - dx) * dy * dz) +
                (Xyz * dx * (1 - dy) * (1 - dz)) + (XyZ * dx * (1 - dy) * dz) + (XYz * dx * dy * (1 - dz)) + (XYZ * dx * dy * dz)
    }

    private fun interpolation(x0: Double, y0: Double, x1: Double, y1: Double, x2: Double): Double {
        val X0 = scan(y0)
        val X1 = scan(y1)
        val width = x1 - x0
        val dx = x2 - x0
        val per = dx / width
        val W = X1 - X0
        val X2 = W * per
        return f.apply(X2)
    }

    /**
     * 関数fに対して、yの値が一致するxを検出する。
     * 検出は二分探索にて行うため、f(x)に於いてxが増加するとyも増加することが必須である。
     */
    private fun scan(y: Double): Double {
        var i = 1.0
        var n = 0
        val limit = 100
        var current = 0
        while (current < limit) {
            val fy = f.apply(i)
            if (fy == y) break
            if (fy < y) {
                i += 2.0.pow(n)
            }
            if (fy > y) {
                i -= 2.0.pow(n)
                n--
            }
            current++
        }
        return i
    }
}