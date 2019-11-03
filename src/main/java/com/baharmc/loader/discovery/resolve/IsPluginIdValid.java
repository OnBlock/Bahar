package com.baharmc.loader.discovery.resolve;

import com.baharmc.loader.discovery.PluginResolve;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;
import org.cactoos.list.Sorted;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class IsPluginIdValid implements Scalar<List<String>> {

    @NotNull
    private final String pluginId;

    public IsPluginIdValid(@NotNull String pluginId) {
        this.pluginId = pluginId;
    }

    @Override
    public List<String> value() {
        final List<String> errorList = new ArrayList<>();

        if (pluginId.isEmpty()) {
            errorList.add("is empty!");
            return errorList;
        }

        if (pluginId.length() == 1) {
            errorList.add("is only a single character! (It must be at least 2 characters long)!");
        } else if (pluginId.length() > 64) {
            errorList.add("has more than 64 characters!");
        }

        final char first = pluginId.charAt(0);

        if (first < 'a' || first > 'z') {
            errorList.add("starts with an invalid character '" + first + "' (it must be a lowercase a-z - upper case isn't allowed anywhere in the ID)");
        }

        final Set<Character> invalidChars = new HashSet<>();

        for (int i = 1; i < pluginId.length(); i++) {
            char c = pluginId.charAt(i);

            if (c == '-' || c == '_' || ('0' <= c && c <= '9') || ('a' <= c && c <= 'z')) {
                continue;
            }

            invalidChars.add(c);
        }

        if (invalidChars.isEmpty()) {
            return new ListOf<>();
        }

        final StringBuilder error = new StringBuilder("contains invalid characters: '");

        for (Character c : new Sorted<>(invalidChars.toArray(new Character[0]))) {
            error.append(c.charValue());
        }

        errorList.add(error.append("'!").toString());

        assert errorList.isEmpty() == PluginResolve.PLUGIN_ID_PATTERN.matcher(pluginId).matches() :
            "Errors list " + errorList + " didn't match the mod ID pattern!";

        return errorList;
    }
}
