package com.degenerate_human.pigmen_forgiveness;

import com.degenerate_human.pigmen_forgiveness.events.AngryAtPlayerEvent;
import com.degenerate_human.pigmen_forgiveness.interfaces.ICanForgive;
import com.degenerate_human.pigmen_forgiveness.interfaces.IProxy;
import com.google.common.collect.Sets;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod(modid = Constants.MODID, version = Constants.VERSION, name = Constants.MODNAME)
public class PigmenForgiveness {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MODID);
    public static final String CLIENT = Constants.MODGROUP + ".proxies.ClientProxy";
    public static final String SERVER = Constants.MODGROUP + ".proxies.ServerProxy";
    private static final Map<UUID, Set<Integer>> targetedPlayers = new ConcurrentHashMap<>();

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

    @SubscribeEvent
    public void onAnger(AngryAtPlayerEvent event) {
        UUID player = event.getPlayerUUID();
        Integer angeryBoi = event.getPigZombieID();
        if (!targetedPlayers.containsKey(event.getPlayerUUID())) {
            Set<Integer> initialiser = Collections.synchronizedSet(Sets.newHashSet(angeryBoi));
            targetedPlayers.put(player, initialiser);
        } else {
            synchronized (targetedPlayers.get(player)) {
                targetedPlayers.get(player).add(angeryBoi);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerDie(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        if (!(event.getSource().getTrueSource() instanceof EntityPigZombie)) {
            return;
        }

        EntityPlayer player = (EntityPlayer)event.getEntity();
        World world = player.getEntityWorld();
        for (WorldServer worldServer : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
            targetedPlayers.get(player.getUniqueID())
                    .parallelStream()
                    .map(worldServer::getEntityByID)
                    .map(angeryBoi -> angeryBoi instanceof EntityPigZombie ? angeryBoi : null)
                    .map(Optional::ofNullable)
                    .forEach(maybeAngery -> maybeAngery
                            .ifPresent(angeryBoi -> {
                                ((ICanForgive)angeryBoi).becomeUnangeryBoi();
                                world.updateEntity(angeryBoi);
                                player.sendMessage(new TextComponentString("One angery boi is now calm"));
                                LOGGER.info("One angery boi is now calm");
                            }));

        }

        // Clear the set of angery boiz
        targetedPlayers.get(player.getUniqueID()).clear();
    }
}
