package fishcute.celestialmain.sky;

import fishcute.celestialmain.sky.objects.ICelestialObject;

import java.util.ArrayList;

public class CelestialRenderInfo {
    public final ArrayList<ICelestialObject> skyObjects;

    public final CelestialEnvironmentRenderInfo environment;
    public CelestialRenderInfo(ArrayList<ICelestialObject> skyObjects, CelestialEnvironmentRenderInfo environment) {
        this.skyObjects = skyObjects;
        this.environment = environment;

    }
}
