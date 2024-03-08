package fishcute.celestialmain.api.minecraft.wrappers;

public interface IBufferBuilderWrapper {
     void celestial$beginTriangleFan();
     void celestial$beginObject();
     void celestial$beginColorObject();
     void celestial$vertex(IMatrix4fWrapper matrix4f, float x, float y, float z, float r, float g, float b, float a);
     void celestial$vertexUv(IMatrix4fWrapper matrix4f, float x, float y, float z, float u, float v, float r, float g, float b, float a);

     void celestial$upload();

     //IBufferBuilderWrapper celestial$init();

     interface Factory {
          IBufferBuilderWrapper build();
     }
}
