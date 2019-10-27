package com.baharmc.loader.discovery;

public class PluginResolutionException extends Exception {
	public PluginResolutionException(String s) {
		super(s);
	}

	public PluginResolutionException(Throwable t) {
		super(t);
	}

	public PluginResolutionException(String s, Throwable t) {
		super(s, t);
	}
}
