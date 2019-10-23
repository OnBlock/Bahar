package com.baharmc.loader.launched;

import org.cactoos.list.ListOf;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public final class InjectingURLClassLoader extends URLClassLoader {

	private final List<String> exclusions;

	public InjectingURLClassLoader(URL[] urls, ClassLoader classLoader, String... exclusions) {
		super(urls, classLoader);
		this.exclusions  = new ListOf<>(exclusions);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class c = findLoadedClass(name);

			if (c == null) {
				boolean excluded = false;
				for (String s : exclusions) {
					if (name.startsWith(s)) {
						excluded = true;
						break;
					}
				}

				if (!excluded) {
					try {
						c = findClass(name);
					} catch (ClassNotFoundException e) {
						// pass
					}
				}
			}

			if (c == null) {
				c = getParent().loadClass(name);
			}

			if (c == null) {
				throw new ClassNotFoundException(name);
			}

			if (resolve) {
				resolveClass(c);
			}

			return c;
		}
	}
}