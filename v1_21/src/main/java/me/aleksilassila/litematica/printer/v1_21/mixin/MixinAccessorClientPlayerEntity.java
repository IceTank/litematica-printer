package me.aleksilassila.litematica.printer.v1_21.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface MixinAccessorClientPlayerEntity {
    @Accessor("ticksLeftToDoubleTapSprint")
    void setTicksLeftToDoubleTapSprint(int ticksLeftToDoubleTapSprint);

    @Accessor("lastSneaking")
    boolean getLastSneaking();

    @Accessor("lastSneaking")
    void setLastSneaking(boolean lastSneaking);

    @Accessor("lastPitch")
    float getLastPitch();
    @Accessor("lastPitch")
    void setLastPitch(float lastPitch);

    @Accessor("lastYaw")
    float getLastYaw();
    @Accessor("lastYaw")
    void setLastYaw(float lastYaw);
}
