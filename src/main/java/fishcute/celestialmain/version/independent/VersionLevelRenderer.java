package fishcute.celestialmain.version.independent;


import fishcute.celestial.sky.*;
import fishcute.celestial.version.dependent.*;
import fishcute.celestial.version.dependent.util.*;
import fishcute.celestialmain.sky.CelestialRenderInfo;
import fishcute.celestialmain.sky.CelestialSky;
import fishcute.celestialmain.sky.objects.ICelestialObject;

public class VersionLevelRenderer {

    public static void renderTwilight(ShaderInstanceWrapper shader, BufferBuilderWrapper bufferBuilder, float tickDelta, Matrix4fWrapper projectionMatrix, PoseStackWrapper matrices, VertexBufferWrapper skyBuffer, LevelWrapper level) {
        VRenderSystem.unbindVertexBuffer();
        VRenderSystem.toggleBlend(true);
        VRenderSystem.defaultBlendFunction();

        skyBuffer.bind();
        skyBuffer.drawWithShader(matrices.lastPose(), projectionMatrix, shader);
        float[] fs = level.getSunriseColor(tickDelta);
        if (fs != null) {
            VRenderSystem.setShaderPositionColor();
            VRenderSystem.toggleTexture(false);
            VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pushPose();
            matrices.mulPose(PoseStackWrapper.Axis.X, 90.0F);
            float f3 = Math.sin(level.getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F;
            matrices.mulPose(PoseStackWrapper.Axis.Z, f3);
            matrices.mulPose(PoseStackWrapper.Axis.Z, 90.0F);
            float j = fs[0];
            float k = fs[1];
            float l = fs[2];
            Matrix4fWrapper matrix4f = matrices.lastPose();
            bufferBuilder.beginTriangleFan();
            bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F, j, k, l, fs[3]);

            for (int n = 0; n <= 16; ++n) {
                float o = (float) n * 6.2831855F / 16.0F;
                float p = VMath.sin(o);
                float q = VMath.cos(o);
                bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3], fs[0], fs[1], fs[2], 0.0F);
            }

            bufferBuilder.upload();
            matrices.popPose();
        }
    }

    public static void renderLevel(Matrix4fWrapper projectionMatrix, PoseStackWrapper matrices, VertexBufferWrapper skyBuffer, VertexBufferWrapper darkBuffer, CameraWrapper camera, LevelWrapper level, float tickDelta) {
        if (camera.doesFogBlockSky() && !(camera.doesMobEffectBlockSky())) {
            VRenderSystem.toggleTexture(false);
            Vector Vector3d = level.getSkyColor(tickDelta);
            float f = (float) Vector3d.x;
            float g = (float) Vector3d.y;
            float h = (float) Vector3d.z;
            VRenderSystem.levelFogColor();
            BufferBuilderWrapper bufferBuilder = new BufferBuilderWrapper();
            VRenderSystem.depthMask(false);
            VRenderSystem.setShaderColor(f, g, h, 1.0F);

            ShaderInstanceWrapper shader = new ShaderInstanceWrapper();

            renderTwilight(shader, bufferBuilder, tickDelta, projectionMatrix, matrices, skyBuffer, level);

            CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();

            VRenderSystem.toggleTexture(true);
            VRenderSystem.toggleBlend(true);

            VRenderSystem.blendFuncSeparate();

            renderSkyObjects(matrices, bufferBuilder, renderInfo);

            VRenderSystem.levelFogColor();

            VRenderSystem.toggleBlend(false);
            VRenderSystem.toggleTexture(false);
            VRenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

            double d = VMinecraftInstance.getPlayerEyePosition().y - level.getHorizonHeight();
            if (d < 0.0) {
                matrices.pushPose();
                matrices.translate(0.0, 12.0 + renderInfo.environment.voidCullingLevel.invoke(), 0.0);
                darkBuffer.bind();
                darkBuffer.drawWithShader(matrices.lastPose(), projectionMatrix, shader);
                VRenderSystem.unbindVertexBuffer();
                matrices.popPose();
            }

            if (level.hasGround()) {
                VRenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
            } else {
                VRenderSystem.setShaderColor(f, g, h, 1.0F);
            }

            VRenderSystem.toggleTexture(true);
            VRenderSystem.depthMask(true);

            VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderSkyObjects(PoseStackWrapper matrices, BufferBuilderWrapper bufferBuilder, CelestialRenderInfo renderInfo) {
        for (ICelestialObject c : renderInfo.skyObjects) {
            // Different push/pop functions so that pushing and popping can be handled differently for skybox objects
            c.pushPose(matrices);
            c.render(bufferBuilder, matrices, matrices.lastPose());
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

    private static void renderSkyObject(BufferBuilderWrapper bufferBuilder, PoseStackWrapper matrices, Matrix4fWrapper matrix4f2, CelestialObject c, Vector color, Vector colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<Util.VertexPointValue> vertexList, HashMap<String, Util.DynamicValue> objectReplaceMap) {
        VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        VRenderSystem.toggleBlend(true);

        // Set texture
        if (c.texture != null)
            VRenderSystem.setShaderTexture(0, c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            VRenderSystem.setupNoFog();

        else
            VRenderSystem.levelFogColor();

        if (c.celestialObjectProperties.isSolid)
            VRenderSystem.defaultBlendFunc();

        if (c.type.equals(CelestialObject.CelestialObjectType.DEFAULT)) {
            VRenderSystem.setShaderPositionTex();
            VRenderSystem.setShaderColor((float) color.x, (float) color.y, (float) color.z, alpha);

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
            VRenderSystem.setShaderPositionColor();
            VRenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            VRenderSystem.toggleTexture(false);

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

            VRenderSystem.toggleTexture(true);
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
                    VRenderSystem.setShaderTexture(0, side.texture);
                    VRenderSystem.setShaderPositionTex();
                } else {
                    VRenderSystem.setShaderPositionColor();
                }
                if (l == 0) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }
                if (l == 1) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, 90);
                }

                if (l == 2) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, -90);
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }

                if (l == 3) {
                    matrices.mulPose(PoseStackWrapper.Axis.X, 180);
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 180);
                }

                if (l == 4) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, 90);
                    matrices.mulPose(PoseStackWrapper.Axis.Z, -90);
                }

                if (l == 5) {
                    matrices.mulPose(PoseStackWrapper.Axis.Y, -90);
                    matrices.mulPose(PoseStackWrapper.Axis.Z, 90);
                }

                size = c.skyBoxProperties.skyBoxSize.invoke().floatValue();

                Matrix4fWrapper matrix4f3 = matrices.lastPose();

                if (c.solidColor != null) {
                    VRenderSystem.toggleTexture(false);
                    bufferBuilder.beginColorObject();

                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? size : -size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? -size : size), (float) (colorsSolid.x / 255.0F), (float) (colorsSolid.y / 255.0F), (float) (colorsSolid.z / 255.0F), alpha);
                    bufferBuilder.upload();

                    VRenderSystem.toggleTexture(true);
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
            VRenderSystem.blendFuncSeparate();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }*/
}
