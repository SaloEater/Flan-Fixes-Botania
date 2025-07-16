package com.saloeater.flan_fixes.botania;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlanFixesBotania.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataEvent {
    @SubscribeEvent
    public static void data(GatherDataEvent event) {
        DataGenerator data = event.getGenerator();
        data.addProvider(event.includeServer(), new PermissionGen(data.getPackOutput()));
    }
}
