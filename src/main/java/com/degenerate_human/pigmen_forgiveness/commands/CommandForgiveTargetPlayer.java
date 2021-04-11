package com.degenerate_human.pigmen_forgiveness.commands;

import com.degenerate_human.pigmen_forgiveness.utils.Hooks;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import scala.util.Try;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public class CommandForgiveTargetPlayer extends CommandBase {

    public String getName() {
        return "forgive";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "pigmen_forgiveness.command.forgive.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        /*
         * Need to disable this inspection, because the implementation of getPlayerByUUID
         * uses a map, and maps return null if no key match is found. The lack of `@Nullable`
         * on getPlayerByUUID means that intellij thinks getPlayerByUUID won't ever be null
         * */
        //noinspection ConstantConditions
        List<UUID> argsList = Arrays.stream(args)
                .map(UUID::fromString)
                .filter(uuid -> !Objects.isNull(server.getPlayerList().getPlayerByUUID(uuid)))
                .collect(Collectors.toList());

        if (argsList.isEmpty()) {
            throw new CommandException("pigmen_forgiveness.command.forgive.insufficient_args");
        }

        for (UUID player : argsList) {

        }

    }

    private static void executeForgive(MinecraftServer server, UUID playerID) throws CommandException {
        Entity shouldBePlayer = server.getEntityFromUuid(playerID);

        if (!(shouldBePlayer instanceof EntityPlayer)) {
            throw new CommandException("pigmen_forgiveness.command.forgive.bad_entity", shouldBePlayer);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index >= 0 && args.length >= 1;
    }
}
