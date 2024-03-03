package fishcute.celestialmain.sky;

import com.google.gson.JsonObject;
import fishcute.celestialmain.api.minecraft.wrappers.IResourceLocationWrapper;
import fishcute.celestialmain.util.CelestialExpression;
import fishcute.celestialmain.util.Util;
import fishcute.celestialmain.version.independent.Instances;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SkyBoxObjectProperties {
    public ArrayList<SkyBoxSideTexture> sides;
    public CelestialExpression skyBoxSize;

    public CelestialExpression textureSizeX;
    public CelestialExpression textureSizeY;

    public SkyBoxObjectProperties(ArrayList<SkyBoxSideTexture> sides, String dimension, String object, String skyBoxSize, String textureSizeX, String textureSizeY) {
        this.sides = sides;
        this.skyBoxSize = Util.compileExpression(skyBoxSize, Util.locationFormat(dimension, "object/" + object, "skybox.size"));
        this.textureSizeX = Util.compileExpression(textureSizeX, Util.locationFormat(dimension, "object/" + object, "skybox.texture_width"));
        this.textureSizeY = Util.compileExpression(textureSizeY, Util.locationFormat(dimension, "object/" + object, "skybox.texture_height"));
    }

    public static SkyBoxObjectProperties getSkyboxPropertiesFromJson(JsonObject o, String dimension, String object) {
        String texture;

        texture = Util.getOptionalString(o, "texture", "", Util.locationFormat(dimension, object));

        int textureWidth = 0;
        int textureHeight = 0;
        try {
            BufferedImage b = ImageIO.read(Instances.minecraft.getResource(texture));
            textureWidth = b.getWidth();
            textureHeight = b.getHeight();
        }
        catch (Exception ignored) {}

        if (!o.has("skybox")) {
            // Returns if there is no skybox entry
            return new SkyBoxObjectProperties(createDefaultSkybox(
                    Instances.resourceLocationFactory.build(texture), (textureHeight / 2) + "",
                    Util.locationFormat(dimension, "objects/" + object, "")
            ),
                    dimension, object,
                    Util.getOptionalString(o, "size", "100", Util.locationFormat(dimension, object)),
                    Util.getOptionalString(o, "texture_width", textureWidth + "", Util.locationFormat(dimension, object)),
                    Util.getOptionalString(o, "texture_height", textureHeight + "", Util.locationFormat(dimension, object)));
        }

        JsonObject skybox = o.get("skybox").getAsJsonObject();

        if (skybox.has("sides")) {
            ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (skybox.get("sides").getAsJsonObject().has("all")) {
                    String location = Util.locationFormat(dimension, "objects/" + object, "sides.all");
                    return new SkyBoxObjectProperties(
                            createSingleTextureSkybox(
                                    Instances.resourceLocationFactory.build(Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "texture", texture, location)),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_x", "0", location),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_y", "0", location),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_width", "0", location),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_height", "0", location),
                                    Util.locationFormat(dimension, "objects/" + object, "")),
                            dimension, object,
                            Util.getOptionalString(skybox, "size", "100", Util.locationFormat(dimension, object, "skybox")),
                            Util.getOptionalString(skybox, "texture_width", textureWidth + "", Util.locationFormat(dimension, object, "skybox")),
                            Util.getOptionalString(skybox, "texture_height", textureHeight + "", Util.locationFormat(dimension, object, "skybox"))
                    );
                }
                else if (!skybox.get("sides").getAsJsonObject().has(String.valueOf(i))) {
                    textures.add(new SkyBoxSideTexture(
                            Instances.resourceLocationFactory.build(texture), "-1", "-1", "-1", "-1",
                            Util.locationFormat(dimension, "objects/" + object, "")
                    ));
                }
                else {
                    JsonObject entry = skybox.get("sides").getAsJsonObject().getAsJsonObject(String.valueOf(i));
                    textures.add(new SkyBoxSideTexture(
                            Instances.resourceLocationFactory.build(Util.getOptionalString(entry, "texture", texture, Util.locationFormat(dimension, object, "skybox.sides." + i))),
                            Util.getOptionalString(entry, "uv_x", "0", Util.locationFormat(dimension, object, "skybox.sides." + i)),
                            Util.getOptionalString(entry, "uv_y", "0", Util.locationFormat(dimension, object, "skybox.sides." + i)),
                            Util.getOptionalString(entry, "uv_width", "0", Util.locationFormat(dimension, object, "skybox.sides." + i)),
                            Util.getOptionalString(entry, "uv_height", "0", Util.locationFormat(dimension, object, "skybox.sides." + i)),
                            Util.locationFormat(dimension, "objects/" + object, "")
                    ));
                }
            }
            // Returns skybox with custom format
            return new SkyBoxObjectProperties(
                    textures,
                    dimension, object,
                    Util.getOptionalString(skybox, "size", "100", Util.locationFormat(dimension, object, "skybox")),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + "", Util.locationFormat(dimension, object, "skybox")),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "", Util.locationFormat(dimension, object, "skybox"))
            );
        }
        else {
            // Returns default format skybox
            return new SkyBoxObjectProperties(
                    createDefaultSkybox(
                            Instances.resourceLocationFactory.build(texture), Util.getOptionalString(skybox, "uv_size", (textureHeight / 2) + "", Util.locationFormat(dimension, object, "skybox")),
                            Util.locationFormat(dimension, "objects/" + object, "")
                    ),
                    dimension, object,
                    Util.getOptionalString(skybox, "size", "100", Util.locationFormat(dimension, object, "skybox")),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + "", Util.locationFormat(dimension, object, "skybox")),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "", Util.locationFormat(dimension, object, "skybox"))
            );
        }
    }

    public static ArrayList<SkyBoxSideTexture> createDefaultSkybox(IResourceLocationWrapper texture, String textureSize, String location) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();

        // Bottom
        // #Green
        textures.add(new SkyBoxSideTexture(texture, textureSize, "0", textureSize, textureSize, location));

        // North
        // #Yellow
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", "0", textureSize, textureSize, location));

        // South
        // #Light Blue
        textures.add(new SkyBoxSideTexture(texture, textureSize, textureSize, textureSize, textureSize, location));


        // Up
        // #Red
        textures.add(new SkyBoxSideTexture(texture, "0", "0", textureSize, textureSize, location));

        // East
        // #Blue
        textures.add(new SkyBoxSideTexture(texture, "0", textureSize, textureSize, textureSize, location));

        // West
        // #Purple
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", textureSize, textureSize, textureSize, location));

        return textures;
    }

    public static ArrayList<SkyBoxSideTexture> createSingleTextureSkybox(IResourceLocationWrapper texture, String uvX, String uvY, String uvSizeX, String uvSizeY, String location) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            textures.add(new SkyBoxSideTexture(texture, uvX, uvY, uvSizeX, uvSizeY, location));
        }

        return textures;
    }

    public static class SkyBoxSideTexture {
        public IResourceLocationWrapper texture;
        public CelestialExpression uvX;
        public CelestialExpression uvY;
        public CelestialExpression uvSizeX;
        public CelestialExpression uvSizeY;

        public SkyBoxSideTexture(IResourceLocationWrapper texture, String uvX, String uvY, String uvSizeX, String uvSizeY, String location) {
            this.texture = texture;
            this.uvX = Util.compileExpression(uvX, location + ".uv_x");
            this.uvY = Util.compileExpression(uvY, location + ".uv_y");
            this.uvSizeX = Util.compileExpression(uvSizeX, location + ".uv_width");
            this.uvSizeY = Util.compileExpression(uvSizeY, location + ".uv_height");
        }
    }
}