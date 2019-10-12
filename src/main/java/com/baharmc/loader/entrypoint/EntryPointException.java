package com.baharmc.loader.entrypoint;

public class EntryPointException extends RuntimeException {

	public EntryPointException(Throwable t) {
		super(t);
	}

	public EntryPointException(String s) {
		super(s);
	}

	public EntryPointException(String s, Throwable t) {
		super(s, t);
	}

}
