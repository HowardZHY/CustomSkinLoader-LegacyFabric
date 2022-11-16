package customskinloader.mixin;

import customskinloader.fake.itf.IFakeMinecraft;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraft implements IFakeMinecraft {

}
