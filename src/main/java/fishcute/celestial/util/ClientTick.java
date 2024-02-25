package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.version.dependent.VMinecraftInstance;

import java.util.ArrayList;

public class ClientTick {

    public static boolean dimensionHasCustomSky = false;

    public static void onReload() {
        Util.errorCount = 0;
        Util.errors = new ArrayList<>();
        CelestialSky.loadResources(true);
    }

    public static void onTick() {
        if (VMinecraftInstance.doesLevelExist())
            onWorldTick();
    }

    // For sending errors on first join
    private static boolean joined = false;
    private static boolean hasShownWarning = false;
    public static void onFirstJoin() {
        CelestialSky.loadResources(false);
    }
    public static void onReloadKey() {
        onReload();
        if (!hasShownWarning) {
            hasShownWarning = true;
            VMinecraftInstance.sendRedMessage("Note: This will not reload textures. Use F3-T to reload textures.");
        }
    }

    public static void onWorldTick() {
        if (!joined) {
            onFirstJoin();
            joined = true;
        }

        dimensionHasCustomSky = CelestialSky.dimensionSkyMap.containsKey(VMinecraftInstance.getLevelPath());
        CelestialSky.tickValues();

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            CelestialSky.getDimensionRenderInfo().environment.skyColor.setInheritColor(Util.getSkyColor());
            CelestialSky.getDimensionRenderInfo().environment.fogColor.setInheritColor(Util.getFogColor());
        }
    }
    public static boolean devHasSkyOverride = true;
}
