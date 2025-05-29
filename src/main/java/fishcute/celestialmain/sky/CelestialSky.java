package fishcute.celestialmain.sky;

import celestialexpressions.Expression;
import celestialexpressions.FunctionList;
import celestialexpressions.Module;
import celestialexpressions.VariableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestialmain.sky.objects.IBaseCelestialObject;
import fishcute.celestialmain.sky.objects.ICelestialObject;
import fishcute.celestialmain.util.CelestialExpression;
import fishcute.celestialmain.util.ClientTick;
import fishcute.celestialmain.util.ColorEntry;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CelestialSky {
    static Gson reader = new Gson();

    @Nullable
    public static CelestialRenderInfo globalRenderInfo = null;

    public static HashMap<String, CelestialRenderInfo> dimensionSkyMap = new HashMap<>();
    public static boolean forceUpdateVariables = false;
    public static HashMap<String, ColorEntry> colorEntries = new HashMap<>();
    public static boolean forceUpdateColorEntry = false;

    public static int dimensionCount = 0;
    public static int objectCount = 0;

    public static boolean doesDimensionHaveCustomSky() {
        return (ClientTick.dimensionHasCustomSky && dimensionSkyMap.containsKey(ClientTick.dimensionPath)) || globalRenderInfo != null;
    }

    public static CelestialRenderInfo getDimensionRenderInfo() {
        // Check if dimension has a custom sky
        if (ClientTick.dimensionHasCustomSky && dimensionSkyMap.containsKey(ClientTick.dimensionPath)) {
            return dimensionSkyMap.get(ClientTick.dimensionPath);
        }
        // If not, check if a global configuration is available
        else if (globalRenderInfo != null) {
            return globalRenderInfo;
        }
        // Returns a default render info, but this hopefully should never be used
        return CelestialRenderInfo.DEFAULT;
    }
    public static void loadResources(boolean sendMessage) {
        dimensionSkyMap.clear();
        dimensionCount = 0;
        objectCount = 0;

        Util.log("Loading resources...");

        try { getFile("celestial:sky/dimensions.json").getAsJsonArray("dimensions"); }
        catch (Exception e) {
            Util.log("Found no dimension.json file. Skipping initialization");
            return;
        }

        variableModule = setupVariables();
        for (Variable v : variables.values()) {
            v.compile();
        }

        colorEntries = setupColorEntries();

        // Initialize global celestial render info
        String globalPath = Util.getOptionalString(getFile("celestial:sky/dimensions.json"), "global_dimension", "global", Util.locationFormat("sky/dimensions.json", "global_dimension"));

        ArrayList<String> globalObjects = getAllCelestialObjects(globalPath);

        if (!globalObjects.isEmpty()) {
            Util.log("Loading global sky from \"" + globalPath + "\"");
            globalRenderInfo = loadDimension(globalPath, getAllCelestialObjects(globalPath));
            dimensionCount++;
        }

        // Check for legacy rotations

        boolean legacy = Util.getOptionalBoolean(getFile("celestial:sky/dimensions.json"), "legacy_rotations", false, Util.locationFormat("sky/dimensions.json", "legacy_rotations"));
        if (legacy) {
            IBaseCelestialObject.DEFAULT_BASE_DEGREES_X = "-90";
            IBaseCelestialObject.DEFAULT_BASE_DEGREES_Z = "-90";
        }
        else {
            IBaseCelestialObject.DEFAULT_BASE_DEGREES_X = "0";
            IBaseCelestialObject.DEFAULT_BASE_DEGREES_Z = "0";
        }

        // Load remaining sky objects

        JsonArray dimensionList = getFile("celestial:sky/dimensions.json").getAsJsonArray("dimensions");

        if (dimensionList == null) {
            // Don't send the error if the global configuration was loaded
            if (globalObjects.isEmpty()) {
                Util.sendCompilationError("Could not find dimension list in \"dimensions.json\".", "sky", null);
            }
            return;
        }

        for (String dimension : getAsStringList(dimensionList)) {
            Util.log("Loading sky for dimension \"" + dimension + "\"");
            dimensionSkyMap.put(dimension, loadDimension(dimension, getAllCelestialObjects(dimension)));
            dimensionCount++;
        }

        Util.log("Finished loading skies for " + dimensionCount + " dimension(s). Loaded " + objectCount + " celestial object(s) with " + Util.errorCount + " error(s).");
        if (Instances.minecraft.doesPlayerExist() && sendMessage)
            Instances.minecraft.sendInfoMessage("Reloaded with " + Util.errorCount + " error(s).");
        Util.errorCount = 0;
    }

    public static CelestialRenderInfo loadDimension(String dimension, ArrayList<String> objectsToRegister) {
        ArrayList<ICelestialObject> celestialObjects = new ArrayList<>();
        for (String i : objectsToRegister) {
            Util.log("[" + dimension + "] Loading celestial object \"" + i + "\"");
            JsonObject object = getFile("celestial:sky/" + dimension + "/objects/" + i + ".json");
            ICelestialObject o = ICelestialObject.getObjectFromJson(object, i, dimension);
            if (o != null)
                celestialObjects.add(o);
            objectCount++;
        }
        return new CelestialRenderInfo(
                celestialObjects,
                CelestialEnvironmentRenderInfo.createEnvironmentRenderInfoFromJson(getFile("celestial:sky/" + dimension + "/sky.json"), dimension)
        );
    }

    public static ArrayList<String> getAsStringList(JsonArray array) {
        ArrayList<String> returnObject = new ArrayList<>();
        for (JsonElement a : array) {
            if (a != null && !a.isJsonNull())
                returnObject.add(a.getAsString());
        }
        return returnObject;
    }
    public static boolean initializingColorEntries = false;

    public static HashMap<String, ColorEntry> setupColorEntries() {
        initializingColorEntries = true;
        colorEntries.clear();
        try {
            getFile("celestial:sky/color_entries.json").getAsJsonArray("color_entries").toString();
        }
        catch (Exception e) {
            Util.log("Found no color_entries.json file. Skipping global color entry initialization.");
            return new HashMap<>();
        }

        // Set up color entries
        // This seems useless but trust me it isn't (at least I think)
        for (JsonElement o : getFile("celestial:sky/color_entries.json").getAsJsonArray("color_entries")) {
            String name = Util.getOptionalString(o.getAsJsonObject(), "name", null, "color_entries.json, -");
            if (name == null)
                continue;
            colorEntries.put("#" + name, null);
        }

        HashMap<String, ColorEntry> colorEntries = new HashMap<>();

        int colorEntryCount = 0;
        for (JsonElement o : getFile("celestial:sky/color_entries.json").getAsJsonArray("color_entries")) {
            String name = Util.getOptionalString(o.getAsJsonObject(), "name", null, "color_entries.json, -");
            if (name == null)
                continue;
            try {
                colorEntries.put("#" + name,
                        ColorEntry.createColorEntry(o.getAsJsonObject(), Util.locationFormat("variables", name), "color", ColorEntry.DEFAULT, false)
                );
                colorEntryCount++;
            }
            catch (Exception e) {
                Util.sendError("Failed to load empty color entry. Skipping global color entry initialization.", "color_entries.json", null);
                break;
            }
        }

        Util.log("Registered " + colorEntryCount + " global color entries(s).");

        forceUpdateColorEntry = true;
        initializingColorEntries = false;
        return colorEntries;
    }

    public static boolean isColorEntry(String color) {
        return colorEntries.containsKey(color);
    }
    public static Color getColorEntry(String color) {
        return colorEntries.get(color).getCallValue();
    }

    public static void tickValues() {
        Variable v;
        for (String name : variables.keySet()) {
            v = variables.get(name);

            if ((v.updateTick <= 0 && !Instances.minecraft.isGamePaused()) || forceUpdateVariables) {
                v.updateTick = v.updateFrequency;
                v.updateValue();
            }
            else
                v.updateTick--;
        }

        if (doesDimensionHaveCustomSky()) {
            getDimensionRenderInfo().environment.updateColorEntries();
            for (ICelestialObject object : getDimensionRenderInfo().skyObjects) {
                object.preTick();
            }
        }

        for (ColorEntry color : colorEntries.values()) {
            if (forceUpdateColorEntry) {
                color.updateColor();
                forceUpdateColorEntry = false;
            }
            color.tick();
        }

        if (forceUpdateVariables)
            forceUpdateVariables = false;
    }

    public static ArrayList<String> getAllCelestialObjects(String dimension) {
        JsonObject o = getFile("celestial:sky/" + dimension + "/sky.json");
        if (o == null) {
            Util.log("Found no sky.json for dimension \"" + dimension + "\", skipping dimension.");
            return new ArrayList<>();
        }
        JsonArray skyObjectList = o.getAsJsonArray("sky_objects");
        if (skyObjectList == null) {
            Util.log("Didn't load any celestial objects, as \"sky_objects\" was missing.");
            return new ArrayList<>();
        }
        return getAsStringList(skyObjectList);
    }

    public static JsonObject getFile(String path) {
        try {
            InputStream inputStream = Instances.minecraft.getResource(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            JsonElement jsonElement = reader.fromJson(bufferedReader, JsonElement.class);
            return jsonElement.getAsJsonObject();
        } catch (Exception exception) {
            return null;
        }
    }

    public static Module variableModule;
    public static HashMap<String, Variable> variables = new HashMap<>();

    public static Module setupVariables() {
        variables = new HashMap<>();
        try {
            getFile("celestial:sky/variables.json").getAsJsonArray("variables").toString();
        }
        catch (Exception e) {
            Util.log("Found no variables.json file. Skipping variable initialization.");
            return new Module("variables", new VariableList(), new FunctionList());
        }


        HashMap<String, Expression> variableList = new HashMap<>();

        int variableCount = 0;
        for (JsonElement o : getFile("celestial:sky/variables.json").getAsJsonArray("variables")) {
            try {
                String name = Util.getOptionalString(o.getAsJsonObject(), "name", "undefined", "variables.json, -");
                Variable v = new Variable(
                        Util.getOptionalString(o.getAsJsonObject(), "value", "0", Util.locationFormat("variables", name)),
                        Util.getOptionalInteger(o.getAsJsonObject(), "update_frequency", 0, Util.locationFormat("variables", name)),
                        Util.locationFormat( "variables", "Value of variable \"" + name + "\"")
                );

                variables.put(name, v);
                variableList.put(name, v::getValue);

                variableCount++;
            }
            catch (Exception e) {
                Util.sendError("Failed to load empty variable entry. Skipping variable initialization.", "variables.json", null);
                break;
            }
        }

        Util.log("Registered " + variableCount + " variable(s).");

        forceUpdateVariables = true;

        return new Module("variable", new VariableList(variableList),
                // Some day in the future
                new FunctionList());
    }

    public static class Variable {
        public int updateFrequency;
        public int updateTick;
        public CelestialExpression value;
        public double storedValue = 0;

        public String location;
        public String valueString;

        public Variable(String value, int updateFrequency, String location) {
            this.valueString = value;
            this.updateFrequency = updateFrequency;
            this.updateTick = updateFrequency;
            this.location = location;
        }
        public double getValue() {
            return this.storedValue;
        }
        public void compile() {
            this.value = Util.compileExpression(this.valueString, this.location);
        }
        public void compile(Module m) {
            this.value = Util.compileMultiExpression(this.valueString, this.location, m);
        }
        public void updateValue() {
            this.storedValue = this.value.invoke();
        }
    }
}
