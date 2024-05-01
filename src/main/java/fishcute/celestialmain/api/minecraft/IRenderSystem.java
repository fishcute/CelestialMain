package fishcute.celestialmain.api.minecraft;

import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;

public interface IRenderSystem {
     void setShaderFogStart(float start);
     void setShaderFogEnd(float end);
     void levelFogColor();
     void setupNoFog();
     void defaultBlendFunc();

     void depthMask(boolean enable);
     void setShaderColor(float f, float g, float h, float a);
     void clearColor(float f, float g, float h, float a);
     void unbindVertexBuffer();
     void toggleBlend(boolean enable);
     void defaultBlendFunction();
     void setShaderPositionColor();
     void setShaderPositionTex();

     void toggleTexture(boolean texture);
     void blendFuncSeparate();
     void setShaderTexture(int i, IResourceLocationWrapper j);
     void shadeModel(int i);
}
