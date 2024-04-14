package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.FMath;
import fishcute.celestialmain.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

public class VersionSky {
    public static void setupFogStartEnd(boolean fogSky, float viewDistance, boolean thickFog) {
        if (thickFog) {
            Util.fogStart = viewDistance * 0.05F;
            Util.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
        }
        else if (fogSky) {
            Util.fogStart = 0.0F;
            Util.fogEnd = viewDistance;
        }
        else {
            Util.fogStart = viewDistance - FMath.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            Util.fogEnd = viewDistance;
        }
    }
    public static int getFogColor(int defaultBiomeColor, int biomeColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !Instances.minecraft.disableFogChanges()) {
            if (Util.getRealFogColor) {
                return biomeColor;
            }
            else {
                return Util.getDecimal(CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredColor());
            }
        }
        return defaultBiomeColor;
    }
    public static int getSkyColor(int defaultSkyColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !Instances.minecraft.disableFogChanges()) {
            if (Util.getRealSkyColor) {
                return defaultSkyColor;
            }
            else {
                return Util.getDecimal(CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredColor());
            }
        }
        return defaultSkyColor;
    }
    public static double[] getCloudColor(double[] defaultCloudColor, float f) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (CelestialSky.getDimensionRenderInfo().environment.cloudColor.ignoreSkyEffects) {
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255,  255, 255));

                return new double[]{CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredRed(), CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredGreen(), CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredBlue()};
            }
            else
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255, 255, 255));
        }
        return defaultCloudColor;
    }
    public static float getCloudColorRed(float previousRed) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredRed()) * previousRed;
        }
        return previousRed;
    }
    public static float getCloudColorGreen(float previousGreen) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredGreen()) * previousGreen;
        }
        return previousGreen;
    }
    public static float getCloudColorBlue(float previousBlue) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.getStoredBlue()) * previousBlue;
        }
        return previousBlue;
    }
    public static double[] getClientLevelSkyColor(double[] defaultSkyColor) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.skyColor.ignoreSkyEffects) {
            return new double[]{((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredRed()),
                    ((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredGreen()),
                    ((double) CelestialSky.getDimensionRenderInfo().environment.skyColor.getStoredBlue())};
        }
        return defaultSkyColor;
    }

    public static void getCloudHeight(CallbackInfoReturnable<Float> info) {
        if (Instances.minecraft.doesLevelExist() &&
                CelestialSky.doesDimensionHaveCustomSky())
            info.setReturnValue(CelestialSky.getDimensionRenderInfo().environment.cloudHeight.invoke());
    }

    public static void getSunriseColor(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            float[] rgba = new float[4];

            float g = (float) (Math.cos(skyAngle * 6.2831855F) - 0.0F);
            if (g >= -0.4F && g <= 0.4F) {
                float i = (g + 0.0F) / 0.4F * 0.5F + 0.5F;
                float j = (float) (1.0F - (1.0F - Math.sin(i * 3.1415927F)) * 0.99F);
                j *= j;

                CelestialSky.getDimensionRenderInfo().environment.twilightColor.setInheritColor(new Color(
                        i * 0.3F + 0.7F, i * i * 0.7F + 0.2F, i * i * 0.0F + 0.2F
                ));

                rgba[0] = i * 0.3F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getStoredRed());
                rgba[1] = i * i * 0.7F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getStoredGreen());
                rgba[2] = i * i * 0.0F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getStoredBlue());

                rgba[3] = Math.min(j, CelestialSky.getDimensionRenderInfo().environment.twilightAlpha.invoke());
                info.setReturnValue(rgba);
            } else {
                info.setReturnValue(null);
            }
        }
    }

    public static boolean checkThickFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog() && !Instances.minecraft.disableFogChanges())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }

    public static void setupFog() {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog() && !Instances.minecraft.disableFogChanges()) {
            if (Instances.minecraft.hasDarkness()) {
                float darkness = Instances.minecraft.getDarknessFogEffect(CelestialSky.getDimensionRenderInfo().environment.fogStart.invoke());
                Instances.renderSystem.setShaderFogStart(darkness * 0.75F);
                Instances.renderSystem.setShaderFogEnd(darkness);
            }
            else {
                Instances.renderSystem.setShaderFogStart(CelestialSky.getDimensionRenderInfo().environment.fogStart.invoke());
                Instances.renderSystem.setShaderFogEnd(CelestialSky.getDimensionRenderInfo().environment.fogEnd.invoke());
            }
        }
    }

    public static boolean canModifyFogColor() {
        return CelestialSky.getDimensionRenderInfo().environment.fogColor.ignoreSkyEffects && !Instances.minecraft.disableFogChanges();

    }
    public static float[] getFogColorApplyModifications() {
        float[] color = getFogColor();

        if (color != null && color[0] != 0.0F && color[1] != 0.0F && color[2] != 0.0F) {
            float w = Math.min(1.0F / color[0], Math.min(1.0F / color[1], 1.0F / color[2]));
            float v = (float) Instances.minecraft.getNightVisionModifier();
            color[0] = color[0] * (1.0F - v) + color[0] * w * v;
            color[1] = color[1] * (1.0F - v) + color[1] * w * v;
            color[2] = color[2] * (1.0F - v) + color[2] * w * v;
        }

        if (Instances.minecraft.hasDarkness()) {
            // Probably not the exact calculations minecraft makes, but results in the same effect
            float darkness = 1 - (Instances.minecraft.getDarknessFogEffect(0) / 15);
            color[0] = color[0] * darkness;
            color[1] = color[1] * darkness;
            color[2] = color[2] * darkness;
        }

        return color;
    }

    public static float[] getFogColor() {
        float[] colors = new float[3];
        if (canModifyFogColor()) {
            colors[0] = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredRed();
            colors[1] = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredGreen();
            colors[2] = CelestialSky.getDimensionRenderInfo().environment.fogColor.getStoredBlue();
        }
        else {
            return null;
        }
        return colors;
    }

    public static float[] setupFogColor(float tickDelta) {
        if (!CelestialSky.doesDimensionHaveCustomSky())
            return null;
        float[] color = CelestialSky.getDimensionRenderInfo().environment.fogColor.ignoreSkyEffects ? getFogColorApplyModifications() : getFogColor();
        if (color != null) {
            Instances.renderSystem.clearColor(color[0], color[1], color[2], 0);
        }
        return color;
    }
}