package fishcute.celestialmain.util;

public class FMath {
    public static float sin(float rhs) {
        return (float) Math.sin(rhs);
    }

    public static float cos(float rhs) {
        return (float) Math.cos(rhs);
    }

    public static float tan(float rhs) {
        return (float) Math.tan(rhs);
    }

    public static float clamp(float v, float v1, float v2) {
        if (v < v1) return v1;
        else if (v > v2) return v2;
        return v;
    }
}
