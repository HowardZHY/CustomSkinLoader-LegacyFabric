package customskinloader.mixin;

import customskinloader.fake.FakeClientPlayer;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TeleportToSpecificPlayerSpectatorCommand.class)
public abstract class MixinPlayerMenuObject {
    @Redirect(
        method = "<init>(Lcom/mojang/authlib/GameProfile;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinId(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"
        )
    )
    private Identifier redirect_init(String username) {
        return FakeClientPlayer.getLocationSkin(username);
    }

    @Redirect(
        method = "<init>(Lcom/mojang/authlib/GameProfile;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;loadSkin(Lnet/minecraft/util/Identifier;Ljava/lang/String;)Lnet/minecraft/client/texture/PlayerSkinTexture;"
        )
    )
    private PlayerSkinTexture redirect_init(Identifier resourceLocationIn, String username) {
        return FakeClientPlayer.getDownloadImageSkin(resourceLocationIn, username);
    }
}
