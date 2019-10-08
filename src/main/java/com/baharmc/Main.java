package com.baharmc;

import com.baharmc.loader.launched.LauncherBasic;

public class Main {

    public static void main(String[] args) {
        try {
            new LauncherBasic(args).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
