package fishcute.celestialmain.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.SkyBoxObjectProperties;
import fishcute.celestialmain.util.ColorEntry;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;

public class ColorSkyBoxObject extends IBaseCelestialObject {
    public ColorSkyBoxObject() {}
    public ColorEntry solidColor;
    public SkyBoxObjectProperties skyBoxObjectProperties;

    public ColorSkyBoxObject(SkyBoxObjectProperties skyBoxObjectProperties, ColorEntry solidColor, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, MultiCelestialExpression.MultiDataModule multiDataModule) {
        super(scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        this.solidColor = solidColor;
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
        if (this.solidColor != null) {
            this.solidColor.updateColor();
        }
    }

    @Override
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance) {
        Instances.renderSystem.setShaderPositionColor();

        Instances.renderSystem.setShaderColor(this.properties.getRed() * this.solidColor.getStoredRed(), this.properties.getGreen() * this.solidColor.getStoredGreen(), this.properties.getBlue() * this.solidColor.getStoredBlue(), this.properties.alpha.invoke());

        float size;

        for (int l = 0; l < 6; ++l) {
            size = this.skyBoxObjectProperties.skyBoxSize.invoke();

            bufferBuilder.celestial$beginColorObject();

            this.rotate(matrices, l);
            Object matrix4f3 = matrices.celestial$lastPose();

            bufferBuilder.celestial$vertex(matrix4f3, -size, -size, -size, 1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.celestial$vertex(matrix4f3, -size, -size, size,1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.celestial$vertex(matrix4f3, size, -size, size, 1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.celestial$vertex(matrix4f3, size, -size, -size, 1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.celestial$upload();

            // Should change this in the future
            this.undoRotate(matrices, l);
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
        return new ColorSkyBoxObject(
                SkyBoxObjectProperties.getSkyboxPropertiesFromJson(o, dimension, name),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color", null, false, module),
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

