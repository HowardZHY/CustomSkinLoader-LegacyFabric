package customskinloader.mixin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.class_1890;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.texture.PlayerSkinProvider.*;

@Mixin(PlayerSkinProvider.class)
public class MixinSkinManager {

    @Shadow
    @Final
    private TextureManager field_8116;

    @Shadow
    @Final
    private MinecraftSessionService sessionService;

    @Shadow
    @Final
    private File skinCacheDir;

    @Shadow
    @Final
    private LoadingCache skinCache;

    @Final
    HashMap var1 = Maps.newHashMap();

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void inject_init(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService, CallbackInfo ci) {
        customskinloader.loader.MojangAPILoader.defaultSessionService=sessionService;
        customskinloader.utils.HttpTextureUtil.defaultCacheDir=skinCacheDir;
    }

    /**
     * @author HowardZHY
     * @reason too much changes
     */
    @Overwrite
    public Identifier method_7045(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type, class_1890 arg) {
        customskinloader.utils.HttpTextureUtil.HttpTextureInfo info = customskinloader.utils.HttpTextureUtil.toHttpTextureInfo(minecraftProfileTexture.getUrl());
        final Identifier var4 = new Identifier("skins/" + info.hash); //Modified
        Texture var5 = this.field_8116.getTexture(var4);
        if (var5 != null) {
            if (arg != null) {
                arg.method_7047(type, var4);
            }
        } else {
            final BufferedImageSkinProvider var8 = type == MinecraftProfileTexture.Type.SKIN ? new customskinloader.renderer.SkinBuffer() : null; //Modified
            PlayerSkinTexture var9 = new PlayerSkinTexture(info.cacheFile, info.url, field_8114, new BufferedImageSkinProvider() //Modified
            {
                public BufferedImage parseSkin(BufferedImage image)
                {
                    if (var8 != null)
                    {
                        image = var8.parseSkin(image);
                    }

                    return image;
                }
                public void setAvailable()
                {
                    if (var8 != null)
                    {
                        var8.setAvailable();
                    }

                    if (arg != null) {
                        arg.method_7047(type, var4);
                    }
                }
            });
            this.field_8116.loadTexture(var4, var9);
        }
        return var4;
    }

    @Inject(
            method = "method_7043",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/class_1888;<init>(Lnet/minecraft/client/texture/PlayerSkinProvider;Lcom/mojang/authlib/GameProfile;ZLnet/minecraft/client/class_1890;)V"
            )
    )
    public void method_7043(GameProfile profile, class_1890 arg, boolean bl, CallbackInfo ci){
        if(customskinloader.CustomSkinLoader.config.enable){
            var1.putAll(customskinloader.CustomSkinLoader.loadProfile(profile));
        }else{
            try{
                var1.putAll(this.sessionService.getTextures(profile, bl));}
            catch (InsecureTextureException var3){}
        }
    }

    /**
     * @author HowardZHY
     * @reason _
     */
    @Overwrite
    public Map getTextures(GameProfile profile) {
        return (customskinloader.CustomSkinLoader.config.enable && customskinloader.CustomSkinLoader.config.enableSkull) ?
            customskinloader.CustomSkinLoader.loadProfileFromCache(profile) :
            (Map) this.skinCache.getUnchecked(profile);
    }
}
