package customskinloader.mixin;

import customskinloader.imixins.IMixinRenderPlayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntityRenderer.class)

public abstract class MixinRenderPlayer extends LivingEntityRenderer implements IMixinRenderPlayer {

    @Shadow
    public BiPedModel field_2133;

    private MixinRenderPlayer(EntityModel modelBaseIn, float shadowSizeIn) {
        super(modelBaseIn, shadowSizeIn);
    }

    @Override
    public void setModelOnInit(EntityRenderDispatcher renderManager, boolean useSmallArms) {
        this.method_1528(renderManager);
        this.field_2133 = new ModelPlayer(0.0F, useSmallArms);
        this.model = this.field_2133;
    }
}