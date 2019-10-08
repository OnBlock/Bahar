package com.baharmc.loader.metadata;

import com.baharmc.loader.PluginMetaData;
import com.baharmc.loader.utils.semanticversion.Version;

import java.util.List;

public class PluginMetaDataBasic implements PluginMetaData {
    /**
     * Required
     */
    private String id;
    private String name;
    private Version version;
    private boolean isStable;
    private boolean isSnapshot;

    /**
     * Optional
     */

    private String description;
    private String license;
    private List<String> authors;
    private List<String> contacts;

}
