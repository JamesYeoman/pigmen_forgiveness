package com.degenerate_human.pigmen_forgiveness.handlers;

import com.degenerate_human.pigmen_forgiveness.PigmenForgiveness;
import com.degenerate_human.pigmen_forgiveness.events.AngryAtPlayerEvent;
import com.degenerate_human.pigmen_forgiveness.interfaces.ICanForgive;
import com.google.common.collect.Sets;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PigmenAngerHandlers {
    private static final Map<UUID, Set<Integer>> targetedPlayers = new ConcurrentHashMap<>();

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
                                PigmenForgiveness.LOGGER.info("One angery boi is now calm");
                            }));

        }

        // Clear the set of angery boiz
        targetedPlayers.get(player.getUniqueID()).clear();
    }
}
