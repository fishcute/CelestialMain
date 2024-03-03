package fishcute.celestialmain.api.minecraft.wrappers;

public interface IVertexBufferWrapper {
    void bind();
    void drawWithShader(IMatrix4fWrapper matrix, IMatrix4fWrapper projectionMatrix, IShaderInstanceWrapper shader);
}
