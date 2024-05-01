package fishcute.celestialmain.util;

import celestialexpressions.Module;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.objects.PopulateObjectData;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ColorEntry {
    public final static ColorEntry DEFAULT = new ColorEntry("#ffffff", null, null, 0, false, "1", "1", "1", "");

    public String location;
    public CelestialExpression red = new CelestialExpression("255", null);
    public CelestialExpression green = new CelestialExpression("255", null);
    public CelestialExpression blue = new CelestialExpression("255", null);
    public boolean isBasicColor = false;
    public ArrayList<ColorListEntry> colors;
    public ColorListEntry baseColor = new ColorListEntry("#ffffff", "");
    public String cloneColor = null;
    public boolean ignoreSkyEffects;
    private PendingColor storedColor = new PendingColor("#ffffff", "");
    public int updateFrequency;
    public int updateTick;

    public static BlendType getBlendType(String type, String location) {
        switch (type) {
            case "linear_interpolation":
                return BlendType.LINEAR_INTERPOLATION;
            case "override":
                return BlendType.OVERRIDE;
            case "lab":
                return BlendType.LAB;
        }
        Util.sendCompilationError("Unknown color entry blend type \"" + type + "\".", location, null);
        return BlendType.LINEAR_INTERPOLATION;
    }

    public enum BlendType {
        LINEAR_INTERPOLATION,
        OVERRIDE,
        LAB
    }

    public static class PendingColor {
        public Color color;
        public String colorName;
        public String location;
        public boolean finalized = false;
        public PendingColor(String colorName, String location) {
            this.colorName = colorName;
            this.location = location;
        }
        public void tick() {
            this.color = Util.decodeColor(this.colorName, this.location);

            if (this.color != null) {
                this.finalized = true;
            }
        }
        public Color getColor() {
            return this.color;
        }
        public int getRed() {
            return this.color == null ? 255 : this.color.getRed();
        }
        public int getGreen() {
            return this.color == null ? 255 : this.color.getGreen();
        }
        public int getBlue() {
            return this.color == null ? 255 : this.color.getBlue();
        }
        public boolean isFinalized() {
            return this.finalized;
        }
    }
    public boolean isFinalized() {
        if (this.colors == null || this.colors.isEmpty()) {
            return this.baseColor.color.isFinalized();
        }
        boolean result = this.baseColor.color.isFinalized();
        for (ColorListEntry c : this.colors) {
            result = result && c.color.isFinalized();
        }
        return result;
    }
    public Color getCallValue() {
        return this.isFinalized() ? this.getStoredColor() : null;
    }

    public ColorEntryData data;

    public static class ColorEntryData implements MultiCelestialExpression.MultiDataModule.IndexSupplier {
        public int index = 0;
        public float currentRed = 0;

        public Double getCurrentRed() {
            return (double) this.currentRed;
        }

        public float currentGreen = 0;

        public Double getCurrentGreen() {
            return (double) this.currentGreen;
        }

        public float currentBlue = 0;

        public Double getCurrentBlue() {
            return (double) this.currentBlue;
        }

        @Override
        public Double getIndex() {
            return (double) this.index;
        }
    }

    public static class ColorListEntry {
        public PendingColor color;
        public CelestialExpression ratio;
        public CelestialExpression red;
        public CelestialExpression green;
        public CelestialExpression blue;
        public CelestialExpression redRatio;
        public CelestialExpression greenRatio;
        public CelestialExpression blueRatio;
        public BlendType type;

        public ColorListEntry(BlendType type, String colorName, String location, CelestialExpression ratio, CelestialExpression red, CelestialExpression green, CelestialExpression blue, CelestialExpression redRatio, CelestialExpression greenRatio, CelestialExpression blueRatio)  {
            this.color = new PendingColor(colorName, location);
            this.ratio = ratio;
            this.redRatio = redRatio;
            this.greenRatio = greenRatio;
            this.blueRatio = blueRatio;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.type = type;
        }

        public ColorListEntry(String colorName, String location) {
            this.color = new PendingColor(colorName, location);
            this.ratio = new CelestialExpression(1.0);
            this.redRatio = new CelestialExpression(1.0);
            this.greenRatio = new CelestialExpression(1.0);
            this.blueRatio = new CelestialExpression(1.0);
            this.red = new CelestialExpression(1.0);
            this.green = new CelestialExpression(1.0);
            this.blue = new CelestialExpression(1.0);
        }

        public void tick() {
            this.color.tick();
        }

        public int getRed() {
            return (int) ((this.red.invoke()) * (this.color.getRed()));
        }
        public int getGreen() {
            return (int) ((this.green.invoke()) * (this.color.getGreen()));
        }
        public int getBlue() {
            return (int) ((this.blue.invoke()) * (this.color.getBlue()));
        }
    }

    public float getStoredRed() {
        return this.storedColor.getRed() / 255.0F;
    }
    public float getStoredGreen() {
        return this.storedColor.getGreen() / 255.0F;
    }
    public float getStoredBlue() {
        return this.storedColor.getBlue() / 255.0F;
    }
    public Color getStoredColor() {
        return this.storedColor.color == null ? new Color(255, 255, 255) : this.storedColor.color;
    }

    public static ColorEntry createColorEntry(JsonObject o, String location, String elementName, ColorEntry defaultEntry, boolean optionalSkyEffects, Module... modules) {
        location += "." + elementName;

        if (o == null)
            return defaultEntry;
        try {
            o.get(elementName).getAsJsonObject();
        }
        catch (Exception e) {
            if (o.has(elementName)) {
                String color = Util.getOptionalString(o, elementName, "#ffffff", location);
                return new ColorEntry(color, null, elementName, 0, false, "1", "1", "1", location, modules);
            }
            return defaultEntry;
        }
        JsonObject colorObject = o.get(elementName).getAsJsonObject();

        return new ColorEntry(
                Util.getOptionalString(colorObject, "base_color", "#ffffff", location),
                colorObject.getAsJsonArray("colors"),
                elementName,
                Util.getOptionalInteger(colorObject, "update_frequency", 0, location),
                optionalSkyEffects && Util.getOptionalBoolean(colorObject, "ignore_sky_effects", false, location),
                Util.getOptionalString(colorObject, "red", "1", location),
                Util.getOptionalString(colorObject, "green", "1", location),
                Util.getOptionalString(colorObject, "blue", "1", location),
                location,
                modules);
    }
    public ColorEntry(String baseColor, JsonArray colorsJson, String elementName, int updateFrequency, boolean ignoreSkyEffects, String r, String g, String b, String location, Module... module) {
        Module[] modules = new Module[module.length + 1];

        System.arraycopy(module, 0, modules, 0, module.length);

        ColorEntryData data = new ColorEntryData();
        this.data = data;

        modules[modules.length - 1] = new ColorEntryModule(data);

        ArrayList<ColorListEntry> colors = new ArrayList<>();

        if (colorsJson != null) {
            try {
                for (JsonElement color : colorsJson) {
                    String hexColor = Util.getOptionalString(color.getAsJsonObject(), "color", "#ffffff", location);
                    colors.add(new ColorListEntry(
                            getBlendType(Util.getOptionalString(color.getAsJsonObject(), "blend_type", "linear_interpolation", location), location + ".blend_type"),
                            hexColor, location + ".color",
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "ratio", "1", location), location + ".ratio", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "red", "1", location), location + ".red", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "green", "1", location), location + ".green", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "blue", "1", location), location + ".blue", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "red_ratio", "1", location), location + ".red_ratio", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "green_ratio", "1", location), location + ".green_ratio", modules),
                            Util.compileMultiExpression(Util.getOptionalString(color.getAsJsonObject(), "blue_ratio", "1", location), location + ".blue_ratio", modules)
                    ));
                }
            } catch (Exception e) {
                Util.sendError("Failed to parse color entry \"" + elementName + "\".", location + "." + elementName, null);
            }
        }

        this.colors = colors;
        this.baseColor = new ColorListEntry(baseColor, location);
        this.updateFrequency = updateFrequency;
        this.ignoreSkyEffects = ignoreSkyEffects;
        this.red = Util.compileMultiExpression(r, location + ".red", module);
        this.green = Util.compileMultiExpression(g, location + ".green", module);
        this.blue = Util.compileMultiExpression(b, location + ".blue", module);
        this.location = location;
    }

    public void tick() {
        if (this.updateTick <= 0 || CelestialSky.forceUpdateVariables) {
            this.updateTick = this.updateFrequency;
            this.updateColor();
        }
        else
            this.updateTick--;
    }

    public void setInheritColor(Color c) {
        //if (this.inheritColor)
            //this.baseColor.color.color = c;
    }

    public void updateColor() {
        this.storedColor.color = getResultColor();
    }

    public Color getResultColor() {
        this.baseColor.tick();

        this.data.index = 0;

        int red = (int) Util.clamp(baseColor.getRed() * this.red.invoke(), 0, 255);
        int green = (int) Util.clamp(baseColor.getGreen() * this.green.invoke(), 0, 255);
        int blue = (int) Util.clamp(baseColor.getBlue() * this.blue.invoke(), 0, 255);

        this.data.currentRed = red / 255F;
        this.data.currentGreen = green / 255F;
        this.data.currentBlue = blue / 255F;

        if (colors == null || colors.isEmpty())
            return new Color(red, green, blue);

        int resultRed = baseColor.getRed();
        int resultGreen = baseColor.getGreen();
        int resultBlue = baseColor.getBlue();

        for (ColorListEntry color : this.colors) {
            this.data.index++;

            color.tick();

            float ratio = color.ratio.invoke();
            if (ratio > 1)
                ratio = 1;
            else if (ratio <= 0)
                continue;

            if (resultRed <= 0)
                resultRed = 1;
            if (resultGreen <= 0)
                resultGreen = 1;
            if (resultBlue <= 0)
                resultBlue = 1;

            this.data.currentRed = resultRed / 255F;
            this.data.currentGreen = resultGreen / 255F;
            this.data.currentBlue = resultBlue / 255F;

            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();

            float redRatio = (color.redRatio.invoke() * ratio);
            float greenRatio = (color.greenRatio.invoke() * ratio);
            float blueRatio = (color.blueRatio.invoke() * ratio);

            if (color.type == BlendType.LINEAR_INTERPOLATION) {
                resultRed = (int) (Util.lerp(red, resultRed, redRatio));
                resultGreen = (int) (Util.lerp(green, resultGreen, greenRatio));
                resultBlue = (int) (Util.lerp(blue, resultBlue, blueRatio));
            }
            else if (color.type == BlendType.OVERRIDE) {
                resultRed = red;
                resultGreen = green;
                resultBlue = blue;
            }
            else if (color.type == BlendType.LAB) {
                float[] lab = getLabFromRGB(red, green, blue);
                float[] labResult = getLabFromRGB(resultRed, resultGreen, resultBlue);

                resultRed = (int) (Util.lerp(lab[0], labResult[0], redRatio) * 255);
                resultGreen = (int) (Util.lerp(lab[1], labResult[1], greenRatio) * 255);
                resultBlue = (int) (Util.lerp(lab[2], labResult[2], blueRatio) * 255);

                float[] rgb = getRGBFromLab(resultRed / 255.0F, resultGreen / 255.0F, resultBlue / 255.0F);
                resultRed = (int) (rgb[0]);
                resultGreen = (int) (rgb[1]);
                resultBlue = (int) (rgb[2]);
            }
        }

        return new Color((int) Util.clamp(resultRed * this.red.invoke(), 0, 255), (int) Util.clamp(resultGreen * this.green.invoke(), 0, 255), (int) Util.clamp(resultBlue * this.blue.invoke(), 0, 255));
    }

    public static float[] getLabFromRGB(float red, float green, float blue) {
        float l = 0.4122214708f * red + 0.5363325363f * green + 0.0514459929f * blue;
        float m = 0.2119034982f * red + 0.6806995451f * green + 0.1073969566f * blue;
        float s = 0.0883024619f * red + 0.2817188376f * green + 0.6299787005f * blue;

        float l_ = (float) Math.cbrt(l);
        float m_ = (float) Math.cbrt(m);
        float s_ = (float) Math.cbrt(s);

        return new float[]{
                0.2104542553f*l_ + 0.7936177850f*m_ - 0.0040720468f*s_,
                1.9779984951f*l_ - 2.4285922050f*m_ + 0.4505937099f*s_,
                0.0259040371f*l_ + 0.7827717662f*m_ - 0.8086757660f*s_,
        };
    }
    public static float[] getRGBFromLab(float light, float a, float b)
    {
        float l_ = light + 0.3963377774f * a + 0.2158037573f * b;
        float m_ = light - 0.1055613458f * a - 0.0638541728f * b;
        float s_ = light - 0.0894841775f * a - 1.2914855480f * b;

        float l = l_*l_*l_;
        float m = m_*m_*m_;
        float s = s_*s_*s_;

        return new float[]{
                +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
                -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
                -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s,
        };
    }

    public static class ColorEntryModule extends MultiCelestialExpression.MultiDataModule {

        public ColorEntryModule(@NotNull String name, @NotNull HashMap<String, Function0<Double>> variables, IndexSupplier indexSupplier) {
            super(name, variables, indexSupplier);
        }

        public ColorEntryModule(@NotNull ColorEntryData indexSupplier) {
            super(
                    "colorentry",
                    PopulateObjectData.buildMap(
                            new PopulateObjectData.Entry("red", indexSupplier::getCurrentRed),
                            new PopulateObjectData.Entry("green", indexSupplier::getCurrentGreen),
                            new PopulateObjectData.Entry("blue", indexSupplier::getCurrentBlue)
                    ),
                    indexSupplier
            );
        }
    }
}
