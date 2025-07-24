package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IOwnedByPlayer;
import com.saloeater.flan_fixes.botania.IOwnedByPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.entity.FlameRingEntity;
import vazkii.botania.common.item.rod.HellsRodItem;

@Mixin(value =  HellsRodItem.class, remap = false)
public class HellsRodItemMixin {
    @Inject(
        method = "m_6225_", //useOn
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;m_7967_(Lnet/minecraft/world/entity/Entity;)Z"), //Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void useOn(UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir, Level world, Player player, ItemStack stack, BlockPos pos, FlameRingEntity entity) {
        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer && entity instanceof IOwnedByPlayer owned) {
            IOwnedByPlayerHelper.setOwnerID(owned, serverPlayer.getUUID());
        }
    }
}
