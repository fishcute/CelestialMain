package fishcute.celestialmain.api.minecraft.wrappers;

public interface IVertexBufferWrapper {
    void celestial$bind();
    void celestial$drawWithShader(IMatrix4fWrapper matrix, IMatrix4fWrapper projectionMatrix, IShaderInstanceWrapper shader);
}
