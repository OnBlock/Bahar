package com.baharmc.loader.entrypoint;

import com.baharmc.loader.language.LanguageAdapted;
import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;

public final class EntryPointStorage {

    public class EntryBasic implements Entry {

        private final Map<Class<?>, Object> instanceMap = new IdentityHashMap<>();

        @NotNull
        private final PluginContained pluginContained;

        @NotNull
        private final LanguageAdapted languageAdapted;

        @NotNull
        private final String value;

        public EntryBasic(@NotNull PluginContained pluginContained, @NotNull LanguageAdapted languageAdapted, @NotNull String value) {
            this.pluginContained = pluginContained;
            this.languageAdapted = languageAdapted;
            this.value = value;
        }

        @Override
        public <T> @NotNull T getOrCreate(Class<T> type) throws Exception {
            Object o = instanceMap.get(type);
            if (o == null) {
                o = create(type);
                instanceMap.put(type, o);
            }
            //noinspection unchecked
            return (T) o;
        }

        private <T> T create(Class<T> type) throws Exception {
            return languageAdapted.create(pluginContained, value, type);
        }

    }



    interface Entry {

        @NotNull
        <T> T getOrCreate(Class<T> type) throws Exception;

    }

}
