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

package com.baharmc.loader.utils.semanticversion;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class SemanticVersionImpl implements SemanticVersion {

	private static final Pattern DOT_SEPARATED_ID = Pattern.compile("|[-0-9A-Za-z]+(\\.[-0-9A-Za-z]+)*");

	@NotNull
	private final int[] components;

	@NotNull
	private final String prerelease;

	@NotNull
	private final String build;

	@NotNull
	private String friendlyName = "";

	public SemanticVersionImpl(@NotNull String version, boolean storeX) throws VersionParsingException {
		final int buildDelimPos = version.indexOf('+');

		if (buildDelimPos >= 0) {
			build = version.substring(buildDelimPos + 1);
			version = version.substring(0, buildDelimPos);
		} else {
			build = "";
		}

		int dashDelimPos = version.indexOf('-');

		if (dashDelimPos >= 0) {
			prerelease = version.substring(dashDelimPos + 1);
			version = version.substring(0, dashDelimPos);
		} else {
			prerelease = "";
		}

		if (!prerelease.isEmpty() && !DOT_SEPARATED_ID.matcher(prerelease).matches()) {
			throw new VersionParsingException("Invalid prerelease string '" + prerelease + "'!");
		}

		if (!build.isEmpty() && !DOT_SEPARATED_ID.matcher(build).matches()) {
			throw new VersionParsingException("Invalid build string '" + build + "'!");
		}

		if (version.endsWith(".")) {
			throw new VersionParsingException("Negative version number component found!");
		} else if (version.startsWith(".")) {
			throw new VersionParsingException("Missing version component!");
		}

		final String[] componentStrings = version.split("\\.");

		if (componentStrings.length < 1) {
			throw new VersionParsingException("Did not provide version numbers!");
		}

		components = new int[componentStrings.length];

		for (int i = 0; i < componentStrings.length; i++) {
			final String compStr = componentStrings[i];

			if (storeX) {
				if (compStr.equals("x") || compStr.equals("X") || compStr.equals("*")) {
					if (!prerelease.isEmpty()) {
						throw new VersionParsingException("Pre-release versions are not allowed to use X-ranges!");
					}

					components[i] = Integer.MIN_VALUE;
					continue;
				} else if (i > 0 && components[i - 1] == Integer.MIN_VALUE) {
					throw new VersionParsingException("Interjacent wildcard (1.x.2) are disallowed!");
				}
			}

			if (compStr.trim().isEmpty()) {
				throw new VersionParsingException("Missing version number component!");
			}

			try {
				components[i] = Integer.parseInt(compStr);
				if (components[i] < 0) {
					throw new VersionParsingException("Negative version number component '" + compStr + "'!");
				}
			} catch (NumberFormatException e) {
				throw new VersionParsingException("Could not parse version number component '" + compStr + "'!", e);
			}
		}

		if (storeX && components.length == 1 && components[0] == Integer.MIN_VALUE) {
			throw new VersionParsingException("Versions of form 'x' or 'X' not allowed!");
		}

		buildFriendlyName();
	}

	private void buildFriendlyName() {
		final StringBuilder fnBuilder = new StringBuilder();
		boolean first = true;

		for (int i : components) {
			if (first) {
				first = false;
			} else {
				fnBuilder.append('.');
			}

			if (i == Integer.MIN_VALUE) {
				fnBuilder.append('x');
			} else {
				fnBuilder.append(i);
			}
		}

		if (!prerelease.isEmpty()) {
			fnBuilder.append('-').append(prerelease);
		}

		if (!build.isEmpty()) {
			fnBuilder.append('+').append(build);
		}

		friendlyName = fnBuilder.toString();
	}

	@Override
	public int getVersionComponentCount() {
		return components.length;
	}

	@Override
	public int getVersionComponent(int pos) {
		if (pos < 0) {
			throw new RuntimeException("Tried to access negative version number component!");
		} else if (pos >= components.length) {
			// Repeat "x" if x-range, otherwise repeat "0".
			return components[components.length - 1] == Integer.MIN_VALUE ? Integer.MIN_VALUE : 0;
		} else {
			return components[pos];
		}
	}

	@NotNull
    @Override
	public Optional<String> getPrereleaseKey() {
		return Optional.of(prerelease);
	}

	@NotNull
	@Override
	public Optional<String> getBuildKey() {
		return Optional.of(build);
	}

	@NotNull
    @Override
	public String getFriendlyString() {
		return friendlyName;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SemanticVersionImpl)) {
			return false;
		} else {
			SemanticVersionImpl other = (SemanticVersionImpl) o;
			if (!equalsComponentsExactly(other)) {
				return false;
			}

			return Objects.equals(prerelease, other.prerelease) && Objects.equals(build, other.build);
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(components) * 73 + (!prerelease.isEmpty()
			? prerelease.hashCode() * 11
			: 0
		) + (
			!build.isEmpty()
				? build.hashCode()
				: 0
		);
	}

	@NotNull
	@Override
	public String toString() {
		return getFriendlyString();
	}

	@Override
	public boolean hasWildcard() {
		for (int i : components) {
			if (i < 0) {
				return true;
			}
		}

		return false;
	}

	public boolean equalsComponentsExactly(@NotNull SemanticVersionImpl other) {
		for (int i = 0; i < Math.max(getVersionComponentCount(), other.getVersionComponentCount()); i++) {
			if (getVersionComponent(i) != other.getVersionComponent(i)) {
				return false;
			}
		}

		return true;
	}

	boolean isPrerelease() {
		return !prerelease.isEmpty();
	}
}
