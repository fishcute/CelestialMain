package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;
import java.util.HashMap;

public class CelestialObject extends IBaseCelestialObject {
    public CelestialObject() {}
    public IResourceLocationWrapper texture;

    public CelestialObject(Object[] localVariables, String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, Module... multiDataModule) {
        super(localVariables, scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        if (texturePath != null)
            this.texture = Instances.resourceLocationFactory.build(texturePath);
    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.DEFAULT;
    }

    @Override
    public void tick() {

    }

    @Override
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance) {
        int moonPhase = this.properties.moonPhase.invokeInt();

        Instances.renderSystem.setShaderPositionTex();

        // Set texture
        if (this.texture != null)
            Instances.renderSystem.setShaderTexture(0, this.texture);

        if (this.properties.color != null) {
            this.properties.color.updateColor();
        }
        float red = this.properties.getRed();
        float green = this.properties.getGreen();
        float blue = this.properties.getBlue();
        float alpha = this.properties.alpha.invoke();

        Instances.renderSystem.setShaderColor(1, 1, 1, 1);

        if (this.properties.hasMoonPhases) {
            int l = (moonPhase % 4);
            int i1 = (moonPhase / 4 % 2);
            float f13 = l / 4.0F;
            float f14 = i1 / 2.0F;
            float f15 = (l + 1) / 4.0F;
            float f16 = (i1 + 1) / 2.0F;
            bufferBuilder.celestial$vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                    f15, f16, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                    f13, f16, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                    f13, f14, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                    f15, f14, red, green, blue, alpha);
        } else if (this.vertexList != null && !this.vertexList.isEmpty()) {
            for (Util.VertexPoint v : this.vertexList) {
                if (v.color != null) {
                    v.color.tick();
                }
                bufferBuilder.celestial$vertexUv(matrix4f2, v.pointX.invoke(), v.pointY.invoke(), v.pointZ.invoke(),
                        v.uvX.invoke(), v.uvY.invoke(),
                        v.color == null ? red : red * v.color.getStoredRed(),
                        v.color == null ? green : green * v.color.getStoredGreen(),
                        v.color == null ? blue : blue * v.color.getStoredBlue(),
                        v.alpha.invoke() * alpha);
            }
        } else {
            bufferBuilder.celestial$vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                    0.0F, 0.0F, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                    1.0F, 0.0F, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                    1.0F, 1.0F, red, green, blue, alpha);
            bufferBuilder.celestial$vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                    0.0F, 1.0F, red, green, blue, alpha);
        }
    }

    @Override
    public ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");

        Object[] localVariables = this.setupLocalVariables(o, name, dimension);
        Module localModule = (Module) localVariables[1];

        return new CelestialObject(
                localVariables,
                Util.getOptionalTexture(o, "texture", null, Util.locationFormat(dimension, "objects/" + name, "")),
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
    public void begin(IBufferBuilderWrapper bufferBuilder) {
        bufferBuilder.celestial$beginObject();
    }

    @Override
    public void end(IBufferBuilderWrapper bufferBuilder) {
        bufferBuilder.celestial$upload();
    }

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
