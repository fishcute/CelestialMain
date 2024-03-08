package fishcute.celestialmain.api.minecraft.wrappers;

import fishcute.celestialmain.api.minecraft.IMcVector;

public interface ILevelWrapper {
     IMcVector celestial$getSkyColor(float tickDelta);

     float[] celestial$getSunriseColor(float tickDelta);

     float celestial$getTimeOfDay(float tickDelta);
     float celestial$getSunAngle(float tickDelta);
     double celestial$getHorizonHeight();
     boolean celestial$hasGround();
}
