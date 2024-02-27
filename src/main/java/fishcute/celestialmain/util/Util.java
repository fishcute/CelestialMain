package fishcute.celestialmain.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.version.dependent.VMath;
import fishcute.celestial.version.dependent.VMinecraftInstance;
import fishcute.celestial.version.dependent.Vector;
import fishcute.celestialmain.sky.CelestialSky;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Util {
    public static float fogStart = 0;
    public static float fogEnd = 0;
    static Random random = new Random();
    public static String locationFormat(String file, String object) {
        return file + ".json, " + object;
    }
    public static String locationFormat(String dimension, String file, String object) {
        return dimension + "/" + file + ".json, " + object;
    }
    public static String locationFormat(String dimension, String file, String object, String object2) {
        return dimension + "/" + file + ".json, " + object + "." + object2;
    }

    public static CelestialExpression compileExpression(String input, String location) {
        return new CelestialExpression(input, location);
    }
    public static CelestialExpression compileExpression(String input, String location, MultiCelestialExpression.MultiDataModule multiDataModule) {
        if (multiDataModule == null) {
            return new CelestialExpression(input, location);
        }
        return compileMultiExpression(input, location, multiDataModule);
    }

    public static MultiCelestialExpression compileMultiExpression(String input, String location, MultiCelestialExpression.MultiDataModule... multiDataModule) {
        return new MultiCelestialExpression(input, location, multiDataModule);
    }

    public static CelestialExpression compileExpressionObject(String equation, String dimension, String objectName, String location, MultiCelestialExpression.MultiDataModule... multiDataModule) {
        if (multiDataModule == null) {
            return compileExpression(equation, Util.locationFormat(dimension, "objects/" + objectName, location));
        }
        return compileMultiExpression(equation, Util.locationFormat(dimension, "objects/" + objectName, location), multiDataModule);
    }

    static double print(double i) {
        VMinecraftInstance.sendMessage("Value: " + i, true);
        return i;
    }

    public static void log(Object i) {
        if (!VMinecraftInstance.isGamePaused())
            System.out.println("[Celestial] " + i.toString());
    }
    public static int errorCount;
    static ArrayList<String> errors = new ArrayList<>();

    public static void sendCompilationError(String i, String location) {
        errorCount++;
        if (!VMinecraftInstance.doesPlayerExist())
            return;
        VMinecraftInstance.sendFormattedErrorMessage(i, "Compilation Error", location);
    }

    public static void sendError(String i, String location) {
        if (!VMinecraftInstance.doesPlayerExist() || errorCount > 25 || errors.contains(i))
            return;
        errorCount++;
        errors.add(i);
        VMinecraftInstance.sendFormattedErrorMessage(i, "Error", location);

        if (errorCount >= 25)
            VMinecraftInstance.sendErrorMessage("Passing 25 error messages. Muting error messages.");
    }

    public static boolean getOptionalBoolean(JsonObject o, String toGet, boolean ifNull, String location) {
        try {
            return o != null && o.has(toGet) ? o.get(toGet).getAsBoolean() : ifNull;
        }
        catch (Exception e) {
            if (o.has(toGet)) {
                Util.sendCompilationError("Failed to parse boolean \"" + o.get(toGet) + "\".", location + "." + toGet);
            }
            else {
                Util.sendCompilationError("Failed to parse boolean.", location + "." + toGet);
            }
            return false;
        }
    }

    public static String getOptionalString(JsonObject o, String toGet, String ifNull, String location) {
        try {
            return o != null && o.has(toGet) ? o.get(toGet).getAsString() : ifNull;
        }
        catch (Exception e) {
            if (o.has(toGet)) {
                Util.sendCompilationError("Failed to parse string \"" + o.get(toGet) + "\".", location + "." + toGet);
            }
            else {
                Util.sendCompilationError("Failed to parse string.", location + "." + toGet);
            }
            return "";
        }
    }

    public static String getOptionalTexture(JsonObject o, String toGet, String ifNull, String location) {
        String texture = getOptionalString(o, toGet, ifNull, location);
        try {
            ImageIO.read(VMinecraftInstance.getResource(texture));
        }
        catch (Exception e) {
            Util.sendCompilationError("Invalid texture path \"" + texture + "\".", location + "." + toGet);
        }
        return texture;
    }

    public static double getOptionalDouble(JsonObject o, String toGet, double ifNull, String location) {
        try {
            return o != null && o.has(toGet) ? o.get(toGet).getAsDouble() : ifNull;
        }
        catch (Exception e) {
            if (o.has(toGet)) {
                Util.sendCompilationError("Failed to parse double \"" + o.get(toGet) + "\".", location + "." + toGet);
            }
            else {
                Util.sendCompilationError("Failed to parse double.", location + "." + toGet);
            }
            return 0;
        }
    }

    public static int getOptionalInteger(JsonObject o, String toGet, int ifNull, String location) {
        try {
            return o != null && o.has(toGet) ? o.get(toGet).getAsInt() : ifNull;
        }
        catch (Exception e) {
            if (o.has(toGet)) {
                Util.sendCompilationError("Failed to parse integer \"" + o.get(toGet) + "\".", location + "." + toGet);
            }
            else {
                Util.sendCompilationError("Failed to parse integer.", location + "." + toGet);
            }
            return 0;
        }
    }

    public static ArrayList<String> getOptionalStringArray(JsonObject o, String toGet, ArrayList<String> ifNull) {
        return o != null && o.has(toGet) ? convertToStringArrayList(o.get(toGet).getAsJsonArray()) : ifNull;
    }

    public static ArrayList<String> convertToStringArrayList(JsonArray array) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (JsonElement o : array) {
            toReturn.add(o.getAsString());
        }
        return toReturn;
    }

    public static int getDecimal(Color color) {
        return color.getRGB();
    }

    public static double generateRandomDouble(double min, double max) {
        return min + ((max - min) * random.nextDouble());
    }

    public static int generateRandomInt(int min, int max) {
        return min + ((max - min) * random.nextInt());
    }

    public static Color decodeColor(String hex, String location) {
        try {
            return decodeColor(hex);
        } catch (Exception ignored) {
            sendError("Failed to parse HEX color \"" + hex + "\".", location);
            return new Color(255, 255, 255);
        }
    }
    public static Color decodeColor(String hex) throws NumberFormatException {
        switch (hex) {
            case "#skyColor":
                return Util.getSkyColor();
            case "#fogColor":
                return Util.getFogColor();
        }
        if (CelestialSky.isColorEntry(hex)) {
            return CelestialSky.getColorEntry(hex);
        }
        return Color.decode(hex.startsWith("#") ? hex : "#" + hex);
    }
    public static double getRedFromColor(String color) throws Exception {
        try {
            return decodeColor(color).getRed() / 255.0;
        }
        catch (Exception e) {
            if (!CelestialSky.initializingColorEntries && !CelestialSky.isColorEntry(color)) {
                throw new Exception("Failed to parse HEX color \"" + color + "\" in colorEntryRed function.");
            }
            return 0.0;
        }
    }
    public static double getGreenFromColor(String color) throws Exception {
        try {
            return decodeColor(color).getGreen() / 255.0;
        }
        catch (Exception e) {
            if (!CelestialSky.initializingColorEntries && !CelestialSky.isColorEntry(color)) {
                throw new Exception("Failed to parse HEX color \"" + color + "\" in colorEntryGreen function.");
            }
            return 0.0;
        }
    }
    public static double getBlueFromColor(String color) throws Exception {
        try {
            return decodeColor(color).getBlue() / 255.0;
        }
        catch (Exception e) {
            if (!CelestialSky.initializingColorEntries && !CelestialSky.isColorEntry(color)) {
                throw new Exception("Failed to parse HEX color \"" + color + "\" in colorEntryBlue function.");
            }
            return 0.0;
        }
    }

    public static int getTime(int id) {
        switch (id) {
            case 0:
                return LocalDate.now().getDayOfYear();
            case 1:
                return LocalDate.now().getDayOfMonth();
            case 2:
                return LocalDate.now().getDayOfWeek().getValue();
            case 3:
                return LocalDate.now().getMonthValue();
            case 4:
                return LocalDate.now().getYear();
            case 5:
                return LocalDate.now().atTime(LocalTime.now()).getSecond();
            case 6:
                return LocalDate.now().atTime(LocalTime.now()).getMinute();
            default:
                return 0;
        }
    }

    public static long getTotalMilliseconds() {
        return (Calendar.getInstance().getTimeInMillis() + Calendar.getInstance().get(Calendar.ZONE_OFFSET) +
                Calendar.getInstance().get(Calendar.DST_OFFSET)) %
                (24 * 60 * 60 * 1000);
    }

    public static int getTotalSeconds() {
        return (getTotalMinutes() * 60) + LocalDate.now().atTime(LocalTime.now()).getSecond();
    }

    public static int getTotalMinutes() {
        return (LocalDate.now().atTime(LocalTime.now()).getHour() * 60) + LocalDate.now().atTime(LocalTime.now()).getMinute();
    }

    public static ArrayList<VertexPoint> convertToPointUvList(JsonObject o, String name, String location) {

        ArrayList<VertexPoint> returnList = new ArrayList<>();
        try {
            if (!o.has(name))
                return new ArrayList<>();
            for (JsonElement e : o.getAsJsonArray(name)) {
                JsonObject entry = e.getAsJsonObject();
                returnList.add(
                        new VertexPoint(
                                getOptionalString(entry, "x", "", location),
                                getOptionalString(entry, "y", "", location),
                                getOptionalString(entry, "z", "", location),
                                getOptionalString(entry, "uv_x", null, location),
                                getOptionalString(entry, "uv_y", null, location),
                                location
                        )
                );
            }
        } catch (Exception e) {
            sendError("Failed to parse vertex point list \"" + name + "\".", location);
            return new ArrayList<>();
        }

        return returnList;
    }

    public static class VertexPoint {
        public CelestialExpression pointX;
        public CelestialExpression pointY;
        public CelestialExpression pointZ;
        public CelestialExpression uvX;
        public CelestialExpression uvY;

        public boolean hasUv;

        public VertexPoint(String pointX, String pointY, String pointZ, String uvX, String uvY, String location) {
            this.pointX = Util.compileExpression(pointX, location + ".x");
            this.pointY = Util.compileExpression(pointY, location + ".y");
            this.pointZ = Util.compileExpression(pointZ, location + ".z");

            this.hasUv = uvX != null || uvY != null;

            this.uvX = Util.compileExpression(uvX, location + ".uv_x");
            this.uvY = Util.compileExpression(uvY, location + ".uv_x");
        }
    }

    public static class VertexPointValue {
        public double pointX;
        public double pointY;
        public double pointZ;
        public double uvX = 0;
        public double uvY = 0;

        public boolean hasUv;

        public VertexPointValue(VertexPoint point) {
            this.pointX = point.pointX.invoke();
            this.pointY = point.pointY.invoke();
            this.pointZ = point.pointZ.invoke();

            this.hasUv = point.hasUv;

            if (this.hasUv) {
                this.uvX = point.uvX.invoke();
                this.uvY = point.uvY.invoke();
            }
        }
    }

    public static boolean isUsing(String... item) {
        return VMinecraftInstance.isRightClicking() && isHolding(item);
    }

    public static boolean isMiningWith(String... item) {
        return VMinecraftInstance.isLeftClicking() && isHolding(item);
    }

    public static boolean isHolding(String... items) {
        for (String item : items) {
            if (item.contains(":")) {
                String[] str = item.split(":");
                return (VMinecraftInstance.getMainHandItemNamespace().equals(str[0])) &&
                        (VMinecraftInstance.getMainHandItemPath().equals(str[1]));
            } else {
                return (VMinecraftInstance.getMainHandItemPath().equals(item));
            }
        }
        return false;
    }

    public static boolean isInArea(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (Math.min(x1, x2) <= VMinecraftInstance.getPlayerX() && VMinecraftInstance.getPlayerX() <= Math.max(x1, x2)) &&
                    (Math.min(y1, y2) <= VMinecraftInstance.getPlayerY() && VMinecraftInstance.getPlayerY() <= Math.max(y1, y2)) &&
                    (Math.min(z1, z2) <= VMinecraftInstance.getPlayerZ() && VMinecraftInstance.getPlayerZ() <= Math.max(z1, z2));

    }

    public static double distanceTo(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
    }

    static float getDistance(float a, float b) {
        if (a <= 0) {
            if (b <= 0) return 0F;
            else return b;
        } else if (b <= 0)
            return a;
        return ((float) Math.sqrt(((double) a * a + b * b)));
    }

    public static double distanceToArea(double px, double py, double pz, double x1, double y1, double z1, double x2, double y2, double z2) {
        double minX = Float.min((float) x1, (float) x2);
        double maxX = Float.max((float) x1, (float) x2);
        double minY = Float.min((float) y1, (float) y2);
        double maxY = Float.max((float) y1, (float) y2);
        double minZ = Float.min((float) z1, (float) z2);
        double maxZ = Float.max((float) z1, (float) z2);

        float[] axisDistances = new float[3];

        {
            double min = minX - px;
            double max = px - maxX;
            axisDistances[0] = Float.max((float) min, (float) max);
        }
        {
            double min = minY - py;
            double max = py - maxY;
            axisDistances[1] = Float.max((float) min, (float) max);
        }
        {
            double min = minZ - pz;
            double max = pz - maxZ;
            axisDistances[2] = Float.max((float) min, (float) max);
        }

        return getDistance(
                getDistance(
                        axisDistances[0],
                        axisDistances[1]
                ),
                axisDistances[2]
        );
    }
    public static double getBiomeBlend(String... args) {
        if (NumberUtils.isCreatable(args[0])) {
            return getBiomeBlend((int) Double.parseDouble(args[0]), -9999, args);
        }
        return getBiomeBlend(6, -9999, args);
    }
    public static double getBiomeBlendFlat(String... args) {
        if (NumberUtils.isCreatable(args[0]) && NumberUtils.isCreatable(args[1])) {
            return getBiomeBlend((float) Double.parseDouble(args[0]), (float) Double.parseDouble(args[1]), args);
        }
        else if (NumberUtils.isCreatable(args[0])) {
            return getBiomeBlend((float) Double.parseDouble(args[0]), (float) VMinecraftInstance.getPlayerY(), args);
        }
        return getBiomeBlend(6.0F, (float) VMinecraftInstance.getPlayerY(), args);
    }

    public static double getBiomeBlend(float searchDistance, float y, String... biomeName) {
        if (isInBiome(biomeName))
            return 1;
        boolean foundSpot = false;
        double dist;
        double closestDist = searchDistance;
        Vector pos = new Vector(0, 0, 0);
        for (float i = -searchDistance; i <= searchDistance; i++) {
            for (float j = (y == -9999 ? -searchDistance : 0); (y == -9999 ? j <= searchDistance : j == 0); j++) {
                for (float k = -searchDistance; k <= searchDistance; k++) {
                    pos.set(i + VMinecraftInstance.getPlayerX(),
                            y == -9999 ? j + VMinecraftInstance.getPlayerY() : y,
                            k + VMinecraftInstance.getPlayerZ());

                    if (VMinecraftInstance.equalToBiome(pos, biomeName)) {
                        dist = distanceTo(pos.x, pos.y, pos.z,
                                VMinecraftInstance.getPlayerX(),
                                y == -9999 ? VMinecraftInstance.getPlayerY() : y,
                                VMinecraftInstance.getPlayerZ());
                        if ((!foundSpot || dist < closestDist)) {
                            closestDist = distanceToArea(VMinecraftInstance.getPlayerX(), y == -9999 ? VMinecraftInstance.getPlayerY() : y, VMinecraftInstance.getPlayerZ(),
                                    pos.x - 0.5, pos.y - 0.5, pos.z + 0.5, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
                            foundSpot = true;
                        }
                    }
                }
            }
        }

        if (foundSpot) {
            double a = 0.05 * searchDistance;
            a = 0.2 + Math.max(a, 0.25);
            closestDist = ((closestDist - a) / searchDistance) > 1 ? 1 : ((closestDist - a) / searchDistance);
            closestDist = closestDist < 0 ? 0 : closestDist;
            closestDist = 1 - closestDist;

            return closestDist;
        }
        return 0;
    }

    public static double getBiomeBlendIgnoreY(String biomeName, int searchDistance, double yLevel) {
        if (isInBiome(biomeName))
            return 1;
        boolean foundSpot = false;
        double dist;
        double closestDist = searchDistance;
        Vector pos = new Vector(0, 0, 0);
        for (int i = -searchDistance; i <= searchDistance; i++) {
            for (int k = -searchDistance; k <= searchDistance; k++) {
                pos.set(i + VMinecraftInstance.getPlayerX(), yLevel, k + VMinecraftInstance.getPlayerZ());
                dist = distanceTo(pos.x, yLevel, pos.z,
                        VMinecraftInstance.getPlayerX(),
                        yLevel,
                        VMinecraftInstance.getPlayerZ());
                if (VMinecraftInstance.equalToBiome(pos, biomeName) && (!foundSpot || dist < closestDist)) {
                    closestDist = dist;
                    foundSpot = true;
                }
            }
        }

        if (foundSpot) {
            double a = 0.05 * searchDistance;
            a = 0.2 + Math.max(a, 0.25);
            closestDist = ((closestDist - a) / searchDistance) > 1 ? 1 : ((closestDist - a) / searchDistance);
            closestDist = closestDist < 0 ? 0 : closestDist;
            closestDist = 1 - closestDist;
            return closestDist;
        }
        return 0;
    }

    public static boolean isInBiome(String... biome) {
        return VMinecraftInstance.equalToBiome(null, biome);
    }

    public static boolean getRealSkyColor = false;
    public static boolean getRealFogColor = false;

    public static Color getSkyColor() {
        double[] i = VMinecraftInstance.getBiomeSkyColor();
        return new Color((int) (i[0] * 255), (int) (i[1] * 255), (int) (i[2] * 255));
    }

    public static Color getFogColor() {
        double[] i = VMinecraftInstance.getBiomeFogColor();
        return new Color((int) (i[0] * 255), (int) (i[1] * 255), (int) (i[2] * 255));
    }
    public static double getTwilightAlpha(double timeOfDay) {
        float g = VMath.cos((float) ((timeOfDay / 360) * (Math.PI * 2)));
        if (g >= -0.4F && g <= 0.4F)
            return Math.pow(1.0F - (1.0F - VMath.sin((float) (((g) / 0.4F * 0.5F + 0.5F) * Math.PI))) * 0.99F, 2);
        return 0;
    }
    public static double getTwilightProgress(double timeOfDay) {
        float i = VMath.cos((float) ((timeOfDay / 360) * (Math.PI * 2)));
        if (i >= -0.4F && i <= 0.4F) {
            return i / 0.4F * 0.5F + 0.5F;
        }
        return 0;
    }
    public static double getTwilightFogEffect(double timeOfDay) {
        float h = VMath.sin((float) timeOfDay / 360.0F) > 0.0F ? -1.0F : 1.0F;
        float s = VMinecraftInstance.getCameraLookVectorTwilight(h);
        if (s < 0) {
            s = 0;
        }
        return s > 0 ? s : 0;
    }
    public static double getStarAlpha(double timeOfDay) {
        float g = (float) (timeOfDay / 360);
        float h = 1.0F - (Mth.cos(g * 6.2831855F) * 2.0F + 0.25F);
        h = Mth.clamp(h, 0.0F, 1.0F);
        return h * h * 0.5F;
    }

    public static double getDayLight(double timeOfDay) {
        return clamp((float) (VMath.cos((float) timeOfDay / 360) * 2 + 0.5), 0, 1);
    }
    public static double clamp(float x, float min, float max) {
        return Math.max(Math.min(x, max), min);
    }
    public static double lerp(float a, float b, float ratio) {
        return (a * ratio) + b * (1 - ratio);
    }
    public static double repeat(float a, float min, float max) {
        return (a % (max - min)) + min;
    }
}
