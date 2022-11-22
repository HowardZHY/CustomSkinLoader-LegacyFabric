package customskinloader.imixins;

import net.minecraft.client.render.entity.EntityRenderDispatcher;

public interface IMixinRenderPlayer {
    void setModelOnInit(EntityRenderDispatcher renderManager, boolean useSmallArms);
}