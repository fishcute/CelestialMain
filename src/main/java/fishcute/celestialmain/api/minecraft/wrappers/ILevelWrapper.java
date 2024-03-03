package fishcute.celestialmain.api.minecraft.wrappers;

import fishcute.celestialmain.api.minecraft.IMcVector;

public interface ILevelWrapper {
     IMcVector getSkyColor(float tickDelta);

     float[] getSunriseColor(float tickDelta);

     float getTimeOfDay(float tickDelta);
     float getSunAngle(float tickDelta);
     double getHorizonHeight();
     boolean hasGround();
}
