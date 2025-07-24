package com.saloeater.flan_fixes.botania;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(FlanFixesBotania.MODID)
public class FlanFixesBotania
{
    public static final String MODID = "flan_fixes_botania";
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public FlanFixesBotania( )
    {
        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(OnPlayerInteract::onPlayerInteract);
        forgeBus.register(this);
    }
}
