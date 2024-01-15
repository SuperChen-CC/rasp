package org.javaweb.rasp.commons.codec;

import java.util.Arrays;

import static java.lang.Character.*;

public class Characters {

	public static int parseInt(CharSequence s, int beginIndex, int endIndex, int radix)
			throws NumberFormatException {

		if (s == null)
			throw new NullPointerException();

		if (beginIndex < 0 || beginIndex > endIndex || endIndex > s.length()) {
			throw new IndexOutOfBoundsException();
		}

		if (radix < java.lang.Character.MIN_RADIX) {
			throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
		}

		if (radix > java.lang.Character.MAX_RADIX) {
			throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
		}

		boolean negative = false;
		int     i        = beginIndex;
		int     limit    = -Integer.MAX_VALUE;

		if (i < endIndex) {
			char firstChar = s.charAt(i);

			if (firstChar < '0') {
				// Possible leading "+" or "-"
				if (firstChar == '-') {
					negative = true;
					limit = Integer.MIN_VALUE;
				} else if (firstChar != '+') {
					throw forCharSequence(s, beginIndex,
							endIndex, i);
				}

				i++;

				if (i == endIndex) { // Cannot have lone "+" or "-"
					throw forCharSequence(s, beginIndex, endIndex, i);
				}
			}

			int multmin = limit / radix;
			int result  = 0;

			while (i < endIndex) {
				// Accumulating negatively avoids surprises near MAX_VALUE
				int digit = java.lang.Character.digit(s.charAt(i), radix);

				if (digit < 0 || result < multmin) {
					throw forCharSequence(s, beginIndex,
							endIndex, i);
				}

				result *= radix;

				if (result < limit + digit) {
					throw forCharSequence(s, beginIndex,
							endIndex, i);
				}

				i++;
				result -= digit;
			}
			return negative ? result : -result;
		} else {
			throw forInputString("", radix);
		}
	}

	public static NumberFormatException forCharSequence(CharSequence s, int beginIndex, int endIndex, int errorIndex) {
		return new NumberFormatException("Error at index "
				+ (errorIndex - beginIndex) + " in: \""
				+ s.subSequence(beginIndex, endIndex) + "\"");
	}

	public static NumberFormatException forInputString(String s, int radix) {
		return new NumberFormatException("For input string: \"" + s + "\"" +
				(radix == 10 ? "" : " under radix " + radix));
	}

	public static boolean hasNegatives(byte[] ba, int off, int len) {
		for (int i = off; i < off + len; i++) {
			if (ba[i] < 0) {
				return true;
			}
		}
		return false;
	}

	// inflatedCopy byte[] -> char[]
	public static void inflate(byte[] src, int srcOff, char[] dst, int dstOff, int len) {
		for (int i = 0; i < len; i++) {
			dst[dstOff++] = (char) (src[srcOff++] & 0xff);
		}
	}

	/**
	 * Decodes ASCII from the source byte array into the destination
	 * char array. Used via JavaLangAccess from UTF_8 and other charset
	 * decoders.
	 *
	 * @return the number of bytes successfully decoded, at most len
	 */
	/* package-private */
	static int decodeASCII(byte[] sa, int sp, char[] da, int dp, int len) {
		if (!hasNegatives(sa, sp, len)) {
			inflate(sa, sp, da, dp, len);
			return len;
		} else {
			int start = sp;
			int end   = sp + len;
			while (sp < end && sa[sp] >= 0) {
				da[dp++] = (char) sa[sp++];
			}
			return sp - start;
		}
	}

	static final byte LATIN1 = 0;

	public static int implEncodeAsciiArray(char[] sa, int sp, byte[] da, int dp, int len) {
		int i = 0;
		for (; i < len; i++) {
			char c = sa[sp++];
			if (c >= '\u0080')
				break;
			da[dp++] = (byte) c;
		}
		return i;
	}

	public static byte[] encodeASCII(byte coder, byte[] val) {
		if (coder == LATIN1) {
			byte[] dst = Arrays.copyOf(val, val.length);
			for (int i = 0; i < dst.length; i++) {
				if (dst[i] < 0) {
					dst[i] = '?';
				}
			}
			return dst;
		}

		int    len = val.length >> 1;
		byte[] dst = new byte[len];
		int    dp  = 0;

		for (int i = 0; i < len; i++) {
			char c = StringUTF16.getChar(val, i);
			if (c < 0x80) {
				dst[dp++] = (byte) c;
				continue;
			}
			if (java.lang.Character.isHighSurrogate(c) && i + 1 < len &&
					java.lang.Character.isLowSurrogate(StringUTF16.getChar(val, i + 1))) {
				i++;
			}
			dst[dp++] = '?';
		}
		if (len == dp) {
			return dst;
		}
		return Arrays.copyOf(dst, dp);
	}

	public static boolean isSurrogate(char ch) {
		return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
	}

	public static char highSurrogate(int codePoint) {
		return (char) ((codePoint >>> 10) + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
	}

	public static char lowSurrogate(int codePoint) {
		return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
	}

	/**
	 * Determines whether the specified character (Unicode code point)
	 * is in the <a href="#BMP">Basic Multilingual Plane (BMP)</a>.
	 * Such code points can be represented using a single {@code char}.
	 *
	 * @param codePoint the character (Unicode code point) to be tested
	 * @return {@code true} if the specified code point is between
	 * {MIN_VALUE} and {MAX_VALUE} inclusive;
	 * {@code false} otherwise.
	 * @since 1.7
	 */
	public static boolean isBmpCodePoint(int codePoint) {
		return codePoint >>> 16 == 0;
		// Optimized form of:
		//     codePoint >= MIN_VALUE && codePoint <= MAX_VALUE
		// We consistently use logical shift (>>>) to facilitate
		// additional runtime optimizations.
	}

}
