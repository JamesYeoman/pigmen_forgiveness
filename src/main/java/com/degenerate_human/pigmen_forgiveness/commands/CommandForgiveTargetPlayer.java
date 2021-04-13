package com.degenerate_human.pigmen_forgiveness.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.*;
import java.util.stream.Collectors;

import static com.degenerate_human.pigmen_forgiveness.handlers.PigmenAngerHandlers.untargetOnePlayer;
import static com.degenerate_human.pigmen_forgiveness.utils.Booleans.not;

/*
 * This suppression is because of the stupid `MethodsReturnNonnullByDefault` annotation
 * */
@SuppressWarnings("NullableProblems")
public class CommandForgiveTargetPlayer extends PFCommand {
    private static final String INSUFFICIENT_ARGS = "pigmen_forgiveness.command.forgive.insufficient_args";
    private static final String BAD_ENTITY = "pigmen_forgiveness.command.forgive.bad_entity";

    public CommandForgiveTargetPlayer() {
        super("pigmen_forgive", 2);
    }

    @Override
    protected Map<String, String> getTranslationStrings() {
        Map<String, String> translationStrings = new HashMap<>();
        translationStrings.put("not cached", "pigmen_forgiveness.command.forgive.not_cached.1");
        translationStrings.put("other players", "pigmen_forgiveness.command.forgive.not_cached.2");
        translationStrings.put("success", "pigmen_forgiveness.command.success");

        return translationStrings;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        List<UUID> argsList = Arrays.stream(args)
                .map(UUID::fromString)
                .collect(Collectors.toList());

        List<Entity> badEntities = new ArrayList<>();
        List<UUID> goodEntities = new ArrayList<>();

        if (argsList.isEmpty()) {
            throw new CommandException(INSUFFICIENT_ARGS);
        }

        /*
         * Can't do this in a stream because streams don't allow exception bubbling
         * and I'd rather not add in a dependency on https://www.vavr.io/ just for this
         * */
        for (UUID player : argsList) {
            Entity shouldBePlayer = server.getEntityFromUuid(player);

            if (shouldBePlayer instanceof EntityPlayer) {
                goodEntities.add(player);
            } else {
                badEntities.add(shouldBePlayer);
            }
        }

        // Exit before processing the good entities, if not all arguments are players
        if (!badEntities.isEmpty()) {
            throw new CommandException(BAD_ENTITY, badEntities.toArray());
        }

        TextComponentTranslation base = getTranslationComponent("not cached");

        /*
         * Need to have this suppression because Intellij IDEA is adamant that
         * Entity::getName might produce a null pointer exception
         * */
        //noinspection ConstantConditions
        goodEntities.stream()
                .map(id -> new Tuple<>(id, untargetOnePlayer(id)))
                .filter(data -> not(data.getSecond()))
                .map(data -> server.getEntityFromUuid(data.getFirst()))
                .map(Entity::getName)
                .forEach(data -> base.appendSibling(new TextComponentString(data)));

        if (base.getSiblings().isEmpty()) {
            sender.sendMessage(getTranslationComponent("success"));
        } else {
            sender.sendMessage(base);
            sender.sendMessage(getTranslationComponent("other players"));
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index >= 0 && args.length >= 1;
    }
}
