package com.baharmc.loader.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaharLogger {
    private static String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    private static String prefix = String.format("[%s|BaharLoader/%s] ", time);

    public static void info(String s) {
        System.out.println(String.format(prefix, "INFO") + s);
    }

    public static void warn(String s) {
        System.out.println(String.format(prefix, "WARN") + s);
    }

    public static void error(String s) {
        System.err.println(String.format(prefix, "ERROR") + s);
    }

    public static void printLine() {
        System.out.println();
    }
}
