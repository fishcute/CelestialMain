package fishcute.celestialmain.sky.objects;

import celestialexpressions.Module;
import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialObjectProperties;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.util.*;
import fishcute.celestialmain.version.independent.Instances;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class TriangleFanObject extends IBaseCelestialObject {
    public TriangleFanObject() {}
    public ColorEntry solidColor;
    public CelestialExpression sideX;
    public CelestialExpression sideY;
    public CelestialExpression sideZ;
    public CelestialExpression complexity;

    public TriangleFanData data = new TriangleFanData();

    public static class TriangleFanData implements MultiCelestialExpression.MultiDataModule.IndexSupplier {
        public int index = 0;
        public double sideX = 0;
        public double sideY = 0;
        public double sideZ = 0;

        @Override
        public Double getIndex() {
            return (double) this.index;
        }
        public Double getSideX() {
            return this.sideX;
        }
        public Double getSideY() {
            return this.sideY;
        }
        public Double getSideZ() {
            return this.sideZ;
        }
    }

    public TriangleFanObject(Object[] localVariables, String sideX, String sideY, String sideZ, String complexity, ColorEntry solidColor, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, TriangleFanData data, Module... multiDataModule) {
        super(localVariables, scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        this.solidColor = solidColor;
        this.sideX = Util.compileExpressionObject(sideX, dimension, name, "display.pos_side_x", multiDataModule);
        this.sideY = Util.compileExpressionObject(sideY, dimension, name, "display.pos_side_y", multiDataModule);
        this.sideZ = Util.compileExpressionObject(sideZ, dimension, name, "display.pos_side_z", multiDataModule);
        this.complexity = Util.compileExpressionObject(complexity, dimension, name, "display.complexity", multiDataModule);
        this.data = data;
    }

    @Override
    public fishcute.celestialmain.sky.objects.ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, fishcute.celestialmain.sky.objects.PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");

        Object[] localVariables = this.setupLocalVariables(o, name, dimension);
        Module localModule = (Module) localVariables[1];

        Module[] modules = new Module[module != null ? 3:2];
        TriangleFanData data = new TriangleFanData();
        modules[0] = new TriangleFanModule(data);
        modules[1] = localModule;
        if (module != null) modules[2] = module;
        return new TriangleFanObject(
                localVariables,
                Util.getOptionalString(display, "pos_side_x", DEFAULT_POS_SIDE_X, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_side_y", DEFAULT_POS_SIDE_Y, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_side_z", DEFAULT_POS_SIDE_Z, Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "complexity", DEFAULT_COMPLEXITY, Util.locationFormat(dimension, name, "display")),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color", null, false, modules),
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
                data,
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

        int complexity = this.complexity.invokeInt();

        float x = this.posX.invoke();
        float y = this.posY.invoke();
        float z = this.posZ.invoke();

        this.data.index = 0;
        this.data.sideX = x;
        this.data.sideY = y;
        this.data.sideZ = z;

        float red = this.solidColor.getStoredRed() * this.properties.getRed();
        float green = this.solidColor.getStoredGreen() * this.properties.getGreen();
        float blue = this.solidColor.getStoredBlue() * this.properties.getBlue();
        float alpha = this.properties.alpha.invoke();

        bufferBuilder.celestial$vertex(matrix4f2, this.sideX.invoke() * scale, this.sideY.invoke() * scale, this.sideZ.invoke() * scale, red, green, blue, alpha);

        for (int n = 0; n <= complexity; ++n) {
            this.data.index = n + 1;
            float o = (float) n * 6.2831855F / complexity;
            float p = FMath.sin(o);
            float q = FMath.cos(o);
            this.data.sideX = p;
            this.data.sideY = q;
            this.data.sideZ = 0;

            alpha = this.properties.alpha.invoke();

            //Instances.renderSystem.setShaderColor(red, green, blue, alpha);

            bufferBuilder.celestial$vertex(matrix4f2, this.sideX.invoke() * scale, this.sideY.invoke() * scale, this.sideZ.invoke() * scale, red, green, blue, alpha);
        }


    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.TRIANGLE_FAN;
    }

    public static class TriangleFanModule extends MultiCelestialExpression.MultiDataModule {

        public TriangleFanModule(@NotNull String name, @NotNull HashMap<String, Function0<Double>> variables, IndexSupplier indexSupplier) {
            super(name, variables, indexSupplier);
        }

        public TriangleFanModule(@NotNull TriangleFanData indexSupplier) {
            super(
                    "trianglefan",
                    PopulateObjectData.buildMap(
                            new PopulateObjectData.Entry("sideId", indexSupplier::getIndex),
                            new PopulateObjectData.Entry("sideXPos", indexSupplier::getSideX),
                            new PopulateObjectData.Entry("sideYPos", indexSupplier::getSideY),
                            new PopulateObjectData.Entry("sideZPos", indexSupplier::getSideZ)
                    ),
                    indexSupplier
            );
        }
    }
}
