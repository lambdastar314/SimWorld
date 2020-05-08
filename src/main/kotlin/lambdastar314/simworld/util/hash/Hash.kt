package lambdastar314.simworld.util.hash

import kotlin.math.pow

interface Hash {
    fun hash(value: Int): Int

    fun hash(value: Int, bound: Int): Int = hash(value) % bound
    fun hashDouble(value: Int, bound: Double): Double = (hash(value) / 2.0.pow(16)) % bound

    fun hash3D(x: Int, y: Int, z: Int, seed: Int): Int {
        var v = hash(x)
        v = hash(v) + y
        v = hash(v) + z
        v = hash(v) + seed
        return v
    }
    fun hash3DDouble(x: Int, y: Int, z: Int, seed: Int, bound: Double): Double {
        var v = hash(x)
        v = hash(v) + y
        v = hash(v) + z
        v = hash(v) + seed
        return (v / 2.0.pow(16)) % bound
    }
}