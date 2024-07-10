package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.util.*;
import fishcute.celestialmain.version.independent.Instances;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TwilightObject extends IBaseCelestialObject {
    public TwilightObject() {}
    public ColorEntry solidColor;
    public ColorEntry solidColor2;
    public CelestialExpression twilightRotation;
    public double angle = 0;

    public TwilightObject(Object[] localVariables, ColorEntry solidColor, ColorEntry solidColor2, String twilightRotation, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, Module... multiDataModule) {
        super(localVariables, scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        this.solidColor = solidColor;
        this.solidColor2 = solidColor2;
        this.twilightRotation = Util.compileExpressionObject(twilightRotation, dimension, name, "rotation.twilight_rotation", multiDataModule);
    }

    @Override
    public fishcute.celestialmain.sky.objects.ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, fishcute.celestialmain.sky.objects.PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");

        Object[] localVariables = this.setupLocalVariables(o, name, dimension);

        return new TwilightObject(
                localVariables,
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color", ColorEntry.DEFAULT, false, module),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color_transition"), "solid_color_transition", ColorEntry.DEFAULT, false, module),
                Util.getOptionalString(rotation, "twilight_rotation", /*TODO*/null, Util.locationFormat(dimension, name, "twilight_rotation")),
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
        bufferBuilder.celestial$beginTriangleFan();
    }

    @Override
    public void end(IBufferBuilderWrapper bufferBuilder) {
        bufferBuilder.celestial$upload();
    }
    @Override
    public void tick() {
        if (this.properties.color != null) {
            this.properties.color.updateColor();
        }
        if (this.solidColor != null) {
            this.solidColor.updateColor();
        }
        if (this.solidColor2 != null) {
            this.solidColor2.updateColor();
        }
    }
    public Vector4d fogTwilightColor = new Vector4d();
    @Override
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance) {
        Instances.renderSystem.toggleTexture(false);

        Instances.renderSystem.setShaderPositionColor();

        float x = this.posX.invoke();
        float y = this.posY.invoke();
        float z = this.posZ.invoke();

        this.angle = this.twilightRotation.invoke();

        // Some funky stuff for the color progression stuff
        float k = (float) Util.getTwilightProgress(this.angle);
        float twilightAlpha = (float) Util.getTwilightAlpha(this.angle);

        float red = (float) (Util.lerp(this.solidColor.getStoredRed(), this.solidColor2.getStoredRed(), k)
                * this.properties.getRed());
        float green = (float) (Util.lerp(this.solidColor.getStoredGreen(), this.solidColor2.getStoredGreen(), k * k)
                * this.properties.getGreen());
        float blue = (float) (Util.lerp(this.solidColor.getStoredBlue(), this.solidColor2.getStoredBlue(), k * k)
                * this.properties.getBlue());
        float alpha = this.properties.alpha.invoke() * twilightAlpha;

        //System.out.println(alpha);

        this.fogTwilightColor.x = red;
        this.fogTwilightColor.y = green;
        this.fogTwilightColor.z = blue;
        this.fogTwilightColor.w = alpha * Util.getTwilightFogEffect((360 - this.angle) - 180, -this.baseDegreesY.invoke()) * scale;

        //System.out.println(k);

        bufferBuilder.celestial$vertex(matrix4f2, 0, 100, 0, red, green, blue, alpha);

        for (int n = 0; n <= 16; ++n) {
            float o = (float) n * 6.2831855F / 16;
            bufferBuilder.celestial$vertex(matrix4f2,
                    FMath.sin(o) * 120 * scale,
                    FMath.cos(o) * 120 * scale,
                    FMath.cos(o) * -40 * scale * twilightAlpha,
                    red, green, blue, 0.0F);
        }
    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.TWILIGHT;
    }
}
