package customskinloader.mixin;

import java.io.File;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import customskinloader.fake.FakeSkinManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager$3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// For 1.12.2-
@Mixin(targets = "net.minecraft.client.resources.SkinManager$3")
public abstract class MixinSkinManager$3 extends SkinManager implements Runnable{
    public MixinSkinManager$3(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionService) {
        super(textureManagerInstance, skinCacheDirectory, sessionService);
    }

    @Redirect(
            method = "Lnet/minecraft/client/resources/SkinManager$3;run()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;getTextures(Lcom/mojang/authlib/GameProfile;Z)Ljava/util/Map;",
                    ordinal = 0
            )
    )
    private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> redirect_run(MinecraftSessionService sessionService, GameProfile profile, boolean requireSecure) {
        return FakeSkinManager.getUserProfile(sessionService, profile, requireSecure);
    }
}
