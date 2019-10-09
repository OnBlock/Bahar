package com.baharmc.loader.metadata;

import com.baharmc.loader.Contact;
import com.baharmc.loader.Person;
import org.jetbrains.annotations.NotNull;

public class PersonBasic implements Person {

    @NotNull
    private final String name;

    @NotNull
    private final Contact contact;

    public PersonBasic(@NotNull String name, @NotNull Contact contact) {
        this.name = name;
        this.contact = contact;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Contact getContact() {
        return contact;
    }

}
