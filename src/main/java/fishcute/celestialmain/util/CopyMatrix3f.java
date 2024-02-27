package fishcute.celestialmain.util;


import org.apache.commons.math3.complex.Quaternion;
import org.joml.Vector3f;

public interface CopyMatrix3f {

    float getM00();
    float getM01();
    float getM02();
    float getM10();
    float getM11();
    float getM12();
    float getM20();
    float getM21();
    float getM22();
    void copyQuaternion(Quaternion quaternion);
    void setAxisAngle(Vector3f axis, float angle);
}
