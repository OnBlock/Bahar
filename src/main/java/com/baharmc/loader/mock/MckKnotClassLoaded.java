package com.baharmc.loader.mock;

import com.baharmc.loader.launched.knot.KnotClassDelegate;
import com.baharmc.loader.launched.knot.KnotClassLoaded;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;

public class MckKnotClassLoaded implements KnotClassLoaded {
    @NotNull
    @Override
    public KnotClassDelegate getDelegate() {
        return null;
    }

    @Override
    public boolean isClassLoaded(String name) {
        return false;
    }

    @Override
    public void addURL(URL url) {
    }

    @Override
    public InputStream getResourceAsStream(String filename, boolean skipOriginalLoader) {
        return null;
    }
}
