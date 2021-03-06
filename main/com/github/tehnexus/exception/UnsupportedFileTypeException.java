package com.github.tehnexus.exception;

public class UnsupportedFileTypeException extends Exception {

	public UnsupportedFileTypeException() {
	}

	public UnsupportedFileTypeException(String message) {
		super(message);
	}

	public UnsupportedFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedFileTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedFileTypeException(Throwable cause) {
		super(cause);
	}
}
