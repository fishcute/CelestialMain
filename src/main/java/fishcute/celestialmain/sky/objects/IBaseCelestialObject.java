package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.CelestialExpression;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class IBaseCelestialObject extends ICelestialObject {
    public static final String DEFAULT_SCALE = "10";
    public static final String DEFAULT_POS_X = "0";
    public static final String DEFAULT_POS_Y = "0";
    public static final String DEFAULT_POS_Z = "0";
    public static final String DEFAULT_DISTANCE = "100";
    public static final String DEFAULT_DEGREES_X = "0";
    public static final String DEFAULT_DEGREES_Y = "0";
    public static final String DEFAULT_DEGREES_Z = "0";
    public static String DEFAULT_BASE_DEGREES_X = "0";
    public static String DEFAULT_BASE_DEGREES_Y = "0";
    public static String DEFAULT_BASE_DEGREES_Z = "0";
    public static String DEFAULT_POS_SIDE_X = "sideXPos";
    public static String DEFAULT_POS_SIDE_Y = "sideYPos";
    public static String DEFAULT_POS_SIDE_Z = "sideZPos";
    public static String DEFAULT_COMPLEXITY = "16";



    public IBaseCelestialObject() {
        scale = null;
        distance = null;
        baseDegreesX = null;
        baseDegreesY = null;
        baseDegreesZ = null;
        properties = null;
        vertexList = null;
    }

    public CelestialExpression degreesX;
    public CelestialExpression degreesY;
    public CelestialExpression degreesZ;
    public CelestialExpression baseDegreesX;
    public CelestialExpression baseDegreesY;
    public CelestialExpression baseDegreesZ;
    public CelestialExpression posX;
    public CelestialExpression posY;
    public CelestialExpression posZ;
    public CelestialExpression scale;
    public CelestialExpression distance;

    public final CelestialObjectProperties properties;
    public final ArrayList<Util.VertexPoint> vertexList;

    public IBaseCelestialObject(Object[] localVariables, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties celestialObjectProperties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, Module... multiDataModule) {
        this.localVariables = (HashMap<String, CelestialSky.Variable>) localVariables[0];
        this.localVariableModule = (Module) localVariables[1];

        for (CelestialSky.Variable v : this.localVariables.values()) {
            v.compile(this.localVariableModule);
        }

        this.posX = Util.compileExpressionObject(posX, dimension, name, "display.pos_x", multiDataModule);
        this.posY = Util.compileExpressionObject(posY, dimension, name, "display.pos_y", multiDataModule);
        this.posZ = Util.compileExpressionObject(posZ, dimension, name, "display.pos_z", multiDataModule);

        this.distance = Util.compileExpressionObject(distance, dimension, name, "display.distance", multiDataModule);

        this.degreesX = Util.compileExpressionObject(degreesX, dimension, name, "rotation.degrees_x", multiDataModule);
        this.degreesY = Util.compileExpressionObject(degreesY, dimension, name, "rotation.degrees_y", multiDataModule);
        this.degreesZ = Util.compileExpressionObject(degreesZ, dimension, name, "rotation.degrees_z", multiDataModule);
        this.baseDegreesX = Util.compileExpressionObject(baseDegreesX, dimension, name, "rotation.base_degrees_x", multiDataModule);
        this.baseDegreesY = Util.compileExpressionObject(baseDegreesY, dimension, name, "rotation.base_degrees_y", multiDataModule);
        this.baseDegreesZ = Util.compileExpressionObject(baseDegreesZ, dimension, name, "rotation.base_degrees_z", multiDataModule);

        this.scale = Util.compileExpressionObject(scale, dimension, name, "display.scale", multiDataModule);;
        this.properties = celestialObjectProperties;
        this.vertexList = vertexList;
    }



    @Override
    public ICelestialObject createFromJson(JsonObject o, String name, String dimension) {
        if (o == null) {
            Util.sendCompilationError("Failed to load celestial object \"" + name + ".json\", as it did not exist.", dimension + "/sky.json", null);
            return null;
        }
        IBaseCelestialObject i;
        if (o.has("populate")) {
            PopulateObjectData data = createPopulateData(o, dimension);
            i = (IBaseCelestialObject) this.createObjectFromJson(o, name, dimension, new PopulateObjectData.Module(data));
            i.registerPopulateObjects(data);
        } else {
            i = (IBaseCelestialObject) this.createObjectFromJson(o, name, dimension, null);
        }

        return i;
    }

    public abstract ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module);

    public PopulateObjectData populateData = null;

    public void registerPopulateObjects(PopulateObjectData populateData) {
        this.populateData = populateData;
    }

    public static PopulateObjectData createPopulateData(JsonObject p, String dimension) {
        PopulateObjectData data;
        JsonObject o = p.getAsJsonObject("populate");
        JsonObject rotation = o.getAsJsonObject("rotation");
        JsonObject display = o.getAsJsonObject("display");

        String location = Util.locationFormat(dimension, "populate");

        int count = Util.getOptionalInteger(o, "count", 1, location);

        data = new PopulateObjectData(
                count,
                o.has("objects") ? count + o.getAsJsonArray("objects").size() : count,
                Util.getOptionalDouble(rotation, "min_degrees_x", 0, location),
                Util.getOptionalDouble(rotation, "max_degrees_x", 360, location),
                Util.getOptionalDouble(rotation, "min_degrees_y", 0, location),
                Util.getOptionalDouble(rotation, "max_degrees_y", 360, location),
                Util.getOptionalDouble(rotation, "min_degrees_z", 0, location),
                Util.getOptionalDouble(rotation, "max_degrees_z", 360, location),
                Util.getOptionalDouble(display, "min_pos_x", 0, location),
                Util.getOptionalDouble(display, "max_pos_x", 0, location),
                Util.getOptionalDouble(display, "min_pos_y", 0, location),
                Util.getOptionalDouble(display, "max_pos_y", 0, location),
                Util.getOptionalDouble(display, "min_pos_z", 0, location),
                Util.getOptionalDouble(display, "max_pos_z", 0, location),
                Util.getOptionalDouble(display, "min_scale", 1, location),
                Util.getOptionalDouble(display, "max_scale", 1, location),
                Util.getOptionalDouble(display, "min_distance", 0, location),
                Util.getOptionalDouble(display, "max_distance", 0, location),
                Util.getOptionalBoolean(o, "per_object_calculations", false, location)
        );

        if (o.has("objects")) {
            location += ".objects";

            for (JsonElement e : o.getAsJsonArray("objects")) {

                JsonObject object = e.getAsJsonObject();
                data.createObject(count,
                        Util.getOptionalDouble(object, "degrees_x", 0, location),
                        Util.getOptionalDouble(object, "degrees_y", 0, location),
                        Util.getOptionalDouble(object, "degrees_z", 0, location),
                        Util.getOptionalDouble(object, "scale", 0, location),
                        Util.getOptionalDouble(object, "pos_x", 0, location),
                        Util.getOptionalDouble(object, "pos_y", 0, location),
                        Util.getOptionalDouble(object, "pos_z", 0, location),
                        Util.getOptionalDouble(o, "distance", 0, location));
                count++;
            }
        }
        return data;
    }

    @Override
    public void render(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2) {
        Instances.renderSystem.toggleBlend(this.properties.blend);

        //FogRenderer.levelFogColor();

        if (this.properties.isSolid)
            Instances.renderSystem.defaultBlendFunc();
        else
            Instances.renderSystem.blendFuncSeparate();

        Instances.renderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.begin(bufferBuilder);

        if (this.populateData != null) {
            this.populateData.index = 0;
        }

        matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, this.baseDegreesX.invoke());
        matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, this.baseDegreesY.invoke());
        matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, this.baseDegreesZ.invoke());

        if (this.populateData != null) {
            this.populateData.renderPopulateObjects(this, bufferBuilder, matrices, matrix4f2);
        }
        else {
            renderPre(bufferBuilder, matrices, matrix4f2,
                    this.degreesX.invoke(), this.degreesY.invoke(), this.degreesZ.invoke(),
                    this.posX.invoke(), this.posY.invoke(), this.posZ.invoke(),
                    this.scale.invoke(), this.distance.invoke());
        }

        this.end(bufferBuilder);
    }
    public abstract void begin(IBufferBuilderWrapper bufferBuilder);
    public abstract void end(IBufferBuilderWrapper bufferBuilder);
    public void renderPre(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float degreesX, float degreesY, float degreesZ, float posX, float posY, float posZ, float scale, float distance) {
        renderObject(bufferBuilder, matrices, matrices.celestial$rotateThenTranslate(
                degreesX,
                degreesY,
                degreesZ,
                posX,
                posY,
                posZ), scale, distance);

        // Undo translations and rotations
        // Should change this in the future
        matrices.celestial$translateThenRotate(-degreesX, -degreesY, -degreesZ, -posX, -posY, -posZ);
    }
    public abstract void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance);
}
