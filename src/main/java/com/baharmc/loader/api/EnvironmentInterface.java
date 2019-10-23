package com.baharmc.loader.api;

import java.lang.annotation.*;

@Retention(RetentionPolicy.CLASS)
@Repeatable(EnvironmentInterfaces.class)
@Target({ElementType.TYPE})
public @interface EnvironmentInterface {
    EnvType value();

    Class<?> itf();
}
