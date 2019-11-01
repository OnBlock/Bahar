package com.baharmc.runtime;

import com.baharmc.api.Bahar;
import com.baharmc.api.plugin.Plugin;

public final class Runtime implements Plugin {

    @Override
    public void load() {
        System.out.println("Server brand name is -> " + Bahar.getServer().getBrandName());
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

}
