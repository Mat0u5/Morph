package net.mat0u5.morph.mixin.client;

import net.mat0u5.morph.morph.MorphComponent;
import net.mat0u5.morph.morph.MorphManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientWorld.class, priority = 1)
public class ClientWorldMixin {

    @Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void tick(Entity entity, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                morphComponent.clientTick();
            }
        }
    }

    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickRiding()V", shift = At.Shift.AFTER))
    private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
        if (passenger instanceof PlayerEntity player) {
            MorphComponent morphComponent = MorphManager.getComponent(player);
            if (morphComponent != null) {
                morphComponent.clientTick();
            }
        }
    }
}
