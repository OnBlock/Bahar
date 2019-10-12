package com.baharmc.loader.launched;

import com.baharmc.loader.launched.common.MappingConfiguration;
import net.fabricmc.loader.util.UrlUtil;
import net.fabricmc.mappings.Mappings;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class LaunchedBase implements BaharLaunched {

    @NotNull
    private final Map<String, Object> properties = new HashMap<>();

    @NotNull
    private final Logger logger;

    @NotNull
    private final MappingConfiguration mappingConfiguration;

    private boolean mixinReady = false;

    @NotNull
    private Path minecraftJar = Path.of(URI.create(""));

    public LaunchedBase(@NotNull Logger logger) {
        this.logger = logger;
        this.mappingConfiguration = new MappingConfiguration(this);
    }

    @Override
    public void deobfuscate(@NotNull String gameId, @NotNull String gameVersion, @NotNull Path gameDirectory, @NotNull Path jarFile) {
        logger.fine("Requesting deobfuscation of " + jarFile.getFileName());

        final Mappings mappings = mappingConfiguration.getMappings();
        final String targetNameSpace = MappingConfiguration.TARGET_NAMESPACE;

        if (mappings.getNamespaces().contains(targetNameSpace)) {
            return;
        }

        try {
            propose(UrlUtil.asUrl(jarFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (minecraftJar.toString().isEmpty()) {
            minecraftJar = jarFile;
        }
    }

    @Override
    public void doneMixinBootstrapping() {
        if (mixinReady) {
            throw new RuntimeException("Must not call LaunchedBase#finishMixinBootstrapping() twice!");
        }

        try {
            Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, MixinEnvironment.Phase.INIT);
            m.invoke(null, MixinEnvironment.Phase.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mixinReady = true;
    }

    @NotNull
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @NotNull
    @Override
    public MappingConfiguration getMappingConfiguration() {
        return mappingConfiguration;
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isMixinReady() {
        return mixinReady;
    }

    @NotNull
    @Override
    public String getTargetNamespace() {
        return "intermediary";
    }
}
