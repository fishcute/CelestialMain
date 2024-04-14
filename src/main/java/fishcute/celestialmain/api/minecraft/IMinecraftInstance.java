package fishcute.celestialmain.api.minecraft;

import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;

import java.io.IOException;
import java.io.InputStream;

public interface IMinecraftInstance {
    boolean doesLevelExist();
    boolean doesPlayerExist();
    String getLevelPath();
    float getTickDelta();
    IMcVector getPlayerEyePosition();

    void sendFormattedErrorMessage(String error, String type, String location);
    void sendInfoMessage(String i);
    void sendErrorMessage(String i);
    void sendRedMessage(String i);
    InputStream getResource(String path) throws IOException;
    boolean isGamePaused();
    void sendMessage(String text, boolean actionBar);
    double getPlayerX();
    double getPlayerY();
    double getPlayerZ();
    double getRainLevel();
    boolean isPlayerInWater();
    long getGameTime();
    long getWorldTime();
    float getStarBrightness();
    float getTimeOfDay();
    float getViewXRot();
    float getViewYRot();
    float getCameraLookVectorTwilight(float h);
    float getRenderDistance();
    float getMoonPhase();
    float getSkyDarken();
    float getBossSkyDarken();
    float getSkyFlashTime();
    float getThunderLevel();
    float getSkyLight();
    float getBlockLight();
    float getBiomeTemperature();
    float getBiomeDownfall();
    boolean getBiomeSnow();
    boolean isRightClicking();
    boolean isLeftClicking();
    IResourceLocationWrapper getMainHandItemKey();
    String getMainHandItemNamespace();
    String getMainHandItemPath();

    boolean equalToBiome(IMcVector position, String... name);
    double[] getBiomeSkyColor();
    double[] getBiomeFogColor();

    boolean disableFogChanges();
    boolean isCameraInWater();
    double getNightVisionModifier();
    boolean isSneaking();

    float getDarknessFogEffect(float fogStart);
    boolean hasDarkness();
}
