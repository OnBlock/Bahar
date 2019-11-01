package com.baharmc.loader.plugin.metadata;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class PersonBasic implements Person {

    @NotNull
    private final String name;

    @NotNull
    private final List<String> roles;

    @NotNull
    private final List<Contact> contacts;

    public PersonBasic(@NotNull String name, @NotNull List<String> roles, @NotNull List<Contact> contacts) {
        this.name = name;
        this.roles = roles;
        this.contacts = contacts;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public List<String> getRoles() {
        return roles;
    }

    @NotNull
    @Override
    public List<Contact> getContact() {
        return contacts;
    }
}
