package fishcute.celestialmain.version.independent;

import fishcute.celestialmain.api.minecraft.IMcVector;
import fishcute.celestialmain.util.FMath;
import fishcute.celestialmain.api.minecraft.wrappers.*;
import fishcute.celestialmain.sky.CelestialRenderInfo;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.objects.ICelestialObject;
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

    /*


    private static Object[] getObjectDataArray(CelestialObject c, HashMap<String, Util.DynamicValue> replaceMap) {

        Object[] dataArray = new Object[13];

        //degrees x 0
        dataArray[0] = c.degreesX.invoke().floatValue();

        //degrees y 1
        dataArray[1] = c.degreesY.invoke().floatValue();

        //degrees z 2
        dataArray[2] = c.degreesZ.invoke().floatValue();

        //pos x 3
        dataArray[3] = c.posX.invoke().floatValue();

        //pos y 4
        dataArray[4] = c.posY.invoke().floatValue();

        //pos z 5
        dataArray[5] = c.posZ.invoke().floatValue();

        //alpha 6
        dataArray[6] = c.celestialObjectProperties.alpha.invoke().floatValue();

        //distance 7
        dataArray[7] = c.distance.invoke().floatValue();

        //scale 8
        dataArray[8] = c.scale.invoke().floatValue();

        //moon phase 9
        dataArray[9] = c.celestialObjectProperties.moonPhase.invoke().intValue();

        ArrayList<Util.VertexPointValue> vertexList = new ArrayList<>();

        if (c.vertexList != null && c.vertexList.size() > 0)
            for (Util.VertexPoint v : c.vertexList)
                vertexList.add(new Util.VertexPointValue(v));

        // vertex list 10
        dataArray[10] = (vertexList);

        // colors 11
        dataArray[11] = (new Vector(
                c.celestialObjectProperties.getRed(),
                c.celestialObjectProperties.getGreen(),
                c.celestialObjectProperties.getBlue()));

        //solid colors 12
        if (c.solidColor != null)
            dataArray[12] = (new Vector(
                    (c.solidColor.storedColor.getRed()) * (((Vector) dataArray[11]).x),
                    (c.solidColor.storedColor.getGreen()) * (((Vector) dataArray[11]).y),
                    (c.solidColor.storedColor.getBlue()) * (((Vector) dataArray[11]).z)));
        else
            dataArray[12] = (null);
        return dataArray;
    }

    private static void renderSkyObject(IBufferBuilderWrapper bufferBuilder, IPoseStackWrapper matrices, Object matrix4f2, CelestialObject c, IMcVector color, IMcVector colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<Util.VertexPointValue> vertexList, HashMap<String, Util.DynamicValue> objectReplaceMap) {
        Instances.renderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        Instances.renderSystem.toggleBlend(true);

        // Set texture
        if (c.texture != null)
            Instances.renderSystem.setShaderTexture(0, c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            Instances.renderSystem.setupNoFog();

        else
            Instances.renderSystem.levelFogColor();

        if (c.celestialObjectProperties.isSolid)
            Instances.renderSystem.defaultBlendFunc();

        if (c.type.equals(CelestialObject.CelestialObjectType.DEFAULT)) {
            Instances.renderSystem.setShaderPositionTex();
            Instances.renderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, alpha);

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.beginObject();
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        f15, f16, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        f13, f16, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        f13, f14, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        f15, f14, (float) color.x, (float) color.y, (float) color.z, alpha);
            } else if (vertexList.size() > 0) {
                bufferBuilder.beginObject();
                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertexUv(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                            (float) v.uvX, (float) v.uvY, (float) color.x, (float) color.y, (float) color.z, alpha);
                }
            } else {
                bufferBuilder.beginObject();
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        0.0F, 0.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        1.0F, 0.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        1.0F, 1.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
                bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        0.0F, 1.0F, (float) color.x, (float) color.y, (float) color.z, alpha);
            }

            bufferBuilder.upload();
        } else if (c.type.equals(CelestialObject.CelestialObjectType.COLOR)) {
            Instances.renderSystem.setShaderPositionColor();
            Instances.renderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            Instances.renderSystem.toggleTexture(false);

            if (vertexList.size() > 0) {
                bufferBuilder.beginColorObject();

                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertex(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                            (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                }
            } else {
                bufferBuilder.beginColorObject();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                        (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
            }

            bufferBuilder.upload();

            Instances.renderSystem.toggleTexture(true);
        } else if (c.type.equals(CelestialObject.CelestialObjectType.SKYBOX)) {
            matrices.popPose();

            SkyBoxObjectProperties.SkyBoxSideTexture side;
            float size;
            float textureX;
            float textureY;
            float textureScaleX;
            float textureScaleY;

            float uvX;
            float uvY;
            float uvSizeX;
            float uvSizeY;
            float textureSizeX = c.solidColor != null ? 0 : c.skyBoxProperties.textureSizeX.invoke().floatValue();
            float textureSizeY = c.solidColor != null ? 0 : c.skyBoxProperties.textureSizeY.invoke().floatValue();

            for (int l = 0; l < 6; ++l) {
                matrices.pushPose();
                side = c.skyBoxProperties.sides.get(l);
                if (c.solidColor == null) {
                    Instances.renderSystem.setShaderTexture(0, side.texture);
                    Instances.renderSystem.setShaderPositionTex();
                } else {
                    Instances.renderSystem.setShaderPositionColor();
                }
                if (l == 0) {
                    matrices.mulPose(IPoseStackWrapper.Axis.Y, 180);
                }
                if (l == 1) {
                    matrices.mulPose(IPoseStackWrapper.Axis.X, 90);
                }

                if (l == 2) {
                    matrices.mulPose(IPoseStackWrapper.Axis.X, -90);
                    matrices.mulPose(IPoseStackWrapper.Axis.Y, 180);
                }

                if (l == 3) {
                    matrices.mulPose(IPoseStackWrapper.Axis.X, 180);
                    matrices.mulPose(IPoseStackWrapper.Axis.Y, 180);
                }

                if (l == 4) {
                    matrices.mulPose(IPoseStackWrapper.Axis.Y, 90);
                    matrices.mulPose(IPoseStackWrapper.Axis.Z, -90);
                }

                if (l == 5) {
                    matrices.mulPose(IPoseStackWrapper.Axis.Y, -90);
                    matrices.mulPose(IPoseStackWrapper.Axis.Z, 90);
                }

                size = c.skyBoxProperties.skyBoxSize.invoke().floatValue();

                Object matrix4f3 = matrices.lastPose();

                if (c.solidColor != null) {
                    Instances.renderSystem.toggleTexture(false);
                    bufferBuilder.beginColorObject();

                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.upload();

                    Instances.renderSystem.toggleTexture(true);
                } else {
                    uvX = side.uvX.invoke().floatValue();
                    uvY = side.uvY.invoke().floatValue();
                    uvSizeX = side.uvSizeX.invoke().floatValue();
                    uvSizeY = side.uvSizeY.invoke().floatValue();

                    textureX = (uvX / textureSizeX);
                    textureY = (uvY / textureSizeY);
                    textureScaleX = textureX + (uvSizeX / textureSizeX);
                    textureScaleY = textureY + (uvSizeY / textureSizeY);

                    if (textureX >= 0 && textureY >= 0 && textureScaleX >= 0 && textureScaleY >= 0) {
                        bufferBuilder.beginObject();
                        bufferBuilder.vertexUv(matrix4f3, -size, -size, -size, textureX, textureY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, -size, -size, size, textureX, textureScaleY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, size, -size, size, textureScaleX, textureScaleY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.vertexUv(matrix4f3, size, -size, -size, textureScaleX, textureY, (float) color.x, (float) color.y, (float) color.z, alpha);
                        bufferBuilder.upload();
                    }
                }
                matrices.popPose();
            }
            matrices.pushPose();
        }

        if (c.celestialObjectProperties.isSolid)
            Instances.renderSystem.blendFuncSeparate();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }*/
}
