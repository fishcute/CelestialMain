package fishcute.celestialmain.api.minecraft.wrappers;


public interface IPoseStackWrapper {
     IMatrix4fWrapper celestial$lastPose();
     void celestial$pushPose();
     void celestial$popPose();
     void celestial$translate(double x, double y, double z);
     void celestial$mulPose(Axis a, float rot);

     void celestial$mulPose(IQuaternionWrapper quaternion);


     void celestial$mulPose(IQuaternionWrapper quaternion, IMatrix4fWrapper intermediate4, IMatrix3fWrapper intermediate3);

     void celestial$mulPose(IMatrix3fWrapper matrix3f, IMatrix4fWrapper intermediate4);

     enum Axis {
        X,
        Y,
        Z
    }

     IMatrix4fWrapper celestial$rotate(float i, float j, float k);

     IMatrix4fWrapper celestial$rotateThenTranslate(float i, float j, float k, float x, float y, float z);

     IMatrix4fWrapper celestial$translateThenRotate(float i, float j, float k, float x, float y, float z);
}
