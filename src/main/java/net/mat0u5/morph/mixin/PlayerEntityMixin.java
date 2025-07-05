package net.mat0u5.morph.mixin;

import net.mat0u5.morph.morph.MorphComponent;
import net.mat0u5.morph.morph.MorphManager;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 1)
public abstract class PlayerEntityMixin {

    @Inject(method = "getBaseDimensions", at = @At("HEAD"), cancellable = true)
    public void getBaseDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            float scaleRatio = 1 / player.getScale();
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null){
                cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateHitbox(CallbackInfo ci) {
        ((PlayerEntity) (Object) this).calculateDimensions();
    }
}
