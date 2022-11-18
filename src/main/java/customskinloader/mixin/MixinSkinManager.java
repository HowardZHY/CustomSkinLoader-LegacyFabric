package customskinloader.mixin;

import java.io.File;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import customskinloader.fake.FakeSkinManager;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSkinProvider.class)
public abstract class MixinSkinManager {
    private FakeSkinManager fakeManager;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void inject_init(
            TextureManager textureManagerInstance,
            File skinCacheDirectory,
            MinecraftSessionService sessionService,
            CallbackInfo callbackInfo) {
        this.fakeManager = new FakeSkinManager(textureManagerInstance, skinCacheDirectory, sessionService);
    }

    @Inject(
        method = "loadSkin(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;)Lnet/minecraft/util/Identifier;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_loadSkin(
            MinecraftProfileTexture profileTexture,
            MinecraftProfileTexture.Type textureType,
            PlayerSkinProvider.SkinTextureAvailableCallback skinAvailableCallback,
            CallbackInfoReturnable<Identifier> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.fakeManager.loadSkin(profileTexture, textureType, skinAvailableCallback));
    }

    @Inject(
        method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_loadProfileTextures(
            GameProfile profile,
            PlayerSkinProvider.SkinTextureAvailableCallback skinAvailableCallback,
            boolean requireSecure,
            CallbackInfo callbackInfo) {
        this.fakeManager.loadProfileTextures(profile, skinAvailableCallback, requireSecure);
        callbackInfo.cancel();
    }

    @Inject(
        method = "getTextures",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_loadSkinFromCache(GameProfile profile, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.fakeManager.loadSkinFromCache(profile));
    }
}
