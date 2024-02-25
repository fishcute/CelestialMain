package fishcute.celestial.util;


import org.apache.commons.math3.complex.Quaternion;
import org.joml.Matrix3f;

public interface CopyMatrix4f {
    void copyQuaternion(Quaternion quaternion);

    void copyMatrix3f(Matrix3f matrix3f);
}
