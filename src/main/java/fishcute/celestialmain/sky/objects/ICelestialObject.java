package fishcute.celestialmain.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestial.version.dependent.util.BufferBuilderWrapper;
import fishcute.celestial.version.dependent.util.Matrix4fWrapper;
import fishcute.celestial.version.dependent.util.PoseStackWrapper;
import fishcute.celestialmain.util.Util;

public abstract class ICelestialObject {
    public ICelestialObject() {

    }
    public abstract CelestialObjectType getType();
    public abstract void render(BufferBuilderWrapper bufferBuilder, PoseStackWrapper matrices, Matrix4fWrapper matrix4f2);
    public abstract ICelestialObject createFromJson(JsonObject o, String name, String dimension);
    public abstract void tick();
    public enum CelestialObjectType {
        DEFAULT,
        COLOR,
        SKYBOX,
        COLOR_SKYBOX,
        TRIANGLE_FAN
    }

    public static ICelestialObject getObjectFromJson(JsonObject o, String name, String dimension) {
        if (o == null) {
            Util.sendCompilationError("Sky object \"" + name + "\" does not exist, or is formatted incorrectly.", dimension + "/sky");
            return null;
        }

        switch (findObjectType(o, name, dimension)) {
            case COLOR:
                return new ColorCelestialObject().createFromJson(o, name, dimension);
            case TRIANGLE_FAN:
                return new TriangleFanObject().createFromJson(o, name, dimension);
            case SKYBOX:
                return new SkyBoxObject().createFromJson(o, name, dimension);
            case COLOR_SKYBOX:
                return new ColorSkyBoxObject().createFromJson(o, name, dimension);
            default:
                return new CelestialObject().createFromJson(o, name, dimension);
        }
    }

    public static CelestialObjectType findObjectType(JsonObject o, String name, String dimension) {
        String objectType = Util.getOptionalString(o, "type", "default", Util.locationFormat(dimension, name));
        if (!objectType.equals("skybox") && !objectType.equals("triangle_fan")) {
            if (o.has("texture"))
                return CelestialObjectType.DEFAULT;
            else if (o.has("solid_color"))
                return CelestialObjectType.COLOR;
        }
        else if (objectType.equals("skybox")) {
            if (o.has("texture"))
                return CelestialObjectType.SKYBOX;
            else if (o.has("solid_color"))
                return CelestialObjectType.COLOR_SKYBOX;
        }
        return getCelestialObjectType(objectType);
    }
    public static CelestialObjectType getCelestialObjectType(String i) {
        switch (i) {
            case "color":
                return CelestialObjectType.COLOR;
            case "triangle_fan":
                return CelestialObjectType.TRIANGLE_FAN;
            case "skybox":
                return CelestialObjectType.SKYBOX;
            default:
                return CelestialObjectType.DEFAULT;
        }
    }

    public void pushPose(PoseStackWrapper matrices) {
        matrices.pushPose();
    }
    public void popPose(PoseStackWrapper matrices) {
        matrices.popPose();
    }
}
