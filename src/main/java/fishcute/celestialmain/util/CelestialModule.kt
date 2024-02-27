package fishcute.celestialmain.util

import celestialexpressions.*
import fishcute.celestial.version.dependent.VMinecraftInstance
import celestialexpressions.toDouble as _toDouble


fun Any.toDouble() = this._toDouble()

val module = Module("fishcute/celestial",
    VariableList(hashMapOf(
        "xPos" to {
            VMinecraftInstance.getPlayerX()
        },
        "yPos" to {
            VMinecraftInstance.getPlayerY()
        },
        "zPos" to {
            VMinecraftInstance.getPlayerZ()
        },
        "headYaw" to {
            VMinecraftInstance.getViewXRot().toDouble()
        },
        "headPitch" to {
            VMinecraftInstance.getViewYRot().toDouble()
        },
        "leftClicking" to {
            if (VMinecraftInstance.isLeftClicking()) 1.0 else 0.0
        },
        "rightClicking" to {
            if (VMinecraftInstance.isRightClicking()) 1.0 else 0.0
        },
        "submerged" to {
            if (VMinecraftInstance.isCameraInWater()) 1.0 else 0.0
        },
        "rainAlpha" to {
            VMinecraftInstance.getRainLevel()
        },
        "gameTime" to {
            VMinecraftInstance.getGameTime().toDouble()
        },
        "worldTime" to {
            VMinecraftInstance.getWorldTime().toDouble()
        },
        "dayTime" to {
            Util.repeat(VMinecraftInstance.getWorldTime().toFloat(), 0.0F, 24000.0F)
        },
        "skyAngle" to {
            (VMinecraftInstance.getTimeOfDay() * 360.0f).toDouble()
        },
        "viewDistance" to {
            VMinecraftInstance.getRenderDistance().toDouble()
        },
        "moonPhase" to {
            VMinecraftInstance.getMoonPhase().toDouble()
        },
        "skyDarken" to {
            VMinecraftInstance.getSkyDarken().toDouble()
        },
        "bossSkyDarken" to {
            VMinecraftInstance.getBossSkyDarken().toDouble()
        },
        "lightningFlashTime" to {
            VMinecraftInstance.getSkyFlashTime().toDouble()
        },
        "thunderAlpha" to {
            VMinecraftInstance.getThunderLevel().toDouble()
        },
        "skyLightLevel" to {
            VMinecraftInstance.getSkyLight().toDouble()
        },
        "blockLightLevel" to {
            VMinecraftInstance.getBlockLight().toDouble()
        },
        "biomeTemperature" to {
            VMinecraftInstance.getBiomeTemperature().toDouble()
        },
        "biomeDownfall" to {
            VMinecraftInstance.getBiomeDownfall().toDouble()
        },
        "biomeHasSnow" to {
            if (VMinecraftInstance.getBiomeSnow()) 1.0 else 0.0
        },
        "fogStart" to {
            Util.fogStart.toDouble()
        },
        "fogEnd" to {
            Util.fogEnd.toDouble()
        },
        "sneaking" to {
            if (VMinecraftInstance.isSneaking()) 1.0 else 0.0;
        }
    )),
    FunctionList(hashMapOf(
        "print" to Function({arr ->
            Util.print(
                arr[0] as Double
            )
        }, 1),
        "printnv" to Function({arr -> Util.print(
            arr[0] as Double
        ) * 0.0
        }, 1),
        "inBiome" to Function({arr ->
            if (Util.isInBiome(*arr.map { it.toString() }.toTypedArray())) 1.0 else 0.0
        }, -1),
        "distanceToBiome" to Function({arr ->
            Util.getBiomeBlend(*arr.map { it.toString() }.toTypedArray())
        }, -1),
        "distanceToBiomeFlat" to Function({arr ->
            Util.getBiomeBlendFlat(*arr.map { it.toString() }.toTypedArray())
        }, -1),
        "rightClickingWith" to Function({arr ->
            if (Util.isUsing(arr[0].toString())) 1.0 else 0.0
        }, 1),
        "holding" to Function({arr ->
            if (Util.isHolding(arr[0].toString())) 1.0 else 0.0
        }, 1),
        "leftClickingWith" to Function({arr ->
            if (Util.isMiningWith(arr[0].toString())) 1.0 else 0.0
        }, 1),
        "distanceTo" to Function({arr ->
            Util.distanceTo(
                VMinecraftInstance.getPlayerX(),
                VMinecraftInstance.getPlayerY(),
                VMinecraftInstance.getPlayerZ(),
                arr[0].toDouble(),
                arr[1].toDouble(),
                arr[2].toDouble()

            )
        }, 3),
        "inArea" to Function({arr ->
            if (Util.isInArea(
                    arr[0].toDouble(),
                    arr[1].toDouble(),
                    arr[2].toDouble(),
                    arr[3].toDouble(),
                    arr[4].toDouble(),
                    arr[5].toDouble()
                )
            )
                1.0 else 0.0;
        }, 6),
        "distanceToArea" to Function({arr ->
            Util.distanceToArea(
                VMinecraftInstance.getPlayerX(),
                VMinecraftInstance.getPlayerY(),
                VMinecraftInstance.getPlayerZ(),
                arr[0].toDouble(),
                arr[1].toDouble(),
                arr[2].toDouble(),
                arr[3].toDouble(),
                arr[4].toDouble(),
                arr[5].toDouble()
            )
        }, 6),
        "twilightAlpha" to Function({arr ->
            Util.getTwilightAlpha(arr[0].toDouble())
        }, 1),
        "twilightProgress" to Function({arr ->
            Util.getTwilightProgress(arr[0].toDouble())
        }, 1),
        "twilightFogEffect" to Function({arr ->
            Util.getTwilightFogEffect(arr[0].toDouble())
        }, 1),
        "starAlpha" to Function({arr ->
            Util.getStarAlpha(arr[0].toDouble())
        }, 1),
        "dayLight" to Function({arr ->
            Util.getDayLight(arr[0].toDouble())
        }, 1),
        "colorEntryRed" to Function({arr ->
            Util.getRedFromColor(arr[0].toString())
        }, 1),
        "colorEntryGreen" to Function({arr ->
            Util.getGreenFromColor(arr[0].toString())
        }, 1),
        "colorEntryBlue" to Function({arr ->
            Util.getBlueFromColor(arr[0].toString())
        }, 1),
        "repeat" to Function({arr ->
            Util.repeat(arr[0].toDouble().toFloat(), arr[1].toDouble().toFloat(), arr[2].toDouble().toFloat())
        }, 3),
        "modulo" to Function({arr ->
            Util.repeat(arr[0].toDouble().toFloat(), 0.0F, arr[1].toDouble().toFloat())
        }, 2),
        "clamp" to Function({arr ->
            Util.clamp(arr[0].toDouble().toFloat(), arr[1].toDouble().toFloat(), arr[2].toDouble().toFloat())
        }, 3),
        "lerp" to Function({arr ->
            Util.lerp(arr[0].toDouble().toFloat(), arr[1].toDouble().toFloat(), arr[2].toDouble().toFloat())
        }, 3)
    ))
);
