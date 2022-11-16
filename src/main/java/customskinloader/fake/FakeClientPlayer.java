package customskinloader.fake;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.MinecraftUtil;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.texture.PlayerSkinProvider.SkinTextureAvailableCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.ChatUtil;

public class FakeClientPlayer {
    //For Legacy Skin
    public static PlayerSkinTexture getDownloadImageSkin(Identifier resourceLocationIn, String username) {
        //CustomSkinLoader.logger.debug("FakeClientPlayer/getDownloadImageSkin "+username);
        TextureManager textman = MinecraftUtil.getTextureManager();
        Texture ito = textman.getTexture(resourceLocationIn);

        if (ito == null || !(ito instanceof PlayerSkinTexture)) {
            //if Legacy Skin for username not loaded yet
            PlayerSkinProvider skinman = MinecraftUtil.getSkinManager();
            UUID offlineUUID = getOfflineUUID(username);
            GameProfile offlineProfile = new GameProfile(offlineUUID, username);

            //Load Default Skin
            Identifier defaultSkin = DefaultSkinHelper.getTexture(offlineUUID);
            Texture defaultSkinObj = new ResourceTexture(defaultSkin);
            textman.loadTexture(resourceLocationIn, defaultSkinObj);

            //Load Skin from SkinManager
            skinman.loadSkin(offlineProfile, (PlayerSkinProvider.SkinTextureAvailableCallback) new LegacyBuffer(resourceLocationIn), false);
        }

        if (ito instanceof PlayerSkinTexture)
            return (PlayerSkinTexture) ito;
        else
            return null;
    }

    public static Identifier getLocationSkin(String username) {
        //CustomSkinLoader.logger.debug("FakeClientPlayer/getLocationSkin "+username);
        return new Identifier("skins/legacy-" + ChatUtil.stripTextFormat(username));
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }

    public static Map<Identifier, Texture> textureCache = Maps.newHashMap();

    public static class LegacyBuffer implements SkinTextureAvailableCallback {
        Identifier resourceLocationIn;
        boolean loaded = false;

        public LegacyBuffer(Identifier resourceLocationIn) {
            CustomSkinLoader.logger.debug("Loading Legacy Texture (" + resourceLocationIn + ")");
            this.resourceLocationIn = resourceLocationIn;
        }

        @Override
        public void method_7047(Type typeIn, Identifier location, MinecraftProfileTexture profileTexture) {
            if (typeIn != Type.SKIN || loaded)
                return;

            TextureManager textman = MinecraftUtil.getTextureManager();
            Texture ito = textman.getTexture(location);
            if (ito == null)
                ito = textureCache.get(location);
            if (ito == null)
                return;

            loaded = true;
            textman.loadTexture(resourceLocationIn, ito);
            CustomSkinLoader.logger.debug("Legacy Texture (" + resourceLocationIn + ") Loaded as " +
                    ito.toString() + " (" + location + ")");
        }
    }
}
