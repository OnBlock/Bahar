package com.baharmc.loader.discovery;

import com.baharmc.loader.launched.BaharLaunched;
import net.fabricmc.loader.util.UrlUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ClasspathPluginCandidateFound implements PluginCandidateFound {

    @Override
    public void findCandidates(@NotNull Consumer<URL> consumer) {
        try {
            Stream.of(
                BaharLaunched.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation()
            ).forEach(url -> {
                BaharLaunched.getInstance().getLogger().debug("[ClasspathPluginCandidateFinded] Processing " + url.getPath());

                try {
                    final File f = UrlUtil.asFile(url);

                    if (f.exists() && (f.isDirectory() || f.getName().endsWith(".jar"))) {
                        consumer.accept(url);
                    }
                } catch (Exception ignored) {
                    // ignored...
                }
            });
        } catch (Exception t) {
            BaharLaunched.getInstance().getLogger().debug("Could not fallback to itself for mod candidate lookup!", t);
        }
    }

}
