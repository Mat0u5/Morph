package net.mat0u5.morph.mixin.client;

import net.mat0u5.morph.interfaces.IEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityRenderer.class, priority = 1)
public abstract class EntityRendererMixin implements IEntityRenderer {
    @Shadow
    protected float shadowRadius;

    @Override
    public float morph$getShadowRadius() {
        return shadowRadius;
    }
}
