package net.mat0u5.morph.mixin;

import net.mat0u5.morph.interfaces.IMorph;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Entity.class, priority = 1)
public abstract class EntityMixin implements IMorph {
    private boolean fromMorph = false;

    @Override
    public void setFromMorph(boolean fromMorph) {
        this.fromMorph = fromMorph;
    }

    @Override
    public boolean isFromMorph() {
        return fromMorph;
    }
}
