package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.BaharLaunched;
import com.baharmc.loader.provided.GameProvided;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

class KnotCompatibilityClassLoader extends URLClassLoader implements KnotClassLoaded {

	@NotNull
	private final KnotClassDelegate delegate;

	@NotNull
	private final BaharLaunched launched;

	KnotCompatibilityClassLoader(@NotNull BaharLaunched launched, @NotNull GameProvided provider) {
		super(new URL[0], KnotCompatibilityClassLoader.class.getClassLoader());
		this.delegate = new KnotClassDelegate(launched, this, provider);
		this.launched = launched;
	}

	@NotNull
	@Override
	public KnotClassDelegate getDelegate() {
		return delegate;
	}

	@Override
	public boolean isClassLoaded(String name) {
		synchronized (getClassLoadingLock(name)) {
			return findLoadedClass(name) != null;
		}
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findLoadedClass(name);

			if (c == null) {
				byte[] input = delegate.loadClassData(name);
				if (input != null) {
					final URL resourceURL = getResource(delegate.getClassFileName(name));
					final Metadata metadata;

					if (resourceURL == null) {
						metadata = Metadata.EMPTY;
					} else {
						metadata = delegate.getMetadata(name, resourceURL);
					}

					int pkgDelimiterPos = name.lastIndexOf('.');
					if (pkgDelimiterPos > 0) {
						String pkgString = name.substring(0, pkgDelimiterPos);
						if (getPackage(pkgString) == null) {
							definePackage(pkgString, null, null, null, null, null, null, null);
						}
					}

					c = defineClass(name, input, 0, input.length, metadata.getCodeSource());
				}
			}

			if (c == null) {
				c = getParent().loadClass(name);
			}

			if (resolve) {
				resolveClass(c);
			}

			return c;
		}
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	static {
		registerAsParallelCapable();
	}

	@Override
	public InputStream getResourceAsStream(String classFile, boolean skipOriginalLoader) throws IOException {
		if (skipOriginalLoader) {
			if (findResource(classFile) == null) {
				return null;
			}
		}

		return super.getResourceAsStream(classFile);
	}
}