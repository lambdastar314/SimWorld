package lambdastar314.simworld.worldgenerators.noises

import lambdastar314.simworld.util.hash.Hash

class BlockNoise(var hash: Hash, var seed: Int): Noise {
    override fun noise(x: Double, y: Double, z: Double): Double {
        return hash.hash3DDouble(x.toInt(),y.toInt(),z.toInt(),seed,1.0)
    }
}