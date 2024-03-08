package fishcute.celestialmain.api.minecraft;

public interface IMcVector {
    float x();
    float y();
    float z();
    float setX(float v);
    float setY(float v);
    float setZ(float v);

    void set(float x, float y, float z);

    default void set(double x, double y, double z) {
        this.set((float) x, (float) y, (float) z);
    }

    interface Factory {
        default IMcVector zero() {
            return this.build(0, 0, 0);
        }

        IMcVector build(float x, float y, float z);
    }
}
