package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.objects.ICelestialObject;
import fishcute.celestialmain.sky.objects.TwilightObject;
import fishcute.celestialmain.util.FMath;
import fishcute.celestialmain.util.Util;

import java.awt.*;

public class VersionSky {
    /**
     * Used for FogRenderer#setupFog
     *
     * @param fogSky       If the fog is sky fog
     * @param viewDistance Render distance
     * @param thickFog     If the fog should be thick fog
     */
    public static void setupFogStartEnd(boolean fogSky, float viewDistance, boolean thickFog) {
        if (thickFog) {
            Util.fogStart = viewDistance * 0.05F;
            Util.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
        } else if (fogSky) {
            Util.fogStart = 0.0F;
            Util.fogEnd = viewDistance;
        } else {
            Util.fogStart = viewDistance - FMath.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            Util.fogEnd = viewDistance;
        }
    }

    /**
     * Used for ClientLevel#getCloudColor
     *
     * @param defaultCloudColor Default cloud color
     * @return Cloud color RGB as a double array
     */
    public static float[] getCloudColor(float[] defaultCloudColor) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            return FogSkyManager.getCloudColor();
        }
        return defaultCloudColor;
    }

    /**
     * Used for ClientLevel#getSkyColor
     *
     * @param defaultSkyColor Default sky color
     * @return Sky color RGB as a double array
     */
    public static float[] getClientLevelSkyColor(float[] defaultSkyColor) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            // Set up sky color
            FogSkyManager.setupSkyColor();

            return FogSkyManager.getSkyColor();
        }
        return defaultSkyColor;
    }

    /**
     * Used for DimensionSpecialEffects#getCloudHeight
     *
     * @param defaultCloudHeight Default cloud height
     * @return Cloud height
     */
    public static float getCloudHeight(float defaultCloudHeight) {
        if (Instances.minecraft.doesLevelExist() && CelestialSky.doesDimensionHaveCustomSky()) {
            return FogSkyManager.getCloudHeight();
        }
        return defaultCloudHeight;
    }

    /**
     * Used for FogRenderer#setupFog
     * <p>
     * Sets up the fog start and end
     */
    public static void setupFog() {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog() && canDisplayCustomFog()) {
            if (Instances.minecraft.hasDarkness()) {
                // Handle darkness effect
                float darkness = Instances.minecraft.getDarknessFogEffect(CelestialSky.getDimensionRenderInfo().environment.fogStart.invoke());
                Instances.renderSystem.setShaderFogStart(darkness * 0.75F);
                Instances.renderSystem.setShaderFogEnd(darkness);
            } else {
                // Update configured fog start/end
                Instances.renderSystem.setShaderFogStart(FogSkyManager.getFogStart());
                Instances.renderSystem.setShaderFogEnd(FogSkyManager.getFogEnd());
            }
        }
    }

    /**
     * Used for FogRenderer#setupColor
     * <p>
     * Sets up the fog color
     *
     * @return Fog color RGB as a float array
     */
    public static float[] setupFogColor() {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            // Set up fog color
            FogSkyManager.setupFogColor();

            // Get the modified fog color
            float[] color = FogSkyManager.getFogColorModified();
            Instances.renderSystem.clearColor(color[0], color[1], color[2], 0);

            return color;
        }
        return null;
    }

    /**
     * Used in FogRenderer#setupFog after the fog color is set up
     * <p>
     * Applies Celestial-only fog modifications such as the twilight object fog effect
     *
     * @param r Default fog color red
     * @param g Default fog color green
     * @param b Default fog color blue
     * @return Modified color RGB as a float array
     */
    public static float[] applyPostFogChanges(float r, float g, float b) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            for (ICelestialObject o : CelestialSky.getDimensionRenderInfo().skyObjects) {
                if (o instanceof TwilightObject) {
                    TwilightObject t = (TwilightObject) o;
                    r = (float) Util.lerp(t.fogTwilightColor.x, r, t.fogTwilightColor.w);
                    g = (float) Util.lerp(t.fogTwilightColor.y, g, t.fogTwilightColor.w);
                    b = (float) Util.lerp(t.fogTwilightColor.z, b, t.fogTwilightColor.w);
                }
            }
        }
        return new float[]{r, g, b};
    }

    /**
     * Returns whether custom Celestial fog should show
     *
     * @return Should custom fog show
     */
    public static boolean canDisplayCustomFog() {
        return !Instances.minecraft.isCameraInLava() && !Instances.minecraft.isCameraBlinded() && !Instances.minecraft.isCameraInPowderedSnow();
    }
}