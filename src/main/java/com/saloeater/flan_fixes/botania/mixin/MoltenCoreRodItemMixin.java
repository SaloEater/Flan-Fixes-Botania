package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.common.item.rod.MoltenCoreRodItem;

@Mixin(MoltenCoreRodItem.class)
public abstract class MoltenCoreRodItemMixin {
    @Inject(
        method = "m_5929_", //onUseTick
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void flan_fixes$onUseTick(Level world, LivingEntity living, ItemStack stack, int time, CallbackInfo ci, Player p, Container dummyInv, BlockHitResult pos, BlockState state) {
        if (!(living instanceof ServerPlayer serverPlayer) || !ManaBurstEntityHelper.evaluateCanHitByPlayer(world, pos.getBlockPos(), serverPlayer)) {
            ci.cancel();
        }
    }
}
