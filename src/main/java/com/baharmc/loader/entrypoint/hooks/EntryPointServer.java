package com.baharmc.loader.entrypoint.hooks;

import com.baharmc.loader.loaded.BaharLoaded;

import java.io.File;

public final class EntryPointServer {

    public static void start(File runDir, Object gameInstance) {
        if (runDir == null) {
            runDir = new File(".");
        }

        EntryPointUtils.logErrors("server", BaharLoaded.getInstance().getEntryPoints("server", ), );
    }

}
