package fishcute.celestialmain.sky.objects;

import celestialexpressions.FunctionList;
import celestialexpressions.Module;
import celestialexpressions.VariableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.ColorEntry;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;
import kotlin.jvm.functions.Function0;

import java.util.HashMap;

public abstract class ICelestialObject {
    public ICelestialObject() {

    }

    public HashMap<String, CelestialSky.Variable> localVariables = new HashMap<>();
    public Module localVariableModule;
    public boolean forceUpdateLocalVariables;
    public Object[] setupLocalVariables(JsonObject object, String objectName, String dimension) {
        HashMap<String, CelestialSky.Variable> variables = new HashMap<>();
        try {
            object.getAsJsonArray("local_variables").toString();
        }
        catch (Exception e) {
            return new Object[]{new HashMap<>(), new Module("localVariables", new VariableList(), new FunctionList())};
        }


        HashMap<String, Function0<Double>> variableList = new HashMap<>();

        int variableCount = 0;
        for (JsonElement o : object.getAsJsonArray("local_variables")) {
            try {
                String name = Util.getOptionalString(o.getAsJsonObject(), "name", "undefined", Util.locationFormat(dimension, objectName, "local_variables"));
                CelestialSky.Variable v = new CelestialSky.Variable(
                        Util.getOptionalString(o.getAsJsonObject(), "value", "0", Util.locationFormat(dimension, objectName, "local_variables", name + ".value")),
                        Util.getOptionalInteger(o.getAsJsonObject(), "update_frequency", 0, Util.locationFormat(dimension, objectName, "local_variables", name + ".update_frequency")),
                        Util.locationFormat(dimension, objectName, "local_variables", name)
                );

                variables.put(name, v);
                variableList.put(name, v::getValue);

                variableCount++;
            }
            catch (Exception e) {
                Util.sendError("Failed to load empty local variable entry. Skipping local variable initialization.", Util.locationFormat(dimension, objectName, "local_variables"), null);
                break;
            }
        }

        Util.log("Registered " + variableCount + " local variable(s).");

        return new Object[]{variables,
                new Module("variable", new VariableList(variableList),
                        // Probably not ever going to come
                        new FunctionList())};
    }
    public abstract CelestialObjectType getType();
    public abstract void render(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2);
    public abstract ICelestialObject createFromJson(JsonObject o, String name, String dimension);
    public void preTick() {
        CelestialSky.Variable v;
        for (String name : localVariables.keySet()) {
            v = localVariables.get(name);

            if ((v.updateTick <= 0 && !Instances.minecraft.isGamePaused()) || this.forceUpdateLocalVariables) {
                v.updateTick = v.updateFrequency;
                v.updateValue();
            }
            else
                v.updateTick--;
        }

        if (this.forceUpdateLocalVariables)
            this.forceUpdateLocalVariables = false;

        this.tick();
    }
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
            Util.sendCompilationError("Sky object \"" + name + "\" does not exist, or is formatted incorrectly.", dimension + "/sky", null);
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

    public void pushPose(IPoseStackWrapper matrices) {
        matrices.celestial$pushPose();
    }
    public void popPose(IPoseStackWrapper matrices) {
        matrices.celestial$popPose();
    }
}
