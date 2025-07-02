package net.mat0u5.morph.mixin.client;

import net.mat0u5.morph.interfaces.IEntityRenderer;
import net.mat0u5.morph.interfaces.IMorph;
import net.mat0u5.morph.morph.MorphComponent;
import net.mat0u5.morph.morph.MorphManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntityRenderer.class, priority = 1)
public abstract class LivingEntityRendererMixin {

    @Inject(method = "getShadowRadius(Lnet/minecraft/entity/LivingEntity;)F", at = @At("HEAD"), cancellable = true)
    private <T extends LivingEntity> void getShadowRadius(T livingEntity, CallbackInfoReturnable<Float> cir) {
        if (((IMorph) livingEntity).isFromMorph()) {
            cir.setReturnValue(0.0F);
            return;
        }
        if (livingEntity instanceof PlayerEntity player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null && morphComponent.isMorphed()) {
                LivingEntity dummy = morphComponent.getDummy();
                if (dummy != null) {
                    EntityRenderer<?> renderer = MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(dummy);
                    float morphShadowRadius = ((IEntityRenderer) renderer).morph$getShadowRadius();
                    cir.setReturnValue(morphShadowRadius);
                }
            }
        }
    }
}
