package com.baharmc.loader.launched.knot;

import com.baharmc.loader.launched.common.BaharLaunched;
import com.baharmc.loader.provided.GameProvided;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.Objects;

class KnotClassLoaderBasic extends SecureClassLoader implements KnotClassLoaded {

	private static class DynamicURLClassLoader extends URLClassLoader {
		private DynamicURLClassLoader(URL[] urls) {
			super(urls, new DummyClassLoader());
		}

		@Override
		public void addURL(URL url) {
			super.addURL(url);
		}

		static {
			registerAsParallelCapable();
		}
	}

	@NotNull
	private final DynamicURLClassLoader urlLoader;

	@NotNull
	private final ClassLoader originalLoader;

	@NotNull
	private final KnotClassDelegate delegate;

	KnotClassLoaderBasic(@NotNull BaharLaunched launched, @NotNull GameProvided provided) {
		super(new DynamicURLClassLoader(new URL[0]));
		this.originalLoader = getClass().getClassLoader();
		this.urlLoader = (DynamicURLClassLoader) getParent();
		this.delegate = new KnotClassDelegate(launched,this, provided);
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
	public URL getResource(String name) {
		Objects.requireNonNull(name);

		URL url = urlLoader.getResource(name);
		if (url == null) {
			url = originalLoader.getResource(name);
		}
		return url;
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		Objects.requireNonNull(name);

		InputStream inputStream = urlLoader.getResourceAsStream(name);
		if (inputStream == null) {
			inputStream = originalLoader.getResourceAsStream(name);
		}
		return inputStream;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Objects.requireNonNull(name);

		Enumeration<URL> first = urlLoader.getResources(name);
		Enumeration<URL> second = originalLoader.getResources(name);
		return new Enumeration<URL>() {
			Enumeration<URL> current = first;

			@Override
			public boolean hasMoreElements() {
				if (current == null) {
					return false;
				}

				if (current.hasMoreElements()) {
					return true;
				}

				return current == first && second.hasMoreElements();
			}

			@Override
			public URL nextElement() {
				if (current == null) {
					return null;
				}

				if (!current.hasMoreElements()) {
					if (current == first) {
						current = second;
					} else {
						current = null;
						return null;
					}
				}

				return current.nextElement();
			}
		};
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> c = findLoadedClass(name);

			if (c == null && !name.startsWith("com.google.gson.")) {
				byte[] input = delegate.loadClassData(name);
				if (input != null) {
					Metadata metadata = delegate.getMetadata(name, Objects.requireNonNull(urlLoader.getResource(delegate.getClassFileName(name))));

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
				c = originalLoader.loadClass(name);
			}

			if (resolve) {
				resolveClass(c);
			}

			return c;
		}
	}

	@Override
	public void addURL(URL url) {
		urlLoader.addURL(url);
	}

	static {
		registerAsParallelCapable();
	}

	@Override
	public InputStream getResourceAsStream(String classFile, boolean skipOriginalLoader) {
		InputStream inputStream = urlLoader.getResourceAsStream(classFile);
		if (inputStream == null && !skipOriginalLoader) {
			inputStream = originalLoader.getResourceAsStream(classFile);
		}
		return inputStream;
	}
}
