package fishcute.celestialmain.api.minecraft.wrappers;

public interface IBufferBuilderWrapper {
     void beginTriangleFan();
     void beginObject();
     void beginColorObject();
     void vertex(IMatrix4fWrapper matrix4f, float x, float y, float z, float r, float g, float b, float a);
     void vertexUv(IMatrix4fWrapper matrix4f, float x, float y, float z, float u, float v, float r, float g, float b, float a);

     void upload();

     IBufferBuilderWrapper init();

     interface Factory {
          IBufferBuilderWrapper build();
     }
}
