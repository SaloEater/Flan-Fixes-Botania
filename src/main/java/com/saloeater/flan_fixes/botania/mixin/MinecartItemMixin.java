package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = MinecartItem.class)
public abstract class MinecartItemMixin {
    @Inject(
        method="useOn",
        at= @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void flan_fixes_vanilla$useOn(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir, Level level, BlockPos blockpos, BlockState blockstate, ItemStack itemstack, RailShape railshape, double d0, AbstractMinecart abstractminecart) {
        if (abstractminecart instanceof IOwnedByPlayer ownedByPlayer && ctx.getPlayer() != null) {
            IOwnedByPlayerHelper.setOwnerID(ownedByPlayer, ctx.getPlayer().getUUID());
        }
    }
}
