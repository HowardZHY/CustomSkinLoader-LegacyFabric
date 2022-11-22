package customskinloader.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.class_1890;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(class_1890.class)
public interface MixinSkinManager$SkinAvailableCallback {

    void onSkinAvailable(MinecraftProfileTexture.Type skinPart, Identifier skinLoc);

    default void /* skinAvailable */ func_180521_a(MinecraftProfileTexture.Type typeIn, Identifier location, MinecraftProfileTexture profileTexture) {
        this.onSkinAvailable(typeIn, location);
    }
}