package me.aleksilassila.litematica.printer.v1_21_4.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
    @Accessor("invertYMouse")
    SimpleOption<Boolean> getInvertYMouse();
}
