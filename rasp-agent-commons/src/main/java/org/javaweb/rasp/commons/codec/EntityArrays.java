package org.javaweb.rasp.commons.codec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EntityArrays {

	/**
	 * A Map&lt;CharSequence, CharSequence&gt; to escape the Java
	 * control characters.
	 * <p>
	 * Namely: {@code \b \n \t \f \r}
	 */
	public static final Map<CharSequence, CharSequence> JAVA_CTRL_CHARS_ESCAPE;

	static {
		final Map<CharSequence, CharSequence> initialMap = new HashMap<CharSequence, CharSequence>();
		initialMap.put("\b", "\\b");
		initialMap.put("\n", "\\n");
		initialMap.put("\t", "\\t");
		initialMap.put("\f", "\\f");
		initialMap.put("\r", "\\r");

		JAVA_CTRL_CHARS_ESCAPE = Collections.unmodifiableMap(initialMap);
	}

	/**
	 * Reverse of {@link #JAVA_CTRL_CHARS_ESCAPE} for unescaping purposes.
	 */
	public static final Map<CharSequence, CharSequence> JAVA_CTRL_CHARS_UNESCAPE;

	static {
		JAVA_CTRL_CHARS_UNESCAPE = Collections.unmodifiableMap(invert(JAVA_CTRL_CHARS_ESCAPE));
	}

	/**
	 * Used to invert an escape Map into an unescape Map.
	 *
	 * @param map Map&lt;String, String&gt; to be inverted
	 * @return Map&lt;String, String&gt; inverted array
	 */
	public static Map<CharSequence, CharSequence> invert(final Map<CharSequence, CharSequence> map) {
		final Map<CharSequence, CharSequence> newMap = new HashMap<CharSequence, CharSequence>();

		for (final Map.Entry<CharSequence, CharSequence> pair : map.entrySet()) {
			newMap.put(pair.getValue(), pair.getKey());
		}

		return newMap;
	}

}
