package org.javaweb.rasp.commons.codec;

import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

public class ParseUtil {

	private static byte unescape(String s, int i) {
		return (byte) Characters.parseInt(s, i + 1, i + 3, 16);
	}

	public static String decode(String s) {
		int n = s.length();
		if ((n == 0) || (s.indexOf('%') < 0))
			return s;

		StringBuilder       sb = new StringBuilder(n);
		ByteBuffer          bb = ByteBuffer.allocate(n);
		java.nio.CharBuffer cb = java.nio.CharBuffer.allocate(n);

		CharsetDecoder dec = UTF_8.INSTANCE.newDecoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);

		char c = s.charAt(0);
		for (int i = 0; i < n; ) {
			assert c == s.charAt(i);
			if (c != '%') {
				sb.append(c);
				if (++i >= n)
					break;
				c = s.charAt(i);
				continue;
			}
			bb.clear();
			for (; ; ) {
				assert (n - i >= 2);
				try {
					bb.put(unescape(s, i));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				}
				i += 3;
				if (i >= n)
					break;
				c = s.charAt(i);
				if (c != '%')
					break;
			}
			bb.flip();
			cb.clear();
			dec.reset();
			CoderResult cr = dec.decode(bb, cb, true);
			if (cr.isError())
				throw new IllegalArgumentException("Error decoding percent encoded characters");
			cr = dec.flush(cb);
			if (cr.isError())
				throw new IllegalArgumentException("Error decoding percent encoded characters");
			sb.append(cb.flip());
		}

		return sb.toString();
	}

}
