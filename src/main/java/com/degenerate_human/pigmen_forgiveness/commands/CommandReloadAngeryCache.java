package com.degenerate_human.pigmen_forgiveness.commands;

import com.degenerate_human.pigmen_forgiveness.handlers.PigmenAngerHandlers;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

/*
 * This suppression is because of the stupid `MethodsReturnNonnullByDefault` annotation
 * */
@SuppressWarnings("NullableProblems")
public class CommandReloadAngeryCache extends PFCommand {
    public CommandReloadAngeryCache() {
        super("pigmen_reload", 3);
    }

    @Override
    protected Map<String, String> getTranslationStrings() {
        Map<String, String> translationStrings = new HashMap<>();
        translationStrings.put("success", "pigmen_forgiveness.command.success");

        return translationStrings;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        /*
        *TODO replace
        *
        * This needs to be replaced with an event, because 2 people could run this command at the same time as eachother
        * which would result in KABOOM! (I don't know that it would _actually_ go kaboom, but it wouldn't be pretty either)
        *
        * An event would allow me to ignore reloads made before an in-progress reload is finished
        * */
        PigmenAngerHandlers.reloadCache(null);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
