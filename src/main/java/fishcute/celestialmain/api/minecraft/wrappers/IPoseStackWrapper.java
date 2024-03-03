package fishcute.celestialmain.api.minecraft.wrappers;

import fishcute.celestialmain.api.math.IMatrix4f;
import fishcute.celestialmain.api.math.IMatrix3f;
import fishcute.celestialmain.api.math.IQuaternion;

public interface IPoseStackWrapper {
     IMatrix4fWrapper lastPose();
     void pushPose();
     void popPose();
     void translate(double x, double y, double z);
     void mulPose(Axis a, float rot);

     void mulPose(IQuaternion quaternion);


     void mulPose(IQuaternion quaternion, IMatrix4f intermediate4, IMatrix3f intermediate3);

     void mulPose(IMatrix3f matrix3f, IMatrix4f intermediate4);

     enum Axis {
        X,
        Y,
        Z
    }

     IMatrix4fWrapper rotate(float i, float j, float k);

     IMatrix4fWrapper rotateThenTranslate(float i, float j, float k, float x, float y, float z);

     IMatrix4fWrapper translateThenRotate(float i, float j, float k, float x, float y, float z);
}
