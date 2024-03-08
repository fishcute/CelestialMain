package fishcute.celestialmain.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IMatrix4fWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.SkyBoxObjectProperties;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;

public class SkyBoxObject extends IBaseCelestialObject {
    public SkyBoxObject() {}
    public IResourceLocationWrapper texture;
    public SkyBoxObjectProperties skyBoxObjectProperties;

    public SkyBoxObject(SkyBoxObjectProperties skyBoxObjectProperties, String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, MultiCelestialExpression.MultiDataModule multiDataModule) {
        super(scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
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
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, IMatrix4fWrapper matrix4f2, float scale, float distance) {
        // Set texture
        if (this.texture != null)
            Instances.renderSystem.setShaderTexture(0, this.texture);

        Instances.renderSystem.setShaderPositionTex();

        SkyBoxObjectProperties.SkyBoxSideTexture side;

        Instances.renderSystem.setShaderColor(this.properties.getRed(), this.properties.getGreen(), this.properties.getBlue(), this.properties.alpha.invoke());

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

        for (int l = 0; l < 6; ++l) {
            side = this.skyBoxObjectProperties.sides.get(l);

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

                this.rotate(matrices, l);
                IMatrix4fWrapper matrix4f3 = matrices.celestial$lastPose();

                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size, -size, textureX, textureY, 1.0F, 1.0F, 1.0F, 1.0F);
                bufferBuilder.celestial$vertexUv(matrix4f3, -size, -size, size, textureX, textureScaleY,1.0F, 1.0F, 1.0F, 1.0F);
                bufferBuilder.celestial$vertexUv(matrix4f3, size, -size, size, textureScaleX, textureScaleY, 1.0F, 1.0F, 1.0F, 1.0F);
                bufferBuilder.celestial$vertexUv(matrix4f3, size, -size, -size, textureScaleX, textureY, 1.0F, 1.0F, 1.0F, 1.0F);
                bufferBuilder.celestial$upload();

                // Should change this in the future
                this.undoRotate(matrices, l);
            }
        }
    }

    private void rotate(IPoseStackWrapper matrices, int l) {
        if (l == 0) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, 180);
        }
        if (l == 1) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, 90);
        }

        if (l == 2) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, -90);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, 180);
        }

        if (l == 3) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, 180);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, 180);
        }

        if (l == 4) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, 90);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, -90);
        }

        if (l == 5) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, -90);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, 90);
        }
    }

    private void undoRotate(IPoseStackWrapper matrices, int l) {
        if (l == 0) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, -180);
        }
        if (l == 1) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, -90);
        }

        if (l == 2) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, -180);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, 90);
        }

        if (l == 3) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, -180);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, -180);
        }

        if (l == 4) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, 90);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, -90);
        }

        if (l == 5) {
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Y, -90);
            matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, 90);
        }
    }

    @Override
    public ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        return new SkyBoxObject(
                SkyBoxObjectProperties.getSkyboxPropertiesFromJson(o, dimension, name),
                Util.getOptionalTexture(o, "texture", null, Util.locationFormat(dimension, "objects/" + name, "texture")),
                Util.getOptionalString(display, "scale", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_x", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_y", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_z", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "distance", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(rotation, "degrees_x", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_z", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_x", "-90", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_z", "-90", Util.locationFormat(dimension, name, "rotation")),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"), dimension, name, module),
                Util.getOptionalString(o, "parent", null, Util.locationFormat(dimension, name)),
                dimension,
                name,
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex")),
                module
        );
    }

    @Override
    public void begin(IBufferBuilderWrapper bufferBuilder) {}

    @Override
    public void end(IBufferBuilderWrapper bufferBuilder) {}
    @Override
    public void pushPose(IPoseStackWrapper matrices) {
        matrices.celestial$pushPose();
    }
    @Override
    public void popPose(IPoseStackWrapper matrices) {
        matrices.celestial$popPose();
    }
}

