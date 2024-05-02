package fishcute.celestialmain.util;

import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;

public class ClientTick {

    public static boolean dimensionHasCustomSky = false;

    public static void onReload() {
        // Prevent initial resource load since celestial resources load on world join
        if (!firstLoad) {
            firstLoad = true;
            return;
        }

        Util.errorCount = 0;
        Util.errors = new ArrayList<>();
        CelestialSky.loadResources(true);
    }

    public static void onTick() {
        if (Instances.minecraft != null && Instances.minecraft.doesLevelExist())
            onWorldTick();
    }

    // For sending errors on first join
    private static boolean joined = false;
    private static boolean hasShownWarning = false;
    private static boolean firstLoad = false;
    public static void onFirstJoin() {
        CelestialSky.loadResources(false);
    }
    public static void onReloadKey() {
        onReload();
        if (!hasShownWarning) {
            hasShownWarning = true;
            Instances.minecraft.sendRedMessage("Note: This will not reload textures. Use F3-T to reload textures.");
        }
    }

    public static void onWorldTick() {
        if (!joined) {
            onFirstJoin();
            joined = true;
        }

        dimensionHasCustomSky = CelestialSky.dimensionSkyMap.containsKey(Instances.minecraft.getLevelPath());
        CelestialSky.tickValues();

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            CelestialSky.getDimensionRenderInfo().environment.skyColor.setInheritColor(Util.getSkyColor());
            CelestialSky.getDimensionRenderInfo().environment.fogColor.setInheritColor(Util.getFogColor());
        }
    }
    public static boolean devHasSkyOverride = true;
}
