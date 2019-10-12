package com.baharmc.runtime;

import com.baharmc.api.plugin.Plugin;

public class Runtime implements Plugin {

    @Override
    public void load() {
        System.out.println("Runtime was loaded!");
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

}
