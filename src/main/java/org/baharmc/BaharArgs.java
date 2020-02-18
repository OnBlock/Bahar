package org.baharmc;

import com.beust.jcommander.Parameter;

public final class BaharArgs {

    @Parameter(names = "-gui", description = "Bahar runs with GUI.")
    public boolean gui = true;

}
