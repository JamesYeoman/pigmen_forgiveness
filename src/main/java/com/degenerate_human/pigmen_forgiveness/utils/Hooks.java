package com.degenerate_human.pigmen_forgiveness.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Hooks {
    public static MinecraftServer serverInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }
}
