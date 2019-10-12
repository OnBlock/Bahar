package com.baharmc.loader.entrypoint.hooks;

import java.io.File;

public final class EntryPointServer {

    public static void start(File runDir, Object gameInstance) {
        if (runDir == null) {
            runDir = new File(".");
        }


    }

}
