package com.degenerate_human.pigmen_forgiveness.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.Map;

/*
* This suppression is because of the stupid `MethodsReturnNonnullByDefault` annotation
* */
@SuppressWarnings("NullableProblems")
public abstract class PFCommand extends CommandBase {
    protected final Map<String, String> translationStrings = getTranslationStrings();
    private final String name;
    private final String usage;
    private final int requiredPermissionLevel;

    public PFCommand(@Nonnull String name, int requiredPermissionLevel) {
        this.name = name;
        this.usage = String.format("pigmen_forgiveness.command.%s.usage", name);
        this.requiredPermissionLevel = requiredPermissionLevel;
    }

    /**
     * This is for defining translation strings that will be used in translation components
     * */
    protected abstract Map<String, String> getTranslationStrings();

    protected TextComponentTranslation getTranslationComponent(@Nonnull String key) {
        if (!translationStrings.containsKey(key)) {
            String errorStr = "Key %s doesn't exist in the defined translation strings map";
            throw new IllegalArgumentException(String.format(errorStr, key));
        }

        return new TextComponentTranslation(translationStrings.get(key));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return requiredPermissionLevel;
    }
}
