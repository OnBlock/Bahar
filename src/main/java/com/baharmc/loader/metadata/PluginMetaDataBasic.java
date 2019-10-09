package com.baharmc.loader.metadata;

import com.baharmc.loader.Contact;
import com.baharmc.loader.License;
import com.baharmc.loader.Person;
import com.baharmc.loader.PluginMetaData;
import com.baharmc.loader.utils.semanticversion.Version;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PluginMetaDataBasic implements PluginMetaData {

    @NotNull
    private final String id;

    @NotNull
    private final String name;

    @NotNull
    private final Version version;

    private final boolean isStable;

    private final boolean isSnapshot;

    @NotNull
    private final String description;

    @NotNull
    private final License license;

    @NotNull
    private final List<Person> authors;

    @NotNull
    private final List<Contact> contacts;



}
