package net.mat0u5.morph.mixin.client;

import net.mat0u5.morph.morph.MorphComponent;
import net.mat0u5.morph.morph.MorphManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if <= 1.21 {
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
@Mixin(value = PlayerEntityRenderer.class, priority = 1)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    public void replaceRendering(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (abstractClientPlayerEntity.isSpectator() || abstractClientPlayerEntity.isInvisible()) return;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, 0, 0, 0, f, g, matrixStack, vertexConsumerProvider, i);
                ci.cancel();
            }
        }
    }

}
//?} else {
/*import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
@Mixin(value = EntityRenderDispatcher.class, priority = 1)
public class PlayerEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void render(Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            if (player.isSpectator() || player.isInvisible()) return;
            MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
            if(morphComponent.isMorphed()) {
                LivingEntity dummy = morphComponent.getDummy();
                if(morphComponent.isMorphed() && dummy != null){
                    MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                            dummy, x, y, z, tickDelta, matrices, vertexConsumers, light);
                    ci.cancel();
                }
            }
        }
    }
}
*///?}