package lambdastar314.simworld.worldgenerators.noises

interface Noise {
    fun noise(x: Double, y: Double, z: Double): Double

    fun toFractal(octave: Int): FBM = FBM(this, octave)
}