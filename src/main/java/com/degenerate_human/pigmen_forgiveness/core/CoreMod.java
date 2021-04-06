package com.degenerate_human.pigmen_forgiveness.core;

import com.degenerate_human.pigmen_forgiveness.Constants;
import com.degenerate_human.pigmen_forgiveness.PigmenForgiveness;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;

public class CoreMod implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MODID + ".coremod");

    public CoreMod() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins." + Constants.MODID + ".json");

        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                File file = new File(location.toURI());
                if (file.isFile()) {
                    CoreModManager.getReparseableCoremods().remove(file.getName());
                }
            } catch (URISyntaxException ignored) {}
        } else {
            LOGGER.warn("No CodeSource, if this is not a development environment we might run into problems!");
            LOGGER.warn(this.getClass().getProtectionDomain());
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}