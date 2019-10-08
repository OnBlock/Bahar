package com.baharmc;

import com.baharmc.loader.launched.Launched;
import org.cactoos.list.ListOf;

public class Main {

    public static void main(String[] args) {
        try {
            new Launched(
                new ListOf<>(args)
            ).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
