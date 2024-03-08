package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.api.minecraft.IMcVector;
import fishcute.celestialmain.api.minecraft.IMinecraftInstance;
import fishcute.celestialmain.api.minecraft.IRenderSystem;
import fishcute.celestialmain.api.minecraft.wrappers.IBufferBuilderWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;
import fishcute.celestialmain.api.minecraft.wrappers.IShaderInstanceWrapper;

public class Instances {
    public static IRenderSystem renderSystem = null;
    public static IMinecraftInstance minecraft = null;

    public static IBufferBuilderWrapper.Factory bufferBuilderFactory = null;
    public static IShaderInstanceWrapper.Factory shaderInstanceFactory = null;
    public static IResourceLocationWrapper.Factory resourceLocationFactory = null;
    public static IMcVector.Factory vectorFactory = null;

}
