package org.baharmc;

import com.beust.jcommander.JCommander;
import org.jetbrains.annotations.NotNull;

public final class BaharMain {

    private final BaharArgs baharArgs = new BaharArgs();

    @NotNull
    private final String[] args;

    public BaharMain(@NotNull String[] args) {
        this.args = args;
    }

    private void exec() {
        final JCommander jCommander = new JCommander(baharArgs);

        jCommander.setProgramName("Bahar");

        try {
            jCommander.parse(args);
        } catch (Exception exception) {
            // TODO: 18.02.2020 This should be logger.
            System.err.println(exception.getMessage());
            showUsage(jCommander);
        }

    }

    private void showUsage(@NotNull JCommander jCommander) {
        jCommander.usage();
        System.exit(0);
    }

    public static void main(String[] args) {
        new BaharMain(args).exec();
    }

}
