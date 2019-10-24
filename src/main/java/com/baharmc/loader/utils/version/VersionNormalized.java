package com.baharmc.loader.utils.version;

import org.cactoos.Scalar;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

public class VersionNormalized implements Scalar<String> {

    @NotNull
    private final String name;

    @NotNull
    private final String release;

    public VersionNormalized(@NotNull String name, @NotNull String release) {
        this.name = name;
        this.release = release;
    }

    @Override
    public String value() {
        if (name.equalsIgnoreCase(release)) {
            return normalizeVersion();
        }

        final String finalName;
        final Matcher matcher;

        if (name.startsWith(release)) {
            matcher = McVersion.PRE_RELEASE_PATTERN.matcher(name);

            if (matcher.matches()) {
                finalName = String.format("rc.%s", matcher.group(1));
            } else {
                finalName = normalizeVersion();
            }

        } else if ((matcher =  McVersion.SNAPSHOT_PATTERN.matcher(name)).matches()) {
            finalName = String.format("alpha.%s.%s.%s", matcher.group(1), matcher.group(2), matcher.group(3));
        } else {
            finalName = normalizeVersion();
        }

        return String.format("%s-%s", release, finalName);
    }

    @NotNull
    private String normalizeVersion() {
        final StringBuilder ret = new StringBuilder(name.length() + 5);
        boolean lastIsDigit = false;
        boolean lastIsLeadingZero = false;
        boolean lastIsSeparator = false;

        for (int i = 0, max = name.length(); i < max; i++) {
            char c = name.charAt(i);

            if (c >= '0' && c <= '9') {
                if (i > 0 && !lastIsDigit && !lastIsSeparator) {
                    ret.append('.');
                } else if (lastIsDigit && lastIsLeadingZero) {
                    ret.setLength(ret.length() - 1);
                }

                lastIsLeadingZero = c == '0' && (!lastIsDigit || lastIsLeadingZero);
                lastIsSeparator = false;
                lastIsDigit = true;
            } else if (c == '.' || c == '-') {
                if (lastIsSeparator) {
                    continue;
                }

                lastIsSeparator = true;
                lastIsDigit = false;
            } else if ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z')) {
                if (lastIsSeparator) {
                    continue;
                }

                c = '.';
                lastIsSeparator = true;
                lastIsDigit = false;
            } else {
                if (lastIsDigit) {
                    ret.append('.');
                }

                lastIsSeparator = false;
                lastIsDigit = false;
            }

            ret.append(c);
        }

        int start = 0;

        while (start < ret.length() && ret.charAt(start) == '.') {
            start++;
        }

        int end = ret.length();

        while (end > start && ret.charAt(end - 1) == '.') {
            end--;
        }

        return ret.substring(start, end);
    }

}
