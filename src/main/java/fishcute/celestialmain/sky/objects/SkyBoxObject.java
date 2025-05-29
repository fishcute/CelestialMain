package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.SkyBoxObjectProperties;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;
import java.util.HashMap;

public class SkyBoxObject extends IBaseCelestialObject {
    public SkyBoxObject() {}
    public IResourceLocationWrapper texture;
    public SkyBoxObjectProperties skyBoxObjectProperties;

    public SkyBoxObject(Object[] localVariables, SkyBoxObjectProperties skyBoxObjectProperties, String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, Module... multiDataModule) {
        super(localVariables, scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        if (texturePath != null) {
            this.texture = Instances.resourceLocationFactory.build(texturePath);
        }
        this.skyBoxObjectProperties = skyBoxObjectProperties;
    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.SKYBOX;
    }

    @Override
    public void tick() {
        if (this.properties.color != null) {
            this.properties.color.updateColor();
        }
    }

    @Override
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance) {
        Instances.renderSystem.setShaderPositionTex();

        SkyBoxObjectProperties.SkyBoxSideTexture side;

        if (this.properties.color != null) {
            this.properties.color.updateColor();
        }
        float red = this.properties.getRed();
        float green = this.properties.getGreen();
        float blue = this.properties.getBlue();
        float alpha = this.properties.alpha.invoke();

        float size;
        float textureX;
        float textureY;
        float textureScaleX;
        float textureScaleY;

        float uvX;
        float uvY;
        float uvSizeX;
        float uvSizeY;
        float textureSizeX = this.skyBoxObjectProperties.textureSizeX.invoke();
        float textureSizeY = this.skyBoxObjectProperties.textureSizeY.invoke();

        for (String sideId : SkyBoxObjectProperties.SIDES) {
            if (!this.skyBoxObjectProperties.sides.containsKey(sideId))
                continue;

            side = this.skyBoxObjectProperties.sides.get(sideId);

            if (side.texture != null) {
                Instances.renderSystem.setShaderTexture(0, side.texture);
            }
            else {
                Instances.renderSystem.setShaderTexture(0, this.texture);
            }

            size = this.skyBoxObjectProperties.skyBoxSize.invoke();

            uvX = side.uvX.invoke();
            uvY = side.uvY.invoke();
            uvSizeX = side.uvSizeX.invoke();
            uvSizeY = side.uvSizeY.invoke();

            textureX = (uvX / textureSizeX);
            textureY = (uvY / textureSizeY);
            textureScaleX = textureX + (uvSizeX / textureSizeX);
            textureScaleY = textureY + (uvSizeY / textureSizeY);

            if (textureX >= 0 && textureY >= 0 && textureScaleX >= 0 && textureScaleY >= 0) {
                bufferBuilder.celestial$beginObject();

                this.vertex(matrix4f2, bufferBuilder, sideId, size, textureX, textureY, textureScaleX, textureScaleY, red, green, blue, alpha);

                bufferBuilder.celestial$upload();
            }
        }
    }

    private void vertex(Object matrix4f3, IBufferBuilderWrapper bufferBuilder, String side, float size, float textureX, float textureY, float textureScaleX, float textureScaleY, float red, float green, float blue, float alpha) {
        switch (side) {
            case "west":
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size, -size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size,  size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size,  size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size, -size, textureScaleX, textureY, red, green, blue, alpha);
                break;
            case "south":
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size,  size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size,  size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size,  size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size,  size, textureScaleX, textureY, red, green, blue, alpha);
                break;
            case "east":
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size,  size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size, -size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size, -size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size,  size, textureScaleX, textureY, red, green, blue, alpha);
                break;
            case "north":
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size, -size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size, -size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size, -size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size, -size, textureScaleX, textureY, red, green, blue, alpha);
                break;
            case "up":
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size,  size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size,  size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size,  size, -size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size,  size, -size, textureScaleX, textureY, red, green, blue, alpha);
                break;
            // down
            default:
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size, -size, textureX, textureY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size, -size, textureX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size,  size, textureScaleX, textureScaleY, red, green, blue, alpha);
                bufferBuilder.celestial$vertexUv(matrix4f3,  size, -size,  size, textureScaleX, textureY, red, green, blue, alpha);
                break;
        }
    }

    @Override
    public ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");

        Object[] localVariables = this.setupLocalVariables(o, name, dimension);
        Module localModule = (Module) localVariables[1];

        return new SkyBoxObject(
                localVariables,
                SkyBoxObjectProperties.getSkyboxPropertiesFromJson(o, dimension, name),
                Util.getOptionalTexture(o, "texture", null, Util.locationFormat(dimension, "objects/" + name, "texture"), true),
                Util.getOptionalString(display, "scale", DEFAULT_SCALE, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_x", DEFAULT_POS_X, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_y", DEFAULT_POS_Y, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_z", DEFAULT_POS_Z, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "distance", DEFAULT_DISTANCE, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(rotation, "degrees_x", DEFAULT_DEGREES_X, Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_y", DEFAULT_DEGREES_Y, Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_z", DEFAULT_DEGREES_Z, Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_x", DEFAULT_BASE_DEGREES_X, Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_y", DEFAULT_BASE_DEGREES_Y, Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_z", DEFAULT_BASE_DEGREES_Z, Util.locationFormat(dimension, name, "rotation")),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"), dimension, name, module, localModule),
                Util.getOptionalString(o, "parent", null, Util.locationFormat(dimension, name)),
                dimension,
                name,
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex"), module, localModule),
                module,
                localModule
        );
    }

    @Override
    public void begin(IBufferBuilderWrapper bufferBuilder) {}

    @Override
    public void end(IBufferBuilderWrapper bufferBuilder) {}
    @Override
    public void pushPose(IPoseStackWrapper matrices) {
        matrices.celestial$pushPose();
        Instances.renderSystem.toggleTexture(true);
    }
    @Override
    public void popPose(IPoseStackWrapper matrices) {
        matrices.celestial$popPose();
        Instances.renderSystem.toggleTexture(false);
    }
}

