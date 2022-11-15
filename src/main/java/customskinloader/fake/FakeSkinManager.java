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
import net.minecraft.client.texture.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

public class FakeSkinManager {
    private final TextureManager textureManager;

    private final Map<Identifier, MinecraftProfileTexture> modelCache = new ConcurrentHashMap<>();

    public FakeSkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        HttpTextureUtil.defaultCacheDir = skinCacheDirectory;
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType) {
        return this.loadSkin(profileTexture, textureType, null);
    }

    public Identifier loadSkin(final MinecraftProfileTexture profileTexture, final MinecraftProfileTexture.Type textureType, final PlayerSkinProvider.SkinTextureAvailableCallback skinAvailableCallback) {
        return this.loadSkin(profileTexture, HttpTextureUtil.toHttpTextureInfo(profileTexture.getUrl()), textureType, skinAvailableCallback);
    }

    private Identifier loadSkin(final MinecraftProfileTexture profileTexture, final HttpTextureUtil.HttpTextureInfo info, final MinecraftProfileTexture.Type textureType, final PlayerSkinProvider.SkinTextureAvailableCallback skinAvailableCallback) {
        final Identifier resourcelocation = new Identifier("skins/" + Hashing.sha1().hashUnencodedChars(info.hash).toString());

        if (FakeInterfaceManager.TextureManager_getTexture(this.textureManager, resourcelocation, null) != null) {//Have already loaded
            makeCallback(skinAvailableCallback, textureType, resourcelocation, modelCache.getOrDefault(resourcelocation, profileTexture));
        } else {
            ResourceTexture threaddownloadimagedata = FakeThreadDownloadImageData.createThreadDownloadImageData(
                    info.cacheFile,
                    info.url,
                    DefaultSkinHelper.getTexture(),
                    (net.minecraft.client.render.BufferedImageSkinProvider) new BaseBuffer(skinAvailableCallback, textureType, resourcelocation, profileTexture),
                    textureType);
            if (skinAvailableCallback instanceof FakeClientPlayer.LegacyBuffer)//Cache for client player
                FakeClientPlayer.textureCache.put(resourcelocation, threaddownloadimagedata);
            FakeInterfaceManager.TextureManager_loadTexture(this.textureManager, resourcelocation, threaddownloadimagedata);
        }
        return resourcelocation;
    }

    public void loadProfileTextures(final GameProfile profile, final PlayerSkinProvider.SkinTextureAvailableCallback skinAvailableCallback, final boolean requireSecure) {
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
                            this.loadSkin(profileTexture, info, type, skinAvailableCallback);
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

    private static void makeCallback(PlayerSkinProvider.SkinTextureAvailableCallback callback, MinecraftProfileTexture.Type type, Identifier location, MinecraftProfileTexture texture) {
        if (callback != null)
            callback.method_7047(type, location, texture);
    }

    private static boolean shouldJudgeType(MinecraftProfileTexture texture) {
        return texture != null && "auto".equals(texture.getMetadata("model"));
    }

    public class BaseBuffer{
        private BufferedImageSkinProvider buffer;

        private PlayerSkinProvider.SkinTextureAvailableCallback callback;
        private MinecraftProfileTexture.Type type;
        private Identifier location;
        private MinecraftProfileTexture texture;

        public BaseBuffer(PlayerSkinProvider.SkinTextureAvailableCallback callback, MinecraftProfileTexture.Type type, Identifier location, MinecraftProfileTexture texture) {
            switch (type) {
                case SKIN: this.buffer = new FakeSkinBuffer(); break;
                case CAPE: this.buffer = new FakeCapeBuffer(location); break;
            }

            this.callback = callback;
            this.type = type;
            this.location = location;
            this.texture = texture;
        }

        public net.minecraft.client.renderer.texture.NativeImage func_195786_a(net.minecraft.client.renderer.texture.NativeImage image) {
            return buffer instanceof FakeSkinBuffer ? ((FakeSkinBuffer) buffer).func_195786_a(image) : image;
        }

        public BufferedImage parseUserSkin(BufferedImage image) {
            return buffer instanceof FakeSkinBuffer ? ((FakeSkinBuffer) buffer).parseUserSkin(image) : image;
        }

        public void skinAvailable() {
            if (buffer != null) {
                buffer.skinAvailable();
                if (shouldJudgeType(texture) && buffer instanceof FakeSkinBuffer) {
                    //Auto judge skin type
                    Map<String, String> metadata = Maps.newHashMap();
                    String type = ((FakeSkinBuffer) buffer).judgeType();
                    metadata.put("model", type);
                    texture = new MinecraftProfileTexture(texture.getUrl(), metadata);
                    FakeSkinManager.this.modelCache.put(location, texture);
                }
            }

            FakeSkinManager.makeCallback(callback, type, location, texture);
        }
    }
}
