package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface MixinAccessorClientPlayerEntity {
    @Accessor("ticksLeftToDoubleTapSprint")
    void setTicksLeftToDoubleTapSprint(int ticksLeftToDoubleTapSprint);
}
