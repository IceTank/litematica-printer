package me.aleksilassila.litematica.printer.v1_21_4.mixin;/*
 * @author IceTank
 * @since 17.03.2025
 */

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractBlock.class)
public interface AbstractBlockInvoker {
    @Invoker("getPickStack")
    ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData);
}
