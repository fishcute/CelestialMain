package fishcute.celestialmain.sky;

import com.google.gson.JsonObject;
import fishcute.celestialmain.util.CelestialExpression;
import fishcute.celestialmain.util.ColorEntry;
import fishcute.celestialmain.util.Util;

public class CelestialEnvironmentRenderInfo {
    public final boolean hasThickFog;
    public final ColorEntry fogColor;
    public final ColorEntry skyColor;
    public final CelestialExpression cloudHeight;
    public final ColorEntry cloudColor;
    public final CelestialExpression fogStart;
    public final CelestialExpression fogEnd;
    public final ColorEntry twilightColor;
    public final CelestialExpression twilightAlpha;
    public final CelestialExpression voidCullingLevel;
    public CelestialEnvironmentRenderInfo(boolean hasThickFog, ColorEntry fogColor, ColorEntry skyColor, String cloudHeight, ColorEntry cloudColor, String fogStart, String fogEnd, ColorEntry twilightColor, String twilightAlpha, String voidCullingLevel, String location) {
        this.hasThickFog = hasThickFog;
        this.fogColor = fogColor;
        this.skyColor = skyColor;
        this.cloudHeight = Util.compileExpression(cloudHeight, location + "environment.clouds.height");
        this.cloudColor = cloudColor;
        this.fogStart = Util.compileExpression(fogStart, location + "environment.fog.fog_start");
        this.fogEnd = Util.compileExpression(fogEnd, location + "environment.fog.fog_end");
        this.twilightColor = twilightColor;
        this.twilightAlpha = Util.compileExpression(twilightAlpha, location + "environment.twilight_alpha");
        this.voidCullingLevel = Util.compileExpression(voidCullingLevel, location + "environment.void_culling_level");
    }

    public final static ColorEntry DEFAULT_COLOR_SKY = new ColorEntry("#skyColor", null, null, -1, false, "1", "1", "1", "");

    public final static ColorEntry DEFAULT_COLOR_FOG = new ColorEntry("#fogColor", null, null, -1, false, "1", "1", "1", "");

    public final static ColorEntry DEFAULT_COLOR_CLOUD = new ColorEntry("#ffffff", null, null, -1, false, "1", "1", "1", "");

    public final static ColorEntry DEFAULT_COLOR_TWILIGHT = new ColorEntry("#b23333", null, null, -1, false, "1", "1", "1", "");

    public static final CelestialEnvironmentRenderInfo DEFAULT = new CelestialEnvironmentRenderInfo(
            false,
            DEFAULT_COLOR_FOG,
            DEFAULT_COLOR_SKY,
            "128",
            DEFAULT_COLOR_CLOUD,
            "fogStart",
            "fogEnd",
            DEFAULT_COLOR_TWILIGHT,
            "1",
            "0",
            ""
    );
    public static CelestialEnvironmentRenderInfo createEnvironmentRenderInfoFromJson(JsonObject o, String dimension) {
        if (o == null) {
            Util.sendCompilationError("Failed to read \"sky.json\" for dimension \"" + dimension + "\" while loading environment render info.", dimension + "/sky.json", null);
            return DEFAULT;
        }
        if (!o.has("environment")) {
            Util.log("Skipped loading environment.");
            return DEFAULT;
        }
        JsonObject environment = o.getAsJsonObject("environment");
        JsonObject fog = environment.getAsJsonObject("fog");
        JsonObject clouds = environment.getAsJsonObject("clouds");
        return new CelestialEnvironmentRenderInfo(
                Util.getOptionalBoolean(fog, "has_thick_fog", false, Util.locationFormat(dimension, "sky", "environment")),
                ColorEntry.createColorEntry(environment, Util.locationFormat(dimension, "sky", "environment"), "fog_color", DEFAULT_COLOR_FOG, true),
                ColorEntry.createColorEntry(environment, Util.locationFormat(dimension, "sky", "environment"),"sky_color", DEFAULT_COLOR_SKY, true),
                Util.getOptionalString(clouds, "height", "128", Util.locationFormat(dimension, "sky", "clouds")),
                ColorEntry.createColorEntry(clouds, Util.locationFormat(dimension, "sky", "environment.clouds"),"color", DEFAULT_COLOR_CLOUD, true),
                Util.getOptionalString(fog, "fog_start", "-1", Util.locationFormat(dimension, "sky", "fog")),
                Util.getOptionalString(fog, "fog_end", "-1", Util.locationFormat(dimension, "sky", "fog")),
                ColorEntry.createColorEntry(environment, Util.locationFormat(dimension, "sky", "environment"),"twilight_color", DEFAULT_COLOR_TWILIGHT, false),
                Util.getOptionalString(environment, "twilight_alpha", "1", Util.locationFormat(dimension, "sky", "environment")),
                Util.getOptionalString(environment, "void_culling_level", "0", Util.locationFormat(dimension, "sky", "environment")),
                Util.locationFormat(dimension, "sky", "")
        );
    }

    public boolean useSimpleFog() {
        return this.fogStart.equals("-1") || this.fogEnd.equals("-1");
    }

    public void updateColorEntries() {
        skyColor.tick();
        fogColor.tick();
        cloudColor.tick();
        twilightColor.tick();
    }
}
