package lambdastar314.simworld.util.hash

class XORHash: Hash {
    override fun hash(value: Int): Int {
        var v = value
        v = v.xor(v.shl(13))
        v = v.xor(v.shr(17))
        v = v.xor(v.shl(5))
        return v
    }
}