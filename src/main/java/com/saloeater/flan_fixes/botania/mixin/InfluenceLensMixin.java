package com.saloeater.flan_fixes.botania.mixin;

import com.saloeater.flan_fixes.botania.IStorage;
import com.saloeater.flan_fixes.botania.IStorageHelper;
import com.saloeater.flan_fixes.botania.ManaBurstEntityHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.item.lens.InfluenceLens;

import java.util.Iterator;
import java.util.List;

@Mixin(value = InfluenceLens.class, remap = false)
public abstract class InfluenceLensMixin {
    @Inject(method = "updateBurst", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Iterables;concat([Ljava/lang/Iterable;)Ljava/lang/Iterable;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void flan_fixes$updateBurst(
            ManaBurst burst,
            ItemStack stack,
            CallbackInfo ci,
            net.minecraft.world.entity.Entity entity,
            double range,
            net.minecraft.world.phys.AABB bounds,
            List<Entity> items,
            List<Entity> expOrbs,
            List<Entity> arrows,
            List<Entity> fallingBlocks,
            List<Entity> primedTnt,
            List<Entity> bursts
    ) {
        if (!(burst instanceof IStorage storage)) {
            return;
        }
        processList(burst, storage, items);
        processList(burst, storage, expOrbs);
        processList(burst, storage, arrows);
        processList(burst, storage, fallingBlocks);
        processList(burst, storage, primedTnt);
        processList(burst, storage, bursts);
    }

    public void processList(ManaBurst burst, IStorage storage, List<Entity> entities) {
        Iterator<Entity> it = entities.iterator();

        while (it.hasNext()) {
            Entity entity = it.next();
            String uuid = entity.getStringUUID();
            Boolean canHit = this.canHitComplete(burst, storage, uuid, it, entity);
            if (!canHit) {
                it.remove();
            }
        }
    }

    public Boolean canHitComplete(ManaBurst burst, IStorage storage, String uuid, Iterator<Entity> it, Entity entity) {
        if (IStorageHelper.has(storage, uuid)) {
            if (Boolean.FALSE.equals(IStorageHelper.get(storage, uuid))) {
                return false;
            }
        }

        var canHit = this.canHit(burst, entity);
        IStorageHelper.set(storage, uuid, canHit);
        return canHit;
    }

    public Boolean canHit(ManaBurst burst, Entity entity) {
        return ManaBurstEntityHelper.evaluateCanPlayerHitByManaBurst(entity.getOnPos(), burst);
    }
}