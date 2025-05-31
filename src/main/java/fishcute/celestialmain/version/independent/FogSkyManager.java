package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.FMath;
import fishcute.celestialmain.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import java.awt.*;

public class FogSkyManager {
    public static float fogRed;
    public static float fogGreen;
    public static float fogBlue;
    private static int targetBiomeFog = -1;
    private static int previousBiomeFog = -1;
    private static long biomeChangedTime = -1L;

    public static float skyRed;
    public static float skyGreen;
    public static float skyBlue;

    /**
     * Returns the configured fog start
     *
     * @return Fog start
     */
    public static float getFogStart() {
        if (Instances.minecraft.isCameraInWater()) {
            return CelestialSky.getDimensionRenderInfo().environment.waterFogStart.invoke();
        }
        else {
            return CelestialSky.getDimensionRenderInfo().environment.fogStart.invoke();
        }
    }

    /**
     * Returns the configured fog end
     *
     * @return Fog end
     */
    public static float getFogEnd() {
        if (Instances.minecraft.isCameraInWater()) {
            return CelestialSky.getDimensionRenderInfo().environment.waterFogEnd.invoke();
        }
        else {
            return CelestialSky.getDimensionRenderInfo().environment.fogEnd.invoke();
        }
    }

    /**
     * Returns whether the fog color should be affected by sky effects
     *
     * @return Should fog be affected by sky effects
     */
    public static boolean shouldFogColorIgnoreSkyEffects() {
        return CelestialSky.getDimensionRenderInfo().environment.fogColor.ignoreSkyEffects;
    }

    /**
     * Sets up the land fog color if the camera is in air, or the water fog color if the camera is in water
     */
    public static void setupFogColor() {
        if (Instances.minecraft.isCameraInWater()) {
            setupWaterFogColor();
        }
        else {
            setupLandFogColor();
        }
    }

    /**
     * Sets up the land fog color. Applies modifications such as render distance fade, the rain and thunder tint, and darkness depending on the time
     */
    public static void setupLandFogColor() {
        // Base fog color
        fogRed = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredRed();
        fogGreen = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredGreen();
        fogBlue = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredBlue();

        if (!shouldFogColorIgnoreSkyEffects()) {
            // Color modification based on render distance
            float fogViewDistanceModifier = (float) (1.0F - Math.pow(0.25F + 0.75F * Instances.minecraft.getRenderDistance() / 32.0F, 0.25F));

            fogRed = (float) Util.lerp(fogRed, skyRed, 1 - fogViewDistanceModifier);
            fogGreen = (float) Util.lerp(fogGreen, skyGreen, 1 - fogViewDistanceModifier);
            fogBlue = (float) Util.lerp(fogBlue, skyBlue, 1 - fogViewDistanceModifier);

            // Rain color modification

            float rainAlpha = (float) Instances.minecraft.getRainLevel();

            if (rainAlpha > 0) {
                fogRed *= 1.0F - rainAlpha * 0.5F;
                fogGreen *= 1.0F - rainAlpha * 0.5F;
                fogBlue *= 1.0F - rainAlpha * 0.4F;
            }

            // Thunder color modification

            float thunderAlpha = (float) Instances.minecraft.getThunderLevel();

            if (thunderAlpha > 0) {
                fogRed *= 1.0F - thunderAlpha * 0.5F;
                fogGreen *= 1.0F - thunderAlpha * 0.5F;
                fogBlue *= 1.0F - thunderAlpha * 0.5F;
            }

            // Time color modification

            float dayAlpha = (float) Util.clamp(Math.cos(Instances.minecraft.getTimeOfDay() * 6.2831855F) * 2 + 0.5, 0, 1);

            fogRed *= dayAlpha * 0.94F + 0.06F;
            fogGreen *= dayAlpha * 0.94F + 0.06F;
            fogBlue *= dayAlpha * 0.91F + 0.09F;

            // Boss fog color modification
            float bossSkyDarken = Math.max(Instances.minecraft.getBossSkyDarken(), 0);

            if (bossSkyDarken > 0.0F) {
                fogRed *= (1.0F - bossSkyDarken) + 0.7F * bossSkyDarken;
                fogGreen *= (1.0F - bossSkyDarken) + 0.6F * bossSkyDarken;
                fogBlue *= (1.0F - bossSkyDarken) + 0.6F * bossSkyDarken;
            }
        }

        biomeChangedTime = -1L;
    }

    /**
     * Returns whether the water fog color should be affected by water vision and fog color transitions
     *
     * @return Should water fog color be affected by sky effects
     */
    public static boolean shouldWaterFogColorIgnoreSkyEffects() {
        return CelestialSky.getDimensionRenderInfo().environment.waterFogColor.ignoreSkyEffects;
    }

    /**
     * Sets up the water fog color. Includes water vision and water fog color transitions
     */
    public static void setupWaterFogColor() {
        if (!shouldWaterFogColorIgnoreSkyEffects()) {
            long currentTime = System.currentTimeMillis();

            // Water fog color defined in configuration
            int waterFogColor = new Color(
                    CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredRed(),
                    CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredGreen(),
                    CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredBlue()
            ).getRGB();

            // Sets water fog color when player enters water
            if (biomeChangedTime < 0L) {
                targetBiomeFog = waterFogColor;
                previousBiomeFog = waterFogColor;
                biomeChangedTime = currentTime;
            }

            // Extracts water fog colors
            int targetBiomeFogRed = targetBiomeFog >> 16 & 255;
            int targetBiomeFogGreen = targetBiomeFog >> 8 & 255;
            int targetBiomeFogBlue = targetBiomeFog & 255;

            int previousBiomeFogRed = previousBiomeFog >> 16 & 255;
            int previousBiomeFogGreen = previousBiomeFog >> 8 & 255;
            int previousBiomeFogBlue = previousBiomeFog & 255;

            // Lerps the previous biome fog color into the targeted biome fog color. Does this over 5 seconds
            float lerpFactor = (float) Util.clamp((currentTime - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);

            float waterFogRed = (float) Util.lerp(targetBiomeFogRed, previousBiomeFogRed, lerpFactor);
            float waterFogGreen = (float) Util.lerp(targetBiomeFogGreen, previousBiomeFogGreen, lerpFactor);
            float waterFogBlue = (float) Util.lerp(targetBiomeFogBlue, previousBiomeFogBlue, lerpFactor);

            fogRed = waterFogRed / 255.0F;
            fogGreen = waterFogGreen / 255.0F;
            fogBlue = waterFogBlue / 255.0F;

            // Sets a new target biome fog if the configured water fog color changes
            if (targetBiomeFog != waterFogColor) {
                targetBiomeFog = waterFogColor;
                previousBiomeFog = (int) Math.floor(waterFogRed) << 16 | (int) Math.floor(waterFogGreen) << 8 | (int) Math.floor(waterFogBlue);
                biomeChangedTime = currentTime;
            }

            // Handles some weird water vision stuff that minecraft does
            float waterVision = Instances.minecraft.getWaterVision();

            if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
                float w = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
                fogRed = fogRed * (1.0F - waterVision) + fogRed * w * waterVision;
                fogGreen = fogGreen * (1.0F - waterVision) + fogGreen * w * waterVision;
                fogBlue = fogBlue * (1.0F - waterVision) + fogBlue * w * waterVision;
            }
        }
        else {
            // Has to ignore water vision effects otherwise it'll look off
            fogRed = CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredRed();
            fogGreen = CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredGreen();
            fogBlue = CelestialSky.getDimensionRenderInfo().environment.waterFogColor.getStoredBlue();
        }
    }

    /**
     * Returns the Celestial fog color
     *
     * @return Fog color
     */
    public static float[] getFogColor() {
        return new float[]{fogRed, fogGreen, fogBlue};
    }

    /**
     * Returns the fog color specified by the configuration. Includes fog effects such as night vision and darkness
     *
     * @return Modified fog color RGB as a float array
     */
    public static float[] getFogColorModified() {
        float[] color = getFogColor();

        // Applies the night vision fog effect
        if (Instances.minecraft.getNightVisionModifier() > 0) {
            float w = Math.min(1.0F / color[0], Math.min(1.0F / color[1], 1.0F / color[2]));
            float v = (float) Instances.minecraft.getNightVisionModifier();
            color[0] = color[0] * (1.0F - v) + color[0] * w * v;
            color[1] = color[1] * (1.0F - v) + color[1] * w * v;
            color[2] = color[2] * (1.0F - v) + color[2] * w * v;
        }

        // Applies the darkness fog effect
        if (Instances.minecraft.hasDarkness()) {
            // Probably not the exact calculations minecraft makes, but results in the same effect
            float darkness = 1 - (Instances.minecraft.getDarknessFogEffect(0) / 15);
            color[0] = color[0] * darkness;
            color[1] = color[1] * darkness;
            color[2] = color[2] * darkness;
        }

        return color;
    }

    /**
     * Returns whether the cloud color should be affected by sky effects
     *
     * @return Should clouds be affected by sky effects
     */
    public static boolean shouldSkyColorIgnoreSkyEffects() {
        return CelestialSky.getDimensionRenderInfo().environment.skyColor.ignoreSkyEffects;
    }

    /**
     * Sets up the sky color. Applies modifications such as lightning flashes, the rain and thunder tint, and darkness depending on the time
     */
    public static void setupSkyColor() {
        skyRed = CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredRed();
        skyGreen = CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredGreen();
        skyBlue = CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredBlue();

        if (!shouldSkyColorIgnoreSkyEffects()) {
            float timeOfDayModifier = (float) Math.clamp((Math.cos(Instances.minecraft.getTimeOfDay() * 6.2831855F) * 2.0F + 0.5F), 0.0F, 1.0F);
            skyRed *= timeOfDayModifier;
            skyGreen *= timeOfDayModifier;
            skyBlue *= timeOfDayModifier;

            // Rain color modification
            float rainLevel = (float) Instances.minecraft.getRainLevel();
            if (rainLevel > 0.0F) {
                float skyColorModifier = ((skyRed * timeOfDayModifier * 0.3F) + (skyGreen * timeOfDayModifier * 0.59F) + (skyBlue * timeOfDayModifier * 0.11F));
                float rainLevelModifier = 1.0F - rainLevel * 0.75F;

                skyRed = skyRed * rainLevelModifier + (skyColorModifier * 0.6F) * (1F - rainLevelModifier);
                skyGreen = skyGreen * rainLevelModifier + (skyColorModifier * 0.6F) * (1F - rainLevelModifier);
                skyBlue = skyBlue * rainLevelModifier + (skyColorModifier * 0.6F) * (1F - rainLevelModifier);
            }

            // Thunder color modification
            float thunderLevel = Instances.minecraft.getThunderLevel();
            if (thunderLevel > 0.0F) {
                float skyColorModifier = (skyRed * 0.3F + skyGreen * 0.59F + skyBlue * 0.11F) * 0.2F;
                float thunderLevelModifier = 1.0F - thunderLevel * 0.75F;

                skyRed = skyRed * thunderLevelModifier + (skyColorModifier) * (1F - thunderLevelModifier);
                skyGreen = skyGreen * thunderLevelModifier + (skyColorModifier) * (1F - thunderLevelModifier);
                skyBlue = skyBlue * thunderLevelModifier + (skyColorModifier) * (1F - thunderLevelModifier);
            }

            // Lightning flash
            int skyFlashTime = (int) Instances.minecraft.getSkyFlashTime();
            if (skyFlashTime > 0) {
                float flashModifier = (Math.min(skyFlashTime - Instances.minecraft.getTickDelta(), 1)) * 0.45F;

                skyRed = skyRed * (1.0F - flashModifier) + 0.8F * flashModifier;
                skyGreen = skyGreen * (1.0F - flashModifier) + 0.8F * flashModifier;
                skyBlue = skyBlue * (1.0F - flashModifier) + 1.0F * flashModifier;
            }
        }
    }

    /**
     * Returns the Celestial sky color
     *
     * @return Sky color
     */
    public static float[] getSkyColor() {
        return new float[]{skyRed, skyGreen, skyBlue};
    }

    /**
     * Returns the configured cloud height
     *
     * @return Cloud height
     */
    public static float getCloudHeight() {
        return CelestialSky.getDimensionRenderInfo().environment.cloudHeight.invoke();
    }

    /**
     * Returns whether the cloud color should be affected by sky effects
     *
     * @return Should clouds be affected by sky effects
     */
    public static boolean shouldCloudColorIgnoreSkyEffects() {
        return CelestialSky.getDimensionRenderInfo().environment.cloudColor.ignoreSkyEffects;
    }

    /**
     * Returns the configured cloud color
     *
     * @return Cloud color RGB as a float array
     */
    public static float[] getCloudColor() {
        float cloudRed = CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredRed();
        float cloudGreen = CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredGreen();
        float cloudBlue = CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredBlue();

        // Skips cloud color modification if sky effects should be ignored
        if (!shouldCloudColorIgnoreSkyEffects()) {
            // Modifies cloud color depending on time and weather
            float timeOfDayModifier = FMath.clamp(FMath.cos(Instances.minecraft.getTimeOfDay() * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);

            // Rain color modification
            float rainLevel = (float) Instances.minecraft.getRainLevel();
            if (rainLevel > 0.0F) {
                float cloudColorModifier = (cloudRed * 0.3F + cloudGreen * 0.59F + cloudBlue * 0.11F) * 0.6F;
                float rainLevelModifier = 1.0F - rainLevel * 0.95F;
                cloudRed = cloudRed * rainLevelModifier + cloudColorModifier * (1.0F - rainLevelModifier);
                cloudGreen = cloudGreen * rainLevelModifier + cloudColorModifier * (1.0F - rainLevelModifier);
                cloudBlue = cloudBlue * rainLevelModifier + cloudColorModifier * (1.0F - rainLevelModifier);
            }

            // Time color modification
            cloudRed *= timeOfDayModifier * 0.9F + 0.1F;
            cloudGreen *= timeOfDayModifier * 0.9F + 0.1F;
            cloudBlue *= timeOfDayModifier * 0.85F + 0.15F;

            // Thunder color modification
            float thunderLevel = Instances.minecraft.getThunderLevel();
            if (thunderLevel > 0.0F) {
                float cloudColorModifier = (cloudRed * 0.3F + cloudGreen * 0.59F + cloudBlue * 0.11F) * 0.2F;
                float thunderLevelModifier = 1.0F - thunderLevel * 0.95F;
                cloudRed = cloudRed * thunderLevelModifier + cloudColorModifier * (1.0F - thunderLevelModifier);
                cloudGreen = cloudGreen * thunderLevelModifier + cloudColorModifier * (1.0F - thunderLevelModifier);
                cloudBlue = cloudBlue * thunderLevelModifier + cloudColorModifier * (1.0F - thunderLevelModifier);
            }
        }

        return new float[]{cloudRed, cloudGreen, cloudBlue};
    }
}
