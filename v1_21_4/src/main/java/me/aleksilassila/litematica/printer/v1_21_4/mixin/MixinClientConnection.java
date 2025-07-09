package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.aleksilassila.litematica.printer.v1_21_4.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.v1_21_4.Printer;
import me.aleksilassila.litematica.printer.v1_21_4.config.PrinterConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void channelReadPre(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo callback) {
        if (!LitematicaMixinMod.PRINT_MODE.getBooleanValue() && !LitematicaMixinMod.PRINT.getKeybind().isPressed()) {
            return;
        }
        if (Printer.inactivityCounter > 20) {
            return;
        }
        if (PrinterConfig.PRINTER_AIRPLACE.getBooleanValue() && PrinterConfig.PRINTER_AIRPLACE_OFFHAND_SLOT_SUPPRESS.getBooleanValue()) {
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
        // This prevents the server from sending the client the packets that would normally be sent to the client
        // Just wanted to keep this comment above because copilot wrote it. What does that even mean?
        // This prevents 2b from fucking up your inventory with useless slot update packets. Turns out 2b sends slot update
        // packet AND an entire inventory update packet just because. The client does not like that so you get a shit ton of ghost items.
        if (PrinterConfig.PRINTER_SUPER_CHINESE_GHOST_ITEM_FIX.getBooleanValue()) {
            if (packet instanceof ScreenHandlerSlotUpdateS2CPacket packet1) {
                // Looks like faulty packet is always on syncId = 0
                if (packet1.getSyncId() == 0 && mc.player != null) {
                    if (packet1.getSlot() >= PlayerScreenHandler.HOTBAR_START || packet1.getSlot() < PlayerScreenHandler.HOTBAR_END) {
                        // Only cancel updates to block items. Some blocks might not be in the USABLE_SLOTS list but still get used for placing
                        if (mc.player.playerScreenHandler.getSlot(packet1.getSlot()).getStack().getItem() instanceof BlockItem) {
                            callback.cancel();
                        }
                    }
                }
            }
        }
    }
}
