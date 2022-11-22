package customskinloader.mixin;

import java.util.Map;

import com.google.common.collect.Maps;

import customskinloader.imixins.IMixinAbstractClientPlayer;
import customskinloader.imixins.IMixinRenderPlayer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinRenderManager {
    private final Map<String, PlayerEntityRenderer> /* skinMap */ field_178636_l = Maps.newHashMap();
    private PlayerEntityRenderer /* renderPlayer */ field_178637_m;

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void inject$_init_$0(CallbackInfo ci) {
        this.field_178637_m = new PlayerEntityRenderer();
        ((IMixinRenderPlayer) this.field_178637_m).setModelOnInit((EntityRenderDispatcher) (Object) this, false);
        this.field_178636_l.put("default", this.field_178637_m);

        PlayerEntityRenderer renderPlayer = new PlayerEntityRenderer();
        ((IMixinRenderPlayer) renderPlayer).setModelOnInit((EntityRenderDispatcher) (Object) this, true);
        this.field_178636_l.put("slim", renderPlayer);
    }

    @Inject(
        method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject$getEntityRenderObject$0(Entity entityIn, CallbackInfoReturnable<EntityRenderer> cir) {
        if (entityIn instanceof AbstractClientPlayerEntity) {
            String skinType = ((IMixinAbstractClientPlayer) entityIn).getModel();
            if (skinType != null) {
                PlayerEntityRenderer renderPlayer = this.field_178636_l.get(skinType);
                cir.setReturnValue(renderPlayer != null ? renderPlayer : this.field_178637_m);
            }
        }
    }
}