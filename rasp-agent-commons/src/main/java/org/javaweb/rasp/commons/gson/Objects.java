package org.javaweb.rasp.commons.gson;

import java.util.Arrays;

public class Objects {

	private Objects() {
		throw new AssertionError("No Objects instances for you!");
	}

	public static boolean equals(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}

	public static int hashCode(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	public static int hash(Object... values) {
		return Arrays.hashCode(values);
	}

	public static String toString(Object o) {
		return String.valueOf(o);
	}

	public static String toString(Object o, String nullDefault) {
		return (o != null) ? o.toString() : nullDefault;
	}

	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean nonNull(Object obj) {
		return obj != null;
	}

}
