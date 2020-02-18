package org.baharmc;

import com.beust.jcommander.Parameter;

public final class BaharArgs {

    @Parameter(
        names = {"-h", "--help"},
        help = true,
        description = "Display help information."
    )
    private boolean help = false;

    @Parameter(
        names = {"-gui", "--gui"},
        description = "Bahar runs with GUI."
    )
    public boolean gui = false;

    public boolean isHelp() {
        return help;
    }

}
