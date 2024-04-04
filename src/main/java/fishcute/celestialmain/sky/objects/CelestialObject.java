package fishcute.celestialmain.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import java.util.ArrayList;

public class CelestialObject extends IBaseCelestialObject {
    public CelestialObject() {}
    public IResourceLocationWrapper texture;

    public CelestialObject(String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, MultiCelestialExpression.MultiDataModule multiDataModule) {
        super(scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
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
            Util.VertexPointValue v;
            for (Util.VertexPoint vertexPoint : this.vertexList) {
                v = new Util.VertexPointValue(vertexPoint);
                bufferBuilder.celestial$vertexUv(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                        (float) v.uvX, (float) v.uvY, red, green, blue, alpha);
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
        return new CelestialObject(
                Util.getOptionalTexture(o, "texture", null, Util.locationFormat(dimension, "objects/" + name, "")),
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
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex"), module),
                module
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
}
