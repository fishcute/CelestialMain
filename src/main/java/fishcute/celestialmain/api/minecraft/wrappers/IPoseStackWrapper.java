package fishcute.celestialmain.api.minecraft.wrappers;


public interface IPoseStackWrapper {
     Object celestial$lastPose();
     void celestial$pushPose();
     void celestial$popPose();
     void celestial$translate(double x, double y, double z);
     void celestial$mulPose(Axis a, float rot);

     void celestial$mulPose(Object quaternion);


     void celestial$mulPose(Object quaternion, Object intermediate4, Object intermediate3);

     void celestial$mulPose(Object matrix3f, Object intermediate4);

     enum Axis {
        X,
        Y,
        Z
    }

     Object celestial$rotate(float i, float j, float k);

     Object celestial$rotateThenTranslate(float i, float j, float k, float x, float y, float z);

     Object celestial$translateThenRotate(float i, float j, float k, float x, float y, float z);
}
