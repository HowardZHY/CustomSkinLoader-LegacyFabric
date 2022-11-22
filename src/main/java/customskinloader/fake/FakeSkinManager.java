package customskinloader.fake;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import customskinloader.CustomSkinLoader;
import customskinloader.fake.itf.FakeInterfaceManager;
import customskinloader.utils.HttpTextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_1890;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class FakeSkinManager {
    private final TextureManager textureManager;

    private final Map<Identifier, MinecraftProfileTexture> modelCache = new ConcurrentHashMap<>();

    public FakeSkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        HttpTextureUtil.defaultCacheDir = skinCacheDirectory;
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType) {
        return this.method_7043(profileTexture, textureType, null);
    }

    public Identifier method_7043(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type textureType, final class_1890 skinAvailableCallback) {
        return this.method_7045(profileTexture, HttpTextureUtil.toHttpTextureInfo(profileTexture.getUrl()), textureType, skinAvailableCallback);
    }

    private Identifier method_7045(final MinecraftProfileTexture profileTexture, final HttpTextureUtil.HttpTextureInfo info, final MinecraftProfileTexture.Type textureType, final class_1890 skinAvailableCallback) {
        final Identifier resourcelocation = new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(info.hash).toString());

        if (FakeInterfaceManager.TextureManager_getTexture(this.textureManager, resourcelocation, null) != null) {//Have already loaded
            makeCallback(skinAvailableCallback, textureType, resourcelocation, modelCache.getOrDefault(resourcelocation, profileTexture));
        } else {
            //working?
            ResourceTexture threaddownloadimagedata = FakeThreadDownloadImageData.createThreadDownloadImageData(
                    info.cacheFile,
                    info.url,
                   null,
                    new FakeSkinManager.BaseBuffer(skinAvailableCallback, textureType, resourcelocation, profileTexture), null);
            if (skinAvailableCallback instanceof FakeClientPlayer.LegacyBuffer)//Cache for client player
                FakeClientPlayer.textureCache.put(resourcelocation, threaddownloadimagedata);
            FakeInterfaceManager.TextureManager_loadTexture(this.textureManager, resourcelocation, threaddownloadimagedata);
        }
        return resourcelocation;
    }

    public void loadProfileTextures(final GameProfile profile, final class_1890 skinAvailableCallback, final boolean requireSecure) {
        CustomSkinLoader.loadProfileTextures(() -> CustomSkinLoader.loadProfileLazily(profile, m -> {
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            map.putAll(m);

            for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
                MinecraftProfileTexture profileTexture = map.get(type);
                if (profileTexture != null) {
                    HttpTextureUtil.HttpTextureInfo info = HttpTextureUtil.toHttpTextureInfo(profileTexture.getUrl());
                    FakeThreadDownloadImageData.downloadTexture(info.cacheFile, info.url);

                    FakeInterfaceManager.Minecraft_addScheduledTask(MinecraftClient.getInstance(), () -> {
                        CustomSkinLoader.logger.debug("Loading type: " + type);
                        try {
                            this.method_7045(profileTexture, info, type, skinAvailableCallback);
                        } catch (Throwable t) {
                            CustomSkinLoader.logger.warning(t);
                        }
                    });
                }
            }
        }));
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = CustomSkinLoader.loadProfileFromCache(profile);
        for (Iterator<Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry = it.next();
            MinecraftProfileTexture texture = entry.getValue();
            if (shouldJudgeType(texture)) {
                texture = this.modelCache.get(this.loadSkin(texture, entry.getKey()));
                if (texture == null) { // remove texture if was not loaded before
                    it.remove();
                } else {
                    map.put(entry.getKey(), texture);
                }
            }
        }
        return map;
    }

    private static void makeCallback(class_1890 callback, MinecraftProfileTexture.Type type, Identifier location, MinecraftProfileTexture texture) {
        if (callback != null)
            callback.method_7047(type, location);
    }

    private static boolean shouldJudgeType(MinecraftProfileTexture texture) {
        return texture != null && "auto".equals(texture.getMetadata("model"));
    }

    private class BaseBuffer implements BufferedImageSkinProvider{
        private BufferedImageSkinProvider buffer;
        private class_1890 callback;
        private MinecraftProfileTexture.Type type;
        private Identifier location;
        private MinecraftProfileTexture texture;

        public BaseBuffer(class_1890 callback, MinecraftProfileTexture.Type type, Identifier location, MinecraftProfileTexture texture) {
            switch (type) {
                case SKIN: this.buffer = new FakeSkinBuffer(); break;
                case CAPE: this.buffer = new FakeCapeBuffer(location); break;
            }

            this.callback = callback;
            this.type = type;
            this.location = location;
            this.texture = texture;
        }


        public BufferedImage parseSkin(BufferedImage image) {
            return buffer instanceof FakeSkinBuffer ? ((FakeSkinBuffer) buffer).parseSkin(image) : image;
        }

        @Override
        public void setAvailable() {
            if (buffer != null) {
                buffer.setAvailable();
                if (shouldJudgeType(texture) && buffer instanceof FakeSkinBuffer) {
                    //Auto judge skin type
                    Map<String, String> metadata = Maps.newHashMap();
                    String type = ((FakeSkinBuffer) buffer).judgeType();
                    metadata.put("model", type);
                    texture = new MinecraftProfileTexture(texture.getUrl(), metadata);
                    FakeSkinManager.this.modelCache.put(location, texture);
                }
            }

            FakeSkinManager.makeCallback(callback, type, location, this.texture);
        }
    }
}
