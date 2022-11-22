package customskinloader.mixin;

import customskinloader.fake.itf.IFakeIResourceManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResourceManager.class)
public interface MixinIResourceManager extends IFakeIResourceManager {
}
