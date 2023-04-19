package org.javaweb.rasp.commons.codec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringEscapeUtils {

	/**
	 * The empty String {@code ""}.
	 * @since 2.0
	 */
	public static final String EMPTY = "";

	public static final CharSequenceTranslator UNESCAPE_JAVA;

	static {
		final Map<CharSequence, CharSequence> unescapeJavaMap = new HashMap<CharSequence, CharSequence>();
		unescapeJavaMap.put("\\\\", "\\");
		unescapeJavaMap.put("\\\"", "\"");
		unescapeJavaMap.put("\\'", "'");
		unescapeJavaMap.put("\\", EMPTY);
		UNESCAPE_JAVA = new AggregateTranslator(
				new OctalUnescaper(),     // .between('\1', '\377'),
				new UnicodeUnescaper(),
				new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE),
				new LookupTranslator(Collections.unmodifiableMap(unescapeJavaMap))
		);
	}

	public static String unescapeJava(final String input) {
		return UNESCAPE_JAVA.translate(input);
	}

}
