package com.baharmc.loader;

import com.baharmc.loader.launched.MainLaunched;
import org.cactoos.list.ListOf;

public class Main {

    public static void main(String[] args) {
        try {
            new MainLaunched(
                new ListOf<>(args)
            ).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
