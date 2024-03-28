package me.aleksilassila.litematica.printer.v1_20_4.mixin;

import me.aleksilassila.litematica.printer.v1_20_4.FreeLook;
import me.aleksilassila.litematica.printer.v1_20_4.LitematicaMixinMod;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class MixinCamera {
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        FreeLook freeLook = LitematicaMixinMod.freeLook;

        if (freeLook.isEnabled()) {
            args.set(0, freeLook.getCameraYaw());
            args.set(1, freeLook.getCameraPitch());
        }
    }
}
