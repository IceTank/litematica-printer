package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("prevYaw")
    float getPrevYaw();

    @Accessor("prevYaw")
    void setPrevYaw(float prevYaw);

    @Accessor("prevPitch")
    float getPrevPitch();

    @Accessor("prevPitch")
    void setPrevPitch(float prevPitch);
}
