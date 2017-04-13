package com.github.tehnexus.home.util;

public class Util {

	public static <T> boolean isAnyOf(T find, @SuppressWarnings("unchecked") T... args) {
		for (T search : args) {
			if (find.equals(search))
				return true;
		}
		return false;
	}
}
