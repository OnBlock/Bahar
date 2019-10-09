package com.baharmc.loader.metadata;

import com.baharmc.loader.License;
import org.cactoos.list.ListOf;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LicenseBasic implements License {

    @NotNull
    private final List<String> licenses;

    public LicenseBasic(@NotNull List<String> licenses) {
        this.licenses = licenses;
    }

    public LicenseBasic(@NotNull String... licenses) {
        this(new ListOf<>(licenses));
    }

    @NotNull
    @Override
    public List<String> getLicenses() {
        return licenses;
    }
}
