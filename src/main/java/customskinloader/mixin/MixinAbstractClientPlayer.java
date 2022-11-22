package customskinloader.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.imixins.IMixinAbstractClientPlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayer implements IMixinAbstractClientPlayer {
    @Shadow
    private Identifier skinId;

    @Shadow
    private Identifier capeId;

    private String skinType;

    @Override
    public String getModel() {
        return this.skinType;
    }

    public void /* skinAvailable */ func_180521_a(MinecraftProfileTexture.Type typeIn, Identifier location, MinecraftProfileTexture profileTexture) {
        switch (typeIn) {
            case SKIN: {
                this.skinId = location;
                this.skinType = profileTexture.getMetadata("model");
                if (this.skinType == null) {
                    this.skinType = "default";
                }
                break;
            }
            case CAPE: {
                this.capeId = location;
                break;
            }
        }
    }
}