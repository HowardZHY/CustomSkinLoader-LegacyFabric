package customskinloader.mixin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import customskinloader.fake.FakeSkinManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkinManager.class)
public abstract class MixinSkinManager {
    public FakeSkinManager fakeManager;

    @Inject(
        method = "<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V",
        at = @At("RETURN")
    )
    private void inject_init(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService, CallbackInfo callbackInfo) {
        this.fakeManager = new FakeSkinManager(textureManagerInstance, skinCacheDirectory, sessionService);
    }

    @ModifyVariable(
        method = "loadSkin(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;",
        at = @At(
            value = "STORE",
            ordinal = 0
        ),
        ordinal = 0
    )
    private ResourceLocation modifyVariable_loadSkin(ResourceLocation resourceLocation, MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        return FakeSkinManager.setResourceLocation(resourceLocation, profileTexture);
    }

    @ModifyArg(
        method = "loadSkin(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;skinAvailable(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;)V"
        )
    )
    private MinecraftProfileTexture modifyArg_loadSkin(MinecraftProfileTexture.Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
        return FakeSkinManager.getModelCache(profileTexture, this.fakeManager, location);
    }

    // 19w37a-
    @Group(
        name = "modifyArgs_loadSkin",
        min = 1
    )
    @ModifyArgs(
        method = "loadSkin(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ThreadDownloadImageData;<init>(Ljava/io/File;Ljava/lang/String;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/IImageBuffer;)V"
        )
    )
    private void modifyArgs_loadSkin_0(Args args, MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        Object[] argsArr = new Object[args.size()];
        for (int i = 0; i < argsArr.length; i++) {
            argsArr[i] = args.get(i);
        }
        argsArr = FakeSkinManager.createThreadDownloadImageData(ImmutableList.copyOf(argsArr), this.fakeManager, profileTexture, textureType, skinAvailableCallback);
        args.setAll(argsArr);
    }

    // 19w37a-
    @Group(
        name = "redirect_loadProfileTextures",
        min = 1
    )
    @Redirect(
        method = "loadProfileTextures(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/concurrent/ExecutorService;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;",
            remap = false
        )
    )
    private Future<?> redirect_loadProfileTextures_0(ExecutorService executor, Runnable task, GameProfile profile, SkinManager.SkinAvailableCallback skinAvailableCallback, boolean requireSecure) {
        FakeSkinManager.loadProfileTextures(task, profile);
        return null;
    }

    @Inject(
        method = "loadSkinFromCache(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_loadSkinFromCache(GameProfile profile, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.fakeManager.loadSkinFromCache(profile));
    }
}
