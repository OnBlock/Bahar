package com.baharmc.loader.entrypoint.hooks;

import com.baharmc.api.plugin.Plugin;
import com.baharmc.loader.loaded.BaharLoaded;
import com.baharmc.loader.loaded.InitiatePlugin;

import java.io.File;

public final class EntryPointServer {

    public static void start(File runDir, Object gameInstance) {
        if (runDir == null) {
            runDir = new File(".");
        }

        if (gameInstance == null) {
            return;
        }

        new InitiatePlugin(gameInstance, runDir).init();

        EntryPointUtils.logErrors("main", BaharLoaded.getInstance().getEntryPoints("main", Plugin.class), Plugin::load);
    }

}
