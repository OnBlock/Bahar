package com.baharmc.loader.language;


import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

public interface LanguageAdapted {

    @NotNull
    <T> T create(@NotNull PluginContained plugin, @NotNull String value, @NotNull Class<T> type) throws LanguageAdapterException;

}
