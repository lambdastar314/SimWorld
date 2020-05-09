package lambdastar314.simworld.worldgenerators.noises

import lambdastar314.simworld.util.hash.Hash
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

class ValueNoise(var hash: Hash, var seed: Int, var f: Function<Double, Double>) : Noise {

    fun noise1D(x: Double, y: Int, z: Int): Double {
        val dx = x % 1.0
        val X0 = (x - dx).toInt()
        val X1 = X0 + 1
        val before = hash.hash3DDouble(X0, y, z, seed, 1.0)
        val after = hash.hash3DDouble(X1, y, z, seed, 1.0)
        return interpolation(before, after, dx)
    }

    fun noise2D(x: Double, y: Double, z: Int): Double {
        val dy = y % 1.0
        val Y0 = (y - dy).toInt()
        val Y1 = Y0 + 1
        val before = noise1D(x, Y0, z)
        val after = noise1D(x, Y1, z)
        return interpolation(before, after, dy)
    }

    fun noise3D(x: Double, y: Double, z: Double): Double {
        val dz = z % 1.0
        val Z0 = (z - dz).toInt()
        val Z1 = Z0 + 1
        val before = noise2D(x, y, Z0)
        val after = noise2D(x, y, Z1)
        return interpolation(before, after, dz)
    }

    override fun noise(x: Double, y: Double, z: Double): Double = noise3D(x, y, z)

    /**
     * 内挿を行う
     * interpolation(Double, Double, Double, Double)の改良版
     */
    private fun interpolation(y0: Double, y1: Double, x2: Double): Double {
        return lerp(-1.0, y0, 1.0, y1, f.apply(x2))
    }

    /**
     * 線形補間をする
     */
    private fun lerp(
        x0: Double,
        y0: Double,
        x1: Double,
        y1: Double,
        x: Double
    ): Double {
        return y0 + (y1 - y0) * (x - x0) / (x1 - x0)
    }

    class SinFunction : Function<Double, Double> {
        private val pi = 3.141592653589793238462
        override fun apply(x: Double): Double =
            sin(x * pi + pi / 2 + pi)
    }

    class LinearFunction : Function<Double, Double> {
        override fun apply(x: Double): Double {
            return 2 * x - 1
        }
    }

    class CubicFunction : Function<Double, Double> {
        override fun apply(t: Double): Double {
            val x = t - 1
            val wavelet = (1 - 3 * x.pow(2) + 2 * abs(x).pow(3))
            return (wavelet - 0.5) * 2
        }
    }

    class QuinticFunction : Function<Double, Double> {
        override fun apply(t: Double): Double {
            val x = t - 1
            val wavelet = 1 - (6 * abs(x.pow(5)) - 15 * x.pow(4) + 10 * abs(x.pow(3)))
            return (wavelet - 0.5) * 2
        }
    }
}