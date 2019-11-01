package com.baharmc.loader.language;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.plugin.PluginContained;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class DefaultLanguageAdapted implements LanguageAdapted {

    @NotNull
    @Override
    public <T> T create(@NotNull PluginContained plugin, @NotNull String value, @NotNull Class<T> type)
        throws LanguageAdapterException {

        final String[] methodSplit = value.split("::");

        if (methodSplit.length >= 3) {
            throw new LanguageAdapterException("Invalid handle format: " + value);
        }

        final Class<?> c;
        try {
            c = Class.forName(methodSplit[0], true, BaharLaunched.getInstance().getTargetClassLoader());
        } catch (ClassNotFoundException e) {
            throw new LanguageAdapterException(e);
        }

        if (methodSplit.length == 1) {
            if (type.isAssignableFrom(c)) {
                try {
                    //noinspection unchecked
                    return (T) c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new LanguageAdapterException(e);
                }
            } else {
                throw new LanguageAdapterException("Class " + c.getName() + " cannot be cast to " + type.getName() + "!");
            }
        } else {
            final List<Method> methodList = new ArrayList<>();

            for (Method m : c.getDeclaredMethods()) {
                if (!(m.getName().equals(methodSplit[1]))) {
                    continue;
                }

                methodList.add(m);
            }

            try {
                final Field field = c.getDeclaredField(methodSplit[1]);
                final Class<?> fType = field.getType();

                if ((field.getModifiers() & Modifier.STATIC) == 0) {
                    throw new LanguageAdapterException("Field " + value + " must be static!");
                }

                if (!methodList.isEmpty()) {
                    throw new LanguageAdapterException("Ambiguous " + value + " - refers to both field and method!");
                }

                if (!type.isAssignableFrom(fType)) {
                    throw new LanguageAdapterException("Field " + value + " cannot be cast to " + type.getName() + "!");
                }
                //noinspection unchecked
                return (T) field.get(null);
            } catch (NoSuchFieldException e) {
                // ignore
            } catch (IllegalAccessException e) {
                throw new LanguageAdapterException("Field " + value + " cannot be accessed!", e);
            }

            if (!type.isInterface()) {
                throw new LanguageAdapterException("Cannot proxy method " + value + " to non-interface type " + type.getName() + "!");
            }

            if (methodList.isEmpty()) {
                throw new LanguageAdapterException("Could not find " + value + "!");
            } else if (methodList.size() >= 2) {
                throw new LanguageAdapterException("Found multiple method entries of name " + value + "!");
            }

            final Method targetMethod = methodList.get(0);
            Object object = null;

            if ((targetMethod.getModifiers() & Modifier.STATIC) == 0) {
                try {
                    object = c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new LanguageAdapterException(e);
                }
            }

            final Object targetObject = object;

            //noinspection unchecked
            return (T) Proxy.newProxyInstance(
                BaharLaunched.getInstance().getTargetClassLoader(),
                new Class[] {
                    type
                },
                (proxy, method, args) -> targetMethod.invoke(targetObject, args));
        }
    }

}
