package com.saloeater.flan_fixes.botania;

import net.minecraftforge.event.entity.ProjectileImpactEvent;
import vazkii.botania.common.entity.ManaBurstEntity;

public class EntityInteractEvents {
    public static void projectileHit(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof ManaBurstEntity))
            return;
        if (!BotaniaCompat.canLensProjectileHit((ManaBurstEntity) event.getEntity(), event.getRayTraceResult())) {
            event.setCanceled(true);
        }
    }
}
