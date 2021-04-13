package com.degenerate_human.pigmen_forgiveness;

import com.degenerate_human.pigmen_forgiveness.commands.CommandForgiveTargetPlayer;
import com.degenerate_human.pigmen_forgiveness.commands.CommandReloadAngeryCache;
import com.degenerate_human.pigmen_forgiveness.interfaces.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Constants.MODID, version = Constants.VERSION, name = Constants.MODNAME)
public class PigmenForgiveness {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MODID);
    public static final String CLIENT = Constants.MODGROUP + ".proxies.ClientProxy";
    public static final String SERVER = Constants.MODGROUP + ".proxies.ServerProxy";

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.debug("Pigmen are actually nice");
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandForgiveTargetPlayer());
        event.registerServerCommand(new CommandReloadAngeryCache());
    }
}
