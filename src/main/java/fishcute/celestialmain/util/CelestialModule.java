package fishcute.celestialmain.util;

import celestialexpressions.Function;
import celestialexpressions.FunctionList;
import celestialexpressions.Module;
import celestialexpressions.VariableList;
import fishcute.celestialmain.version.independent.Instances;

import java.util.Arrays;
import java.util.List;

public class CelestialModule {
    public static double toDouble(Object o) {
        return Double.parseDouble(o.toString());
    }
    public final static Module CELESTIAL_MODULE = new Module(
            "std",
            new VariableList()
                    .with("posX", () ->
                            Instances.minecraft.getPlayerX())
                    .with("posY", () ->
                            Instances.minecraft.getPlayerY())
                    .with("posZ", () ->
                            Instances.minecraft.getPlayerZ())
                    .with("headYaw", () ->
                            toDouble(Instances.minecraft.getViewXRot()))
                    .with("headPitch", () ->
                            toDouble(Instances.minecraft.getViewYRot()))
                    .with("leftClicking", () -> Instances.minecraft.isLeftClicking() ? 1.0 : 0.0)
                    .with("rightClicking", () ->
                            Instances.minecraft.isRightClicking() ? 1.0 : 0.0)
                    .with("submerged", () ->
                            Instances.minecraft.isCameraInWater() ? 1.0 : 0.0)
                    .with("rainAlpha", () ->
                            Instances.minecraft.getRainLevel())
                    .with("gameTime", () ->
                            toDouble(Instances.minecraft.getGameTime()))
                    .with("worldTime", () ->
                            toDouble(Instances.minecraft.getWorldTime()))
                    .with("dayTime", () ->
                            Util.repeat(Instances.minecraft.getWorldTime(), 0.0F, 24000.0F))
                    .with("skyAngle", () ->
                            (Instances.minecraft.getTimeOfDay() * 360.0D))
                    .with("viewDistance", () ->
                            toDouble(Instances.minecraft.getRenderDistance()))
                    .with("moonPhase", () ->
                            toDouble(Instances.minecraft.getMoonPhase()))
                    .with("skyDarken", () ->
                            toDouble(Instances.minecraft.getSkyDarken()))
                    .with("bossSkyDarken", () ->
                            toDouble(Instances.minecraft.getBossSkyDarken()))
                    .with("lightningFlashTime", () ->
                            toDouble(Instances.minecraft.getSkyFlashTime()))
                    .with("thunderAlpha", () ->
                            toDouble(Instances.minecraft.getThunderLevel()))
                    .with("skyLightLevel", () ->
                            toDouble(Instances.minecraft.getSkyLight()))
                    .with("blockLightLevel", () ->
                            toDouble(Instances.minecraft.getBlockLight()))
                    .with("biomeTemperature", () ->
                            toDouble(Instances.minecraft.getBiomeTemperature()))
                    .with("biomeDownfall", () ->
                            toDouble(Instances.minecraft.getBiomeDownfall()))
                    .with("biomeHasSnow", () ->
                            Instances.minecraft.getBiomeSnow() ? 1.0 : 0.0)
                    .with("fogStart", () ->
                            toDouble(Util.fogStart))
                    .with("fogEnd", () ->
                            toDouble(Util.fogEnd))
                    .with("sneaking", () ->
                            Instances.minecraft.isSneaking() ? 1.0 : 0.0)
                    .with("dayLight", () ->
                            Util.getDayLight((Instances.minecraft.getTimeOfDay() * 360.0D)))
                    .with("starAlpha", () ->
                            Util.getStarAlpha((Instances.minecraft.getTimeOfDay() * 360.0D)))
                    .with("tickDelta", () ->
                            toDouble(Instances.minecraft.getTickDelta()))
            ,
            new FunctionList()
                    .with("print", new Function((List<Object> arr) ->
                            Util.print(toDouble(arr.get(0)))
                            , 1))
                    .with("printnv", new Function((List<Object> arr) ->
                            Util.print(toDouble(arr.get(0)) * 0)
                            , 1))
                    .with("inBiome", new Function((List<Object> arr) ->
                            Util.isInBiome(Arrays.stream(arr.toArray()).toArray(String[]::new)) ? 1.0 : 0.0
                            , -1))
                    .with("distanceToBiome", new Function((List<Object> arr) ->
                            Util.getBiomeBlend(Arrays.stream(arr.toArray()).toArray(String[]::new)), -1))
                    .with("distanceToBiomeFlat", new Function((List<Object> arr) ->
                            Util.getBiomeBlendFlat(Arrays.stream(arr.toArray()).toArray(String[]::new)), -1))
                    .with("rightClickingWith", new Function((List<Object> arr) ->
                            Util.isUsing((String) arr.get(0)) ? 1.0 : 0.0
                            , 1))
                    .with("leftClickingWith", new Function((List<Object> arr) ->
                            Util.isMiningWith((String) arr.get(0)) ? 1.0 : 0.0
                            , 1))
                    .with("holding", new Function((List<Object> arr) ->
                            Util.isHolding((String) arr.get(0)) ? 1.0 : 0.0
                            , 1))
                    .with("distanceTo", new Function((List<Object> arr) ->
                            Util.distanceTo(
                                    Instances.minecraft.getPlayerX(),
                                    Instances.minecraft.getPlayerY(),
                                    Instances.minecraft.getPlayerZ(),
                                    toDouble(arr.get(0)),
                                    toDouble(arr.get(1)),
                                    toDouble(arr.get(2))
                            )
                            , 3))
                    .with("inArea", new Function((List<Object> arr) ->
                            Util.isInArea(
                                    toDouble(arr.get(0)),
                                    toDouble(arr.get(1)),
                                    toDouble(arr.get(2)),
                                    toDouble(arr.get(3)),
                                    toDouble(arr.get(4)),
                                    toDouble(arr.get(5))
                            ) ? 1.0 : 0.0
                            , 6))
                    .with("distanceToArea", new Function((List<Object> arr) ->
                            Util.distanceToArea(
                                    Instances.minecraft.getPlayerX(),
                                    Instances.minecraft.getPlayerY(),
                                    Instances.minecraft.getPlayerZ(),
                                    toDouble(arr.get(0)),
                                    toDouble(arr.get(1)),
                                    toDouble(arr.get(2)),
                                    toDouble(arr.get(3)),
                                    toDouble(arr.get(4)),
                                    toDouble(arr.get(5))
                            )
                            , 6))
                    .with("twilightAlpha", new Function((List<Object> arr) ->
                            Util.getTwilightAlpha(toDouble(arr.get(0)))
                            , 1))
                    .with("twilightProgress", new Function((List<Object> arr) ->
                            Util.getTwilightProgress(toDouble(arr.get(0)))
                            , 1))
                    .with("twilightFogEffect", new Function((List<Object> arr) ->
                            Util.getTwilightFogEffect(toDouble(arr.get(0)), 0)
                            , 1))
                    .with("twilightFogEffect", new Function((List<Object> arr) ->
                            Util.getTwilightFogEffect(toDouble(arr.get(0)), (float) toDouble(arr.get(1)))
                            , 2))
                    .with("starAlpha", new Function((List<Object> arr) ->
                            Util.getStarAlpha(toDouble(arr.get(0)))
                            , 1))
                    .with("dayLight", new Function((List<Object> arr) ->
                            Util.getDayLight(toDouble(arr.get(0)))
                            , 1))
                    .with("colorEntryRed", new Function((List<Object> arr) ->
                            Util.getRedFromColor((String) arr.get(0))
                            , 1))
                    .with("colorEntryGreen", new Function((List<Object> arr) ->
                            Util.getGreenFromColor((String) arr.get(0))
                            , 1))
                    .with("colorEntryBlue", new Function((List<Object> arr) ->
                            Util.getBlueFromColor((String) arr.get(0))
                            , 1))
                    .with("repeat", new Function((List<Object> arr) ->
                            Util.repeat(toDouble(arr.get(0)), toDouble(arr.get(1)), toDouble(arr.get(2)))
                            , 3))
                    .with("modulo", new Function((List<Object> arr) ->
                            Util.repeat(toDouble(arr.get(0)), 0, toDouble(arr.get(1)))
                            , 2))
                    .with("clamp", new Function((List<Object> arr) ->
                            Util.clamp(toDouble(arr.get(0)), toDouble(arr.get(1)), toDouble(arr.get(2)))
                            , 3))
                    .with("lerp", new Function((List<Object> arr) ->
                            Util.lerp(toDouble(arr.get(0)), toDouble(arr.get(1)), toDouble(arr.get(2)))
                            , 3))

    );
}
