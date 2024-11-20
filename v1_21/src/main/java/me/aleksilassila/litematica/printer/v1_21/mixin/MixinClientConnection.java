package me.aleksilassila.litematica.printer.v1_21.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.aleksilassila.litematica.printer.v1_21.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21.Printer;
import me.aleksilassila.litematica.printer.v1_21.config.PrinterConfig;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void channelReadPre(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback) {
        if (!LitematicaMixinMod.PRINT_MODE.getBooleanValue() && !LitematicaMixinMod.PRINT.getKeybind().isPressed()) {
            return;
        }
        if (!PrinterConfig.PRINTER_AIRPLACE.getBooleanValue() || !PrinterConfig.PRINTER_AIRPLACE_OFFHAND_SLOT_SUPPRESS.getBooleanValue()) {
            return;
        }
        if (Printer.inactivityCounter > 20) {
            return;
        }
        if (packet instanceof ScreenHandlerSlotUpdateS2CPacket packet1) {
            if (packet1.getSyncId() == -2 && packet1.getSlot() == PlayerInventory.OFF_HAND_SLOT) {
                callback.cancel();
            } else if (packet1.getSyncId() == 0 && PlayerScreenHandler.isInHotbar(packet1.getSyncId())) {
                if (packet1.getSlot() == PlayerScreenHandler.OFFHAND_ID) {
                    callback.cancel();
                }
            }
        }
    }
}
