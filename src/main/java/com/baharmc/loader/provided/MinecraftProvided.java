package com.baharmc.loader.provided;

import com.baharmc.loader.entrypoint.patch.EntryPointPatchHook;
import com.baharmc.loader.plugin.PluginMetaDataBasic;
import com.baharmc.loader.transformed.EntryPointTransformed;
import com.baharmc.loader.transformed.EntryPointTransformerBasic;
import com.baharmc.loader.utils.argument.Arguments;
import com.baharmc.loader.utils.semanticversion.VersionDeserializer;
import com.baharmc.loader.utils.version.McVersion;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class MinecraftProvided implements GameProvided {

    private final EntryPointTransformed transformed = new EntryPointTransformerBasic(it ->
        new ListOf<>(
            new EntryPointPatchHook(it)
        )
    );

    @NotNull
    private final String entryPoint;

    @NotNull
    private final McVersion mcVersion;

    @NotNull
    private final Arguments arguments;

    @NotNull
    private final Path gameJar;

    public MinecraftProvided(@NotNull String entryPoint, @NotNull McVersion mcVersion, @NotNull Arguments arguments, @NotNull Path gameJar) {
        this.entryPoint = entryPoint;
        this.mcVersion = mcVersion;
        this.arguments = arguments;
        this.gameJar = gameJar;
    }

    @NotNull
    @Override
    public String getGameId() {
        return "minecraft";
    }

    @NotNull
    @Override
    public String getGameName() {
        return "Minecraft";
    }

    @NotNull
    @Override
    public String getRawGameVersion() {
        return mcVersion.getRaw();
    }

    @NotNull
    @Override
    public String getNormalizedGameVersion() {
        return mcVersion.getNormalized();
    }

    @NotNull
    @Override
    public Collection<BuiltinPlugin> getBuiltinMods() {
        URL url;

        try {
            url = gameJar.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            return new ListOf<>(
                new BuiltinPlugin(
                    url,
                    new PluginMetaDataBasic(
                        "minecraft",
                        "Minecraft",
                        true,
                        false,
                        "The Minecraft game",
                        VersionDeserializer.deserializeSemantic(getNormalizedGameVersion())
                    )
                )
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ListOf<>();
        }
    }

    @NotNull
    @Override
    public String getEntryPoint() {
        return entryPoint;
    }

    @NotNull
    @Override
    public Path getLaunchDirectory() {
        return arguments.getAsFile("gameDir").toPath();
    }

    @NotNull
    @Override
    public List<Path> getGameContextJars() {
        return new ListOf<>(gameJar);
    }

    @NotNull
    @Override
    public byte[] transform(@NotNull String name) {
        final byte[] transform = transformed.transform(name);

        return transform.length == 0
            ? new byte[0]
            : transform;
    }

    @Override
    public void launch(@NotNull ClassLoader loader) {
        try {
            Class<?> c = loader.loadClass(entryPoint);
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, (Object) arguments.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public EntryPointTransformed getEntryPointTransformed() {
        return transformed;
    }

}
