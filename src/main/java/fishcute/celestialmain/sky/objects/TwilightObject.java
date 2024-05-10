package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.util.*;
import fishcute.celestialmain.version.independent.Instances;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class TwilightObject extends IBaseCelestialObject {
    public TwilightObject() {}
    public ColorEntry solidColor;
    public ColorEntry solidColor2;
    public ColorEntry fogEffectColor;
    public CelestialExpression fogEffectAlpha;
    public CelestialExpression twilightRotation;

    public TwilightObject(Object[] localVariables, ColorEntry solidColor, ColorEntry solidColor2, ColorEntry fogEffectColor, String fogEffectAlpha, String twilightRotation, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, Module... multiDataModule) {
        super(localVariables, scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        this.solidColor = solidColor;
        this.solidColor2 = solidColor2;
        this.fogEffectColor = fogEffectColor;
        this.fogEffectAlpha = Util.compileExpressionObject(fogEffectAlpha, dimension, name, "fog_effect.alpha", multiDataModule);
        this.twilightRotation = Util.compileExpressionObject(twilightRotation, dimension, name, "rotation.twilight_rotation", multiDataModule);
    }

    @Override
    public fishcute.celestialmain.sky.objects.ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, fishcute.celestialmain.sky.objects.PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        JsonObject fogEffect = o.getAsJsonObject("fog_effect");

        Object[] localVariables = this.setupLocalVariables(o, name, dimension);
        Module localModule = (Module) localVariables[1];

        Module[] modules = new Module[module != null ? 3:2];
        TriangleFanObject.TriangleFanData data = new TriangleFanObject.TriangleFanData();
        modules[0] = new TriangleFanObject.TriangleFanModule(data);
        modules[1] = localModule;
        if (module != null) modules[2] = module;
        return new TwilightObject(
                localVariables,
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color", null, false, modules),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color_transition", null, false, modules),
                ColorEntry.createColorEntry(fogEffect, Util.locationFormat(dimension, "objects/" + name, "fog_effect"), "color", null, false, modules),
                Util.getOptionalString(fogEffect, "alpha", /*TODO*/null, Util.locationFormat(dimension, name, "fog_effect")),
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
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"), dimension, name, modules),
                Util.getOptionalString(o, "parent", null, Util.locationFormat(dimension, name)),
                dimension,
                name,
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex"), modules),
                modules
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
    }
    @Override
    public void renderObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, float scale, float distance) {
        Instances.renderSystem.toggleTexture(false);

        Instances.renderSystem.setShaderPositionColor();

        float x = this.posX.invoke();
        float y = this.posY.invoke();
        float z = this.posZ.invoke();


        // Some funky stuff for the color progression stuff
        float k = this.twilightRotation.invoke() / 0.4F * 0.5F + 0.5F;

        float red = (float) (Util.lerp(this.solidColor.getStoredRed(), this.solidColor2.getStoredRed(), k)
                * this.properties.getRed());
        float green = (float) (Util.lerp(this.solidColor.getStoredGreen(), this.solidColor2.getStoredGreen(), k * k)
                * this.properties.getGreen());
        float blue = (float) (Util.lerp(this.solidColor.getStoredBlue(), this.solidColor2.getStoredBlue(), k * k)
                * this.properties.getBlue());
        float alpha = this.properties.alpha.invoke();

        bufferBuilder.celestial$vertex(matrix4f2, 0, 0, 0, red, green, blue, alpha);

        for (int n = 0; n <= 16; ++n) {
            float o = (float) n * 6.2831855F / 16;
            alpha = this.properties.alpha.invoke();
            bufferBuilder.celestial$vertex(matrix4f2, FMath.sin(o) * 120 * scale, FMath.cos(o) * 120 * scale, FMath.cos(o) * -40 * scale, red, green, blue, alpha);
        }
    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.TWILIGHT;
    }
}
