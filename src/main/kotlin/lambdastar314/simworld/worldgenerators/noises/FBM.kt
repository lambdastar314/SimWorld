package lambdastar314.simworld.worldgenerators.noises

class FBM(var noise: Noise, var octave: Int): Noise {
    override fun noise(x: Double, y: Double, z: Double): Double {
        var a = 1.0
        var f = 1.0
        var maxValue = 0.0
        var totalValue = 0.0
        val per = 0.5
        for (i in 0 until octave) {
            totalValue += a * noise.noise((x * f), (y * f), z)
            maxValue += a
            a *= per
            f *= 2.0f
        }
        return totalValue / maxValue
    }
}