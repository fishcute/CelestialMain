package fishcute.celestial.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialObjectProperties;
import fishcute.celestial.util.CelestialExpression;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.MultiCelestialExpression;
import fishcute.celestial.util.Util;
import fishcute.celestial.version.dependent.VMath;
import fishcute.celestial.version.dependent.VRenderSystem;
import fishcute.celestial.version.dependent.util.BufferBuilderWrapper;
import fishcute.celestial.version.dependent.util.Matrix4fWrapper;
import fishcute.celestial.version.dependent.util.PoseStackWrapper;
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

    public TriangleFanObject(String sideX, String sideY, String sideZ, String complexity, ColorEntry solidColor, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, TriangleFanData data, @NotNull MultiCelestialExpression.MultiDataModule... multiDataModule) {
        super(scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        this.solidColor = solidColor;
        this.sideX = Util.compileExpressionObject(sideX, dimension, name, "display.pos_side_x", multiDataModule);
        this.sideY = Util.compileExpressionObject(sideY, dimension, name, "display.pos_side_y", multiDataModule);
        this.sideZ = Util.compileExpressionObject(sideZ, dimension, name, "display.pos_side_z", multiDataModule);
        this.complexity = Util.compileExpressionObject(complexity, dimension, name, "display.complexity", multiDataModule);
        this.data = data;
    }

    @Override
    public ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        var modules = new ArrayList<MultiCelestialExpression.MultiDataModule>(2);
        var data = new TriangleFanData();
        modules.add(new TriangleFanModule(data));
        if (module != null) modules.add(module);
        return new TriangleFanObject(
                Util.getOptionalString(display, "pos_side_x", "sideXPos", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_side_y", "sideYPos", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_side_z", "sideZPos", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "complexity", "16", Util.locationFormat(dimension, name, "display")),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + name, "solid_color"), "solid_color", null, false, module),
                Util.getOptionalString(display, "scale", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_x", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_z", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_y", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "distance", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(rotation, "degrees_x", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_z", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_x", "-90", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_z", "-90", Util.locationFormat(dimension, name, "rotation")),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"), dimension, name, modules.toArray(MultiCelestialExpression.MultiDataModule[]::new)),
                Util.getOptionalString(o, "parent", null, Util.locationFormat(dimension, name)),
                dimension,
                name,
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex")),
                data,
                modules.toArray(MultiCelestialExpression.MultiDataModule[]::new)
        );
    }

    @Override
    public void begin(BufferBuilderWrapper bufferBuilder) {
        bufferBuilder.beginTriangleFan();
    }

    @Override
    public void end(BufferBuilderWrapper bufferBuilder) {
        bufferBuilder.upload();
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
    public void renderObject(BufferBuilderWrapper bufferBuilder, PoseStackWrapper matrices, Matrix4fWrapper matrix4f2, float scale, float distance) {
        VRenderSystem.toggleTexture(false);

        VRenderSystem.setShaderPositionColor();

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

        bufferBuilder.vertex(matrix4f2, this.sideX.invoke(), this.sideY.invoke(), this.sideZ.invoke(), red, green, blue, alpha);

        for (int n = 0; n <= complexity; ++n) {
            this.data.index = n + 1;
            float o = (float) n * 6.2831855F / complexity;
            float p = VMath.sin(o);
            float q = VMath.cos(o);
            this.data.sideX = p;
            this.data.sideY = q;
            this.data.sideZ = -q;

            alpha = this.properties.alpha.invoke();

            //VRenderSystem.setShaderColor(red, green, blue, alpha);

            bufferBuilder.vertex(matrix4f2, this.sideX.invoke(), this.sideY.invoke(), this.sideZ.invoke(), red, green, blue, alpha);
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
