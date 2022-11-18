package customskinloader.mixin;

import customskinloader.fake.itf.IFakeIResource;
import net.minecraft.resource.Resource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Resource.class)
public interface MixinIResource extends IFakeIResource.V1, IFakeIResource.V2 {

}
