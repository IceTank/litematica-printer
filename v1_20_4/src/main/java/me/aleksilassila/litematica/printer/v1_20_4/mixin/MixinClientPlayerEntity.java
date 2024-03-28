package me.aleksilassila.litematica.printer.v1_20_4.mixin;

import me.aleksilassila.litematica.printer.v1_20_4.config.PrinterConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        MixinClientPlayerEntity clientPlayer = this;
        if (PrinterConfig.PREVENT_DOUBLE_TAP_SPRINTING.getBooleanValue()) {
            ((MixinAccessorClientPlayerEntity) clientPlayer).setTicksLeftToDoubleTapSprint(0);
        }
    }
}
