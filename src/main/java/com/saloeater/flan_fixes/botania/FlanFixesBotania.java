package com.saloeater.flan_fixes.botania;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FlanFixesBotania.MODID)
public class FlanFixesBotania
{
    public static final String MODID = "flan_fixes_botania";
    
    private static final Logger LOGGER = LogUtils.getLogger();

    public FlanFixesBotania( )
    {
        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(OnPlayerInteract::onPlayerInteract);
        forgeBus.register(this);
    }
}
