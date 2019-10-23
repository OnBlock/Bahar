package com.baharmc.loader.provided;

import com.baharmc.loader.utils.UrlConversionException;
import com.baharmc.loader.utils.UrlUtil;
import org.cactoos.list.ListOf;
import org.cactoos.list.Mapped;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public final class GameProviderHelped {

    @NotNull
    private final ClassLoader classLoader;

    public GameProviderHelped(@NotNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Optional<Path> getSource(@NotNull String fileName) {
        final URL url;
        if ((url = classLoader.getResource(fileName)) != null) {
            try {
                final URL urlSource = UrlUtil.getSource(fileName, url);
                final Path classSourceFile = UrlUtil.asPath(urlSource);

                return Optional.of(classSourceFile);
            } catch (UrlConversionException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    public List<Path> getSources(@NotNull String fileName) {
        try {
            final Enumeration<URL> urls = classLoader.getResources(fileName);
            final List<Path> paths = new ArrayList<>();

            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();

                final URL urlSource = UrlUtil.getSource(fileName, url);
                paths.add(UrlUtil.asPath(urlSource));
            }

            return paths;
        } catch (Exception e) {
            e.printStackTrace();
            return new ListOf<>();
        }
    }

    public Optional<EntryPointResult> findFirstClass(@NotNull List<String> classNames) {
        final List<String> entryPointFilenames = new Mapped<>(
            className -> className.replace('.', '/') + ".class",
            classNames
        );

        for (int i = 0; i < entryPointFilenames.size(); i++) {
            final Optional<Path> classSourcePath = getSource(entryPointFilenames.get(i));

            if (classSourcePath.isPresent()) {
                return Optional.of(new EntryPointResult(classNames.get(i), classSourcePath.get()));
            }
        }

        return Optional.empty();
    }

}
