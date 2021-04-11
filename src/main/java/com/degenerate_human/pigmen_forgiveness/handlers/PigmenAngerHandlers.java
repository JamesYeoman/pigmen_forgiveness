package com.degenerate_human.pigmen_forgiveness.handlers;

import com.degenerate_human.pigmen_forgiveness.Constants;
import com.degenerate_human.pigmen_forgiveness.PigmenForgiveness;
import com.degenerate_human.pigmen_forgiveness.interfaces.ICanForgive;
import com.degenerate_human.pigmen_forgiveness.utils.Hooks;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.degenerate_human.pigmen_forgiveness.utils.Booleans.not;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class PigmenAngerHandlers {
    private static final Map<UUID, Set<Integer>> targetedPlayers = new ConcurrentHashMap<>();

    public static void onAnger(int pigZombieID, UUID playerID) {
        if (!targetedPlayers.containsKey(playerID)) {
            Set<Integer> initialiser = Collections.synchronizedSet(Sets.newHashSet(pigZombieID));
            targetedPlayers.put(playerID, initialiser);
        } else {
            synchronized (targetedPlayers.get(playerID)) {
                targetedPlayers.get(playerID).add(pigZombieID);
            }
        }
    }

    public static boolean untargetOnePlayer(UUID playerID) {
        if (!targetedPlayers.containsKey(playerID)) {
            return false;
        }

        for (WorldServer worldServer : Hooks.serverInstance().worlds) {
            targetedPlayers.get(playerID)
                    .parallelStream()
                    .map(worldServer::getEntityByID)
                    .filter(boi -> not(boi instanceof EntityPigZombie))
                    .map(boi -> (ICanForgive)boi)
                    .forEach(ICanForgive::becomeUnangeryBoi);
        }

        targetedPlayers.get(playerID).clear();
        return true;
    }

    private static final Predicate<EntityPigZombie> isAngery = (entity) ->
            entity.isAngry() && entity.getRevengeTarget() instanceof EntityPlayer;

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        targetedPlayers.clear(); // clear the previous session's targeted players
        World world = event.getWorld();
        reloadCache(world);
    }


    /*
     *TODO replace
     *
     * This needs replacing with NBT storage and a flag for force reloading.
     * This will get chonky on a server with lots of people loading and unloading dimensions
     * */
    public static void reloadCache(@Nullable World world_in) {
        List<World> worlds = Collections.singletonList(world_in);
        if (Objects.isNull(world_in)) {
            worlds = Arrays.asList(Hooks.serverInstance().worlds);
        }

        worlds.stream()
                .flatMap(world -> world.getEntities(EntityPigZombie.class, isAngery::test).stream())
                .filter(boi -> not(Objects.isNull(boi.getRevengeTarget())))
                .filter(boi -> not(boi.getRevengeTarget() instanceof EntityPlayer))
                .forEach(boi -> onAnger(boi.getEntityId(), boi.getRevengeTarget().getUniqueID()));
    }

    @SubscribeEvent
    public static void onPlayerDie(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer)event.getEntity();

        if (!(event.getSource().getTrueSource() instanceof EntityPigZombie)) {
            return;
        }

        if (!targetedPlayers.containsKey(player.getUniqueID())) {
            return;
        }

        untargetOnePlayer(player.getUniqueID());
    }
}
