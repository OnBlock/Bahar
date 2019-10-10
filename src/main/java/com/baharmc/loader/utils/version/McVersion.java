package com.baharmc.loader.utils.version;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class McVersion {

	public static final Pattern VERSION_PATTERN = Pattern.compile(
		"\\d+\\.\\d+(\\.\\d+)?(-pre\\d+| Pre-Release \\d+)?|"
			+ "\\d+w\\d+[a-z]|"
			+ "[a-c]\\d\\.\\d+(\\.\\d+)?[a-z]?(_\\d+)?[a-z]?|"
			+ "(rd|inf)-\\d+|"
			+ "1\\.RV-Pre1|3D Shareware v1\\.34"
	);
	public static final Pattern PRE_RELEASE_PATTERN = Pattern.compile(".+(?:-pre| Pre-Release )(\\d+)");
	public static final Pattern RELEASE_PATTERN = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?");
	public static final Pattern SNAPSHOT_PATTERN = Pattern.compile("(\\d+)w(\\d+)([a-z])");
	public static final String STRING_DESC = "Ljava/lang/String;";

	@NotNull
	private final String raw;

	@NotNull
	private final String normalized;

	public McVersion(@NotNull String raw, @NotNull String normalized) {
		this.raw = raw;
		this.normalized = normalized;
	}

	@NotNull
	public String getRaw() {
		return raw;
	}

	@NotNull
	public String getNormalized() {
		return normalized;
	}

}