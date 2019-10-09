package com.baharmc.loader.mock;

import com.baharmc.loader.Contact;
import org.cactoos.map.MapOf;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class MckContactInformation implements Contact {

    @NotNull
    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Map<String, String> asMap() {
        return new MapOf<>();
    }

}
