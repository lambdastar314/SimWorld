package lambdastar314.simworld.worldgenerators.biomes

object BiomeDecliner {
    enum class Biome {
        DEEP_FLOZEN_OCEAN, FLOZEN_OCEAN,FLOZEN_PLAIN,
        DEEP_COLD_OCEAN, COLD_OCEAN, SNOW_PLAIN,
        DEEP_OCEAN, OCEAN, PLAIN, MOUNTAIN, NDEFINED,
        DEEP_WARM_OCEAN, WARM_OCEAN, SAVVANNA,;

        fun getName(): String {
            return when(this){
                DEEP_FLOZEN_OCEAN -> "凍った深海"
                DEEP_COLD_OCEAN -> "冷たい深海"
                DEEP_OCEAN -> "深海"
                DEEP_WARM_OCEAN -> "温かい深海"
                FLOZEN_OCEAN -> "凍った海"
                COLD_OCEAN -> "冷たい海"
                OCEAN -> "海"
                WARM_OCEAN -> "温かい海"
                FLOZEN_PLAIN -> "氷の平原"
                SNOW_PLAIN -> "雪原"
                PLAIN -> "平原"
                SAVVANNA -> "サバンナ"
                MOUNTAIN -> "山"
                NDEFINED -> "定義なし"
            }
        }
    }

    fun declineByHeight(height: Int): Biome {
        return when (height) {
            in 0..56 -> Biome.DEEP_OCEAN
            in 57..63 -> Biome.OCEAN
            in 64..127 -> Biome.PLAIN
            in 128..255 -> Biome.MOUNTAIN
            else -> Biome.NDEFINED
        }
    }

    fun declineByBothHT(height: Int, temperature: Int): Biome {
        if (height in 128..255) return Biome.MOUNTAIN
        return when(height) {
            //山 一応温度関係なし
            in 128..255 -> Biome.MOUNTAIN
            in 0..56 ->when(temperature){
                in 0..64 -> Biome.DEEP_FLOZEN_OCEAN
                in 65..96 -> Biome.DEEP_COLD_OCEAN
                in 97..160 -> Biome.DEEP_OCEAN
                in 160..255 -> Biome.DEEP_WARM_OCEAN
                else -> Biome.NDEFINED
            }
            in 57..64 ->when(temperature){
                in 0..64 -> Biome.FLOZEN_OCEAN
                in 65..96 -> Biome.COLD_OCEAN
                in 97..160 -> Biome.OCEAN
                in 160..255 -> Biome.WARM_OCEAN
                else -> Biome.NDEFINED
            }
            in 65..127 ->when(temperature){
                in 0..64 -> Biome.FLOZEN_PLAIN
                in 65..96 -> Biome.SNOW_PLAIN
                in 97..160 -> Biome.PLAIN
                in 160..255 -> Biome.SAVVANNA
                else -> Biome.NDEFINED
            }
            else -> Biome.NDEFINED
        }
    }
}