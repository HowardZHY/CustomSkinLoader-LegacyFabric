package customskinloader.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
@SuppressWarnings("target")
public abstract class MixinGuiPlayerTabOverlay {
    @Redirect(
        method = {
                "render(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;isIntegratedServerRunning()Z"
        )
    )
    private boolean redirect_renderPlayerlist(MinecraftClient mc) {
        return true;
    }
}
