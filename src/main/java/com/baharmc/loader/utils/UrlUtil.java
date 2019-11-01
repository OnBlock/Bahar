/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baharmc.loader.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class UrlUtil {

	private UrlUtil() {
	}

	public static URL getSource(@NotNull String filename, @NotNull URL resourceURL) throws UrlConversionException {
		final URL codeSourceURL;

		try {
			final URLConnection connection = resourceURL.openConnection();

			if (connection instanceof JarURLConnection) {
				codeSourceURL = ((JarURLConnection) connection).getJarFileURL();
			} else {
				final String path = resourceURL.getPath();

				if (path.endsWith(filename)) {
					codeSourceURL = new URL(resourceURL.getProtocol(), resourceURL.getHost(), resourceURL.getPort(), path.substring(0, path.length() - filename.length()));
				} else {
					throw new UrlConversionException("Could not figure out code source for file '" + filename + "' and URL '" + resourceURL + "'!");
				}
			}
		} catch (Exception e) {
			throw new UrlConversionException(e);
		}

		return codeSourceURL;
	}

	public static File asFile(@NotNull URL url) throws UrlConversionException {
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new UrlConversionException(e);
		}
	}

	public static Path asPath(@NotNull URL url) throws UrlConversionException {
		if (url.getProtocol().equals("file")) {
			return asFile(url).toPath();
		} else {
			try {
				return Paths.get(url.toURI());
			} catch (URISyntaxException e) {
				throw new UrlConversionException(e);
			}
		}
	}

	public static URL asUrl(@NotNull File file) throws UrlConversionException {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new UrlConversionException(e);
		}
	}

	public static URL asUrl(@NotNull Path path) throws UrlConversionException {
		try {
			return new URL(null, path.toUri().toString());
		} catch (MalformedURLException e) {
			throw new UrlConversionException(e);
		}
	}
}
