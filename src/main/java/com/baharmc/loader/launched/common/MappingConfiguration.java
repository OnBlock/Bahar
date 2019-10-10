package com.baharmc.loader.launched.common;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.launched.LaunchedBase;
import net.fabricmc.mappings.Mappings;
import net.fabricmc.mappings.MappingsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public final class MappingConfiguration {

    public static final String TARGET_NAMESPACE = "intermediary";

    @NotNull
    private final BaharLaunched baharLaunched;

    @NotNull
    private Mappings mappings = MappingsProvider.createEmptyMappings();

    private boolean checkedMappings = false;

    public MappingConfiguration(@NotNull BaharLaunched baharLaunched) {
        this.baharLaunched = baharLaunched;
    }

    @NotNull
    public Mappings getMappings() {
        if (checkedMappings) {
            return mappings;
        }

        final InputStream mappingStream =
            LaunchedBase.class.getClassLoader().getResourceAsStream("mappings/mappings.tiny");

        if (mappingStream == null) {
            return mappings;
        }

        try {
            long time = System.currentTimeMillis();
            mappings = MappingsProvider.readTinyMappings(mappingStream);
            baharLaunched.getLogger().fine("Loading mappings took " + (System.currentTimeMillis() - time) + " ms");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            mappingStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        checkedMappings = true;

        return mappings;
    }

}
