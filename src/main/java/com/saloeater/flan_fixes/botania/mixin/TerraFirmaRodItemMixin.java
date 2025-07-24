package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.item.rod.TerraFirmaRodItem;

import java.util.List;

@Mixin(value = TerraFirmaRodItem.class, remap = false)
public abstract class TerraFirmaRodItemMixin {
    @Inject(
            method = "terraform",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$terraform(ItemStack stack, Level world, Player player, CallbackInfo ci, int range, BlockPos startCenter, List<BlockPos> blocks) {
        blocks.removeIf(pos -> !(player instanceof ServerPlayer serverPlayer) || !ManaBurstEntityHelper.evaluateCanHitByPlayer(world, pos, serverPlayer));
    }
}
