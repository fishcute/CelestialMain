package fishcute.celestialmain.sky.objects;

import celestialexpressions.Expression;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IPoseStackWrapper;
import fishcute.celestialmain.util.MultiCelestialExpression;
import fishcute.celestialmain.util.Util;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class PopulateObjectData implements MultiCelestialExpression.MultiDataModule.IndexSupplier {
    private class PopulateObject {
        public final double scale;
        public final double distance;
        public final double posX;
        public final double posY;
        public final double posZ;
        public final double degreesX;
        public final double degreesY;
        public final double degreesZ;
        public PopulateObject(double degreesX, double degreesY, double degreesZ, double posX, double posY, double posZ, double scale, double distance) {
            this.scale = scale;
            this.distance = distance;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.degreesX = degreesX;
            this.degreesY = degreesY;
            this.degreesZ = degreesZ;
        }
    }
    private final boolean perObjectCalculations;
    private final PopulateObject[] objects;

    public int index = 0;

    @Override
    public Double getIndex() {
        return (double) index;
    }
    public Double getScale() {
        return this.objects[index].scale;
    }
    public Double getDistance() {
        return this.objects[index].distance;
    }
    public Double getPosX() {
        return this.objects[index].posX;
    }
    public Double getPosY() {
        return this.objects[index].posY;
    }
    public Double getPosZ() {
        return this.objects[index].posZ;
    }
    public Double getDegreesX() {
        return this.objects[index].degreesX;
    }
    public Double getDegreesY() {
        return this.objects[index].degreesY;
    }
    public Double getDegreesZ() {
        return this.objects[index].degreesZ;
    }
    public PopulateObjectData(int count, int trueCount,
                              double minDegreesX, double maxDegreesX,
                              double minDegreesY, double maxDegreesY,
                              double minDegreesZ, double maxDegreesZ,
                              double minPosX, double maxPosX,
                              double minPosY, double maxPosY,
                              double minPosZ, double maxPosZ,
                              double minScale, double maxScale,
                              double minDistance, double maxDistance,
                              boolean perObjectCalculations) {
        objects = new PopulateObject[trueCount];

        for (int i = 0; i < count; i++) {
            createObject(i,
                    Util.generateRandomDouble(minDegreesX, maxDegreesX),
                    Util.generateRandomDouble(minDegreesY, maxDegreesY),
                    Util.generateRandomDouble(minDegreesZ, maxDegreesZ),
                    Util.generateRandomDouble(minPosX, maxPosX),
                    Util.generateRandomDouble(minPosY, maxPosY),
                    Util.generateRandomDouble(minPosZ, maxPosZ),
                    Util.generateRandomDouble(minScale, maxScale),
                    Util.generateRandomDouble(minDistance, maxDistance)
            );
        }

        this.perObjectCalculations = perObjectCalculations;
    }
    public void createObject(int id,
                             double degreesX, double degreesY, double degreesZ,
                             double posX, double posY, double posZ,
                             double scale, double distance) {
        objects[id] = new PopulateObject(degreesX, degreesY, degreesZ, posX, posY, posZ, scale, distance);
    }

    public void renderPopulateObjects(IBaseCelestialObject object, IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2) {
        this.index = 0;

        float degreesX = object.degreesX.invoke();
        float degreesY = object.degreesY.invoke();
        float degreesZ = object.degreesZ.invoke();
        float posX = object.posX.invoke();
        float posY = object.posY.invoke();
        float posZ = object.posZ.invoke();
        float scale = object.scale.invoke();
        float distance = object.distance.invoke();

        for (PopulateObject populateObject : this.objects) {
            if (this.perObjectCalculations) {
                degreesX = object.degreesX.invoke();
                degreesY = object.degreesY.invoke();
                degreesZ = object.degreesZ.invoke();
                posX = object.posX.invoke();
                posY = object.posY.invoke();
                posZ = object.posZ.invoke();
                scale = object.scale.invoke();
                distance = object.distance.invoke();

                if (object.properties.color != null) {
                    object.properties.color.updateColor();
                }
            }
            object.renderPre(bufferBuilder, matrices, matrix4f2,
                    (float) (degreesX + populateObject.degreesX),
                    (float) (degreesY + populateObject.degreesY),
                    (float) (degreesZ + populateObject.degreesZ),
                    (float) (posX + populateObject.posX),
                    (float) (posY + populateObject.posY),
                    (float) (posZ + populateObject.posZ),
                    (float) (scale + populateObject.scale),
                    (float) (distance + populateObject.distance)
            );
            this.index++;
        }
    }

    public static class Module extends MultiCelestialExpression.MultiDataModule {

        public Module(PopulateObjectData data) {
            super(
                    "populate",
                    buildMap(
                            new Entry("populateId", data::getIndex),
                            new Entry("populateScale", data::getScale),
                            new Entry("populateDistance", data::getDistance),
                            new Entry("populatePosX", data::getPosX),
                            new Entry("populatePosY", data::getPosY),
                            new Entry("populatePosZ", data::getPosZ),
                            new Entry("populateDegreesX", data::getDegreesX),
                            new Entry("populateDegreesY", data::getDegreesY),
                            new Entry("populateDegreesZ", data::getDegreesZ)

                    ),
                    data
            );
            //var variables = this.getVariables();
            //variables.getVariables().put("id", this::getIndex);
            //System.out.println("id: "+this.getVariables().getVariable("id"));
        }

        @Override
        public boolean hasVariable(String name) {
            return this.getVariables().hasVariable(name);
        }

        @NotNull
        @Override
        public Function0<Double> getVariable(@NotNull String name) {
            return this.getVariables().getVariable(name);
        }


    }

    public static HashMap<String, Function0<Double>> buildMap(Entry... entries) {
        HashMap<String, Function0<Double>> out = new HashMap<>();
        for (Entry entry: entries) {
            out.put(entry.key(), entry.value());
        }
        return out;
    }

    public static class Entry {
        public final String key;
        public final Expression value;

        public Entry(String key, Expression value) {
            this.key = key;
            this.value = value;
        }

        public String key() {
            return this.key;
        }

        public Expression value() {
            return this.value;
        }
    }
}
