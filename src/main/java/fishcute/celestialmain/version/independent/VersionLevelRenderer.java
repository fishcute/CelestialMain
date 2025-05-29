package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.api.minecraft.IMcVector;
import fishcute.celestialmain.sky.objects.TwilightObject;
import fishcute.celestialmain.util.FMath;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialRenderInfo;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.objects.ICelestialObject;
import fishcute.celestialmain.util.Util;
import org.jetbrains.annotations.Nullable;

public class VersionLevelRenderer {
    // Only needed for 1.16
    public abstract static class RunnableArg implements Runnable {
        public boolean b;
        public RunnableArg() {
        }
        public void run(boolean b) {
            this.b = b;
            this.run();
        }
    }

    public static void renderLevel(Object projectionMatrix, IPoseStackWrapper matrices, IVertexBufferWrapper skyBuffer, IVertexBufferWrapper darkBuffer, ICameraWrapper camera, ILevelWrapper level, float tickDelta, @Nullable RunnableArg skyFormat) {
        if (camera.celestial$doesFogBlockSky() && !(camera.celestial$doesMobEffectBlockSky())) {
            // Init stuff
            Instances.renderSystem.toggleTexture(false);
            IMcVector Vector3d = level.celestial$getSkyColor(tickDelta);

            float f = Vector3d.x();
            float g = Vector3d.y();
            float h = Vector3d.z();

            Instances.renderSystem.levelFogColor();
            IBufferBuilderWrapper bufferBuilder = Instances.bufferBuilderFactory.build();
            Instances.renderSystem.depthMask(false);
            Instances.renderSystem.setShaderColor(f, g, h, 1.0F);

            IShaderInstanceWrapper shader = Instances.shaderInstanceFactory.build();

            Instances.renderSystem.unbindVertexBuffer();
            Instances.renderSystem.toggleBlend(true);
            Instances.renderSystem.defaultBlendFunction();

            skyBuffer.celestial$bind();

            if (skyFormat != null) { skyFormat.run(true); }
            skyBuffer.celestial$drawWithShader(matrices.celestial$lastPose(), projectionMatrix, shader);
            if (skyFormat != null) { skyFormat.run(false); }

            Instances.renderSystem.unbindVertexBuffer();

            // 1.16 only
            Instances.renderSystem.shadeModel(7425);

            float[] fs = level.celestial$getSunriseColor(tickDelta);
            if (fs != null) {
                Instances.renderSystem.setShaderPositionColor();
                Instances.renderSystem.toggleTexture(false);
                Instances.renderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                matrices.celestial$pushPose();
                matrices.celestial$mulPose(IPoseStackWrapper.Axis.X, 90.0F);
                float f3 = Math.sin(level.celestial$getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, f3);
                matrices.celestial$mulPose(IPoseStackWrapper.Axis.Z, 90.0F);
                float j = fs[0];
                float k = fs[1];
                float l = fs[2];
                Object matrix4f = matrices.celestial$lastPose();
                bufferBuilder.celestial$beginTriangleFan();
                bufferBuilder.celestial$vertex(matrix4f, 0.0F, 100.0F, 0.0F, j, k, l, fs[3]);

                for (int n = 0; n <= 16; ++n) {
                    float o = (float) n * 6.2831855F / 16.0F;
                    float p = FMath.sin(o);
                    float q = FMath.cos(o);
                    bufferBuilder.celestial$vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3], fs[0], fs[1], fs[2], 0.0F);
                }

                bufferBuilder.celestial$upload();
                matrices.celestial$popPose();
            }

            CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();

            renderSkyObjects(matrices, bufferBuilder, renderInfo);

            // 1.16 only
            Instances.renderSystem.shadeModel(7424);

            // Reset render system after sky objects
            Instances.renderSystem.levelFogColor();
            Instances.renderSystem.toggleBlend(false);
            Instances.renderSystem.toggleTexture(false);
            Instances.renderSystem.blendFuncSeparate();
            Instances.renderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

            // Void culling rendering
            double d = Instances.minecraft.getPlayerEyePosition().y() - level.celestial$getHorizonHeight();
            if (d < 0.0) {
                matrices.celestial$pushPose();
                matrices.celestial$translate(0.0, 12.0 + renderInfo.environment.voidCullingLevel.invoke(), 0.0);
                darkBuffer.celestial$bind();

                if (skyFormat != null) { skyFormat.run(true); }
                darkBuffer.celestial$drawWithShader(matrices.celestial$lastPose(), projectionMatrix, shader);
                if (skyFormat != null) { skyFormat.run(false); }

                Instances.renderSystem.unbindVertexBuffer();
                matrices.celestial$popPose();
            }

            // Dark sky rendering
            if (level.celestial$hasGround()) {
                Instances.renderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
            } else {
                Instances.renderSystem.setShaderColor(f, g, h, 1.0F);
            }

            // Final reset
            Instances.renderSystem.toggleTexture(true);
            Instances.renderSystem.depthMask(true);
            Instances.renderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderSkyObjects(IPoseStackWrapper matrices, IBufferBuilderWrapper bufferBuilder, CelestialRenderInfo renderInfo) {
        for (ICelestialObject c : renderInfo.skyObjects) {
            // Different push/pop functions so that pushing and popping can be handled differently for skybox objects
            c.pushPose(matrices);
            c.render(bufferBuilder, matrices, matrices.celestial$lastPose());
            c.popPose(matrices);
        }
    }
}
