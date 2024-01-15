package org.javaweb.rasp.commons.utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.list;

public class StringUtils {

	/**
	 * <p>
	 * Eight-bit Unicode Transformation Format.
	 * </p>
	 * <p>
	 * Every implementation of the Java platform is required to support this character encoding.
	 * </p>
	 *
	 * @see <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
	 */
	public static final String UTF_8 = "UTF-8";

	private static final String[] EMPTY_STRING_ARRAY = {};

	private static final int STRING_BUILDER_SIZE = 256;

	/**
	 * The empty String {@code ""}.
	 *
	 * @since 2.0
	 */
	public static final String EMPTY = "";

	/**
	 * 字符串转小写，如果字符串中不包含大写字母不会调用toLowerCase方法
	 *
	 * @param str 字符串
	 * @return 转换成小写后的字符串
	 */
	public static String toLowerCase(String str) {
		if (str != null) {
			char[] chars = null;

			for (int i = 0; i < str.length(); i++) {
				int c   = str.charAt(i);
				int chr = toLowerCase(c);

				// 比较转小写后ASCII是否有变化
				if (c != chr) {
					if (chars == null) {
						chars = str.toCharArray();
					}

					chars[i] = (char) chr;
				}
			}

			if (chars != null) {
				return new String(chars);
			}
		}

		return str;
	}

	/**
	 * ASCII大写字母转小写
	 *
	 * @param ascii 字符
	 * @return 转换后的小写字母
	 */
	public static int toLowerCase(int ascii) {
		if (ascii >= 'A' && ascii <= 'Z') {
			// ('a' - 'A') = 32
			return ascii + 32;
		}

		return ascii;
	}

	/**
	 * 获取JDK文件默认编码
	 */
	private static final String DEFAULT_ENCODING = System.getProperty("file.encoding");

	/**
	 * Case insensitive check if a CharSequence starts with a specified prefix.
	 *
	 * <p>{@code null}s are handled without exceptions. Two {@code null}
	 * references are considered to be equal. The comparison is case insensitive.</p>
	 *
	 * <pre>
	 * StringUtils.startsWithIgnoreCase(null, null)      = true
	 * StringUtils.startsWithIgnoreCase(null, "abc")     = false
	 * StringUtils.startsWithIgnoreCase("abcdef", null)  = false
	 * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
	 * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
	 * </pre>
	 *
	 * @param str    the CharSequence to check, may be null
	 * @param prefix the prefix to find, may be null
	 * @return {@code true} if the CharSequence starts with the prefix, case-insensitive, or
	 * both {@code null}
	 * @see String#startsWith(String)
	 * @since 2.4
	 * @since 3.0 Changed signature from startsWithIgnoreCase(String, String) to startsWithIgnoreCase(CharSequence, CharSequence)
	 */
	public static boolean startsWithIgnoreCase(final CharSequence str, final CharSequence prefix) {
		return startsWith(str, prefix, true);
	}

	/**
	 * Check if a CharSequence starts with a specified prefix (optionally case insensitive).
	 *
	 * @param str        the CharSequence to check, may be null
	 * @param prefix     the prefix to find, may be null
	 * @param ignoreCase indicates whether the compare should ignore case
	 *                   (case-insensitive) or not.
	 * @return {@code true} if the CharSequence starts with the prefix or
	 * both {@code null}
	 * @see String#startsWith(String)
	 */
	private static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) {
		if (str == null || prefix == null) {
			return str == prefix;
		}

		// Get length once instead of twice in the unlikely case that it changes.
		final int preLen = prefix.length();

		if (preLen > str.length()) {
			return false;
		}

		return regionMatches(str, ignoreCase, 0, prefix, 0, preLen);
	}

	/**
	 * Compares two CharSequences, returning {@code true} if they represent
	 * equal sequences of characters, ignoring case.
	 *
	 * <p>{@code null}s are handled without exceptions. Two {@code null}
	 * references are considered equal. The comparison is <strong>case insensitive</strong>.</p>
	 *
	 * <pre>
	 * StringUtils.equalsIgnoreCase(null, null)   = true
	 * StringUtils.equalsIgnoreCase(null, "abc")  = false
	 * StringUtils.equalsIgnoreCase("abc", null)  = false
	 * StringUtils.equalsIgnoreCase("abc", "abc") = true
	 * StringUtils.equalsIgnoreCase("abc", "ABC") = true
	 * </pre>
	 *
	 * @param cs1 the first CharSequence, may be {@code null}
	 * @param cs2 the second CharSequence, may be {@code null}
	 * @return {@code true} if the CharSequences are equal (case-insensitive), or both {@code null}
	 * @since 3.0 Changed signature from equalsIgnoreCase(String, String) to equalsIgnoreCase(CharSequence, CharSequence)
	 * see equals(CharSequence, CharSequence)
	 */
	public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
		if (cs1 == cs2) {
			return true;
		}

		if (cs1 == null || cs2 == null) {
			return false;
		}

		if (cs1.length() != cs2.length()) {
			return false;
		}

		return regionMatches(cs1, true, 0, cs2, 0, cs1.length());
	}

	/**
	 * Case insensitive check if a CharSequence ends with a specified suffix.
	 *
	 * <p>{@code null}s are handled without exceptions. Two {@code null}
	 * references are considered to be equal. The comparison is case insensitive.</p>
	 *
	 * <pre>
	 * StringUtils.endsWithIgnoreCase(null, null)      = true
	 * StringUtils.endsWithIgnoreCase(null, "def")     = false
	 * StringUtils.endsWithIgnoreCase("abcdef", null)  = false
	 * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
	 * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
	 * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
	 * </pre>
	 *
	 * @param str    the CharSequence to check, may be null
	 * @param suffix the suffix to find, may be null
	 * @return {@code true} if the CharSequence ends with the suffix, case-insensitive, or
	 * both {@code null}
	 * @see String#endsWith(String)
	 * @since 2.4
	 * @since 3.0 Changed signature from endsWithIgnoreCase(String, String) to endsWithIgnoreCase(CharSequence, CharSequence)
	 */
	public static boolean endWithIgnoreCase(final CharSequence str, final CharSequence suffix) {
		return endWith(str, suffix, true);
	}

	/**
	 * Check if a CharSequence ends with a specified suffix (optionally case insensitive).
	 *
	 * @param str        the CharSequence to check, may be null
	 * @param suffix     the suffix to find, may be null
	 * @param ignoreCase indicates whether the compare should ignore case
	 *                   (case-insensitive) or not.
	 * @return {@code true} if the CharSequence starts with the prefix or
	 * both {@code null}
	 * @see String#endsWith(String)
	 */
	private static boolean endWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) {
		if (str == null || suffix == null) {
			return str == suffix;
		}

		if (suffix.length() > str.length()) {
			return false;
		}

		final int strOffset = str.length() - suffix.length();

		return regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean isEmpty(final CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	/**
	 * unicode转字符串
	 *
	 * @param content unicode字符串
	 * @return 转码后的字符串
	 */
	public static String ascii2Native(String content) {
		if (isNotEmpty(content)) {
			List<String> asciiList = new ArrayList<String>();

			Matcher matcher = Pattern.compile("\\\\u[0-9a-fA-F]{4}").matcher(content);
			while (matcher.find()) {
				asciiList.add(matcher.group());
			}

			for (int i = 0, j = 2; i < asciiList.size(); i++) {
				String code = asciiList.get(i).substring(j, j + 4);
				char   chr  = (char) Integer.parseInt(code, 16);
				content = content.replace(asciiList.get(i), String.valueOf(chr));
			}
		}

		return content;
	}

	/**
	 * Test whether the given string matches the given substring
	 * at the given index.
	 *
	 * @param str       the original string (or StringBuilder)
	 * @param index     the index in the original string to start matching against
	 * @param substring the substring to match at the given index
	 */
	public static boolean substringMatch(String str, int index, CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;

			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}

		return true;
	}

	public static String charset(String str) {
		try {
			if (!DEFAULT_ENCODING.equals("UTF-8")) {
				return new String(str.getBytes(), DEFAULT_ENCODING);
			}

			return str;
		} catch (UnsupportedEncodingException ignored) {
		}

		return str;
	}

	public static void println(String str) {
		System.out.println(charset(str));
	}

	public static String hex2String(String hexString) {

		if (isNotEmpty(hexString)) {
			List<String> hexList = new ArrayList<String>();

			Matcher matcher = Pattern.compile("\\\\x[0-9a-z]{2}").matcher(hexString);
			while (matcher.find()) {
				hexList.add(matcher.group());
			}

			for (int i = 0, j = 2; i < hexList.size(); i++) {
				String code = hexList.get(i).substring(j, j + 2);
				char   chr  = (char) Integer.parseInt(code, 16);
				hexString = hexString.replace(hexList.get(i), String.valueOf(chr));
			}
		}

		return hexString;
	}

	/**
	 * Check whether the given {@code String} contains actual <em>text</em>.
	 * <p>More specifically, this method returns {@code true} if the
	 * {@code String} is not {@code null}, its length is greater than 0,
	 * and it contains at least one non-whitespace character.
	 *
	 * @param str the {@code String} to check (may be {@code null})
	 * @return {@code true} if the {@code String} is not {@code null}, its
	 * length is greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(String str) {
		return str == null || str.isEmpty() || !containsText(str);
	}

	private static boolean containsText(String str) {
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean containsTexts(String str, CharSequence... array) {
		for (CharSequence sequence : array) {
			if (str.contains(sequence)) return true;
		}

		return false;
	}

	/**
	 * Tokenize the given {@code String} into a {@code String} array via a
	 * {@link StringTokenizer}.
	 * <p>Trims tokens and omits empty tokens.
	 * <p>The given {@code delimiters} string can consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using .
	 *
	 * @param str        the {@code String} to tokenize (potentially {@code null} or empty)
	 * @param delimiters the delimiter characters, assembled as a {@code String}
	 *                   (each of the characters is individually considered as a delimiter)
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Tokenize the given {@code String} into a {@code String} array via a
	 * {@link StringTokenizer}.
	 * <p>The given {@code delimiters} string can consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using .
	 *
	 * @param str               the {@code String} to tokenize (potentially {@code null} or empty)
	 * @param delimiters        the delimiter characters, assembled as a {@code String}
	 *                          (each of the characters is individually considered as a delimiter)
	 * @param trimTokens        trim the tokens via {@link String#trim()}
	 * @param ignoreEmptyTokens omit empty tokens from the result array
	 *                          (only applies to tokens that are empty after trimming; StringTokenizer
	 *                          will not consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see String#trim()
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters,
	                                             boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return EMPTY_STRING_ARRAY;
		}

		StringTokenizer st     = new StringTokenizer(str, delimiters);
		List<String>    tokens = new ArrayList<String>();

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}

		return toStringArray(tokens);
	}

	/**
	 * Copy the given {@link Collection} into a {@code String} array.
	 * <p>The {@code Collection} must contain {@code String} elements only.
	 *
	 * @param collection the {@code Collection} to copy
	 *                   (potentially {@code null} or empty)
	 * @return the resulting {@code String} array
	 */
	public static String[] toStringArray(Collection<String> collection) {
		return (!ArrayUtils.isEmpty(collection) ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
	}

	/**
	 * Copy the given {@link Enumeration} into a {@code String} array.
	 * <p>The {@code Enumeration} must contain {@code String} elements only.
	 *
	 * @param enumeration the {@code Enumeration} to copy
	 *                    (potentially {@code null} or empty)
	 * @return the resulting {@code String} array
	 */
	public static String[] toStringArray(Enumeration<String> enumeration) {
		return (enumeration != null ? toStringArray(list(enumeration)) : EMPTY_STRING_ARRAY);
	}

	/**
	 * Checks if CharSequence contains a search CharSequence irrespective of case,
	 * handling {@code null}. Case-insensitivity is defined as by
	 * {@link String#equalsIgnoreCase(String)}.
	 *
	 * <p>A {@code null} CharSequence will return {@code false}.
	 *
	 * <pre>
	 * StringUtils.containsIgnoreCase(null, *) = false
	 * StringUtils.containsIgnoreCase(*, null) = false
	 * StringUtils.containsIgnoreCase("", "") = true
	 * StringUtils.containsIgnoreCase("abc", "") = true
	 * StringUtils.containsIgnoreCase("abc", "a") = true
	 * StringUtils.containsIgnoreCase("abc", "z") = false
	 * StringUtils.containsIgnoreCase("abc", "A") = true
	 * StringUtils.containsIgnoreCase("abc", "Z") = false
	 * </pre>
	 *
	 * @param str       the CharSequence to check, may be null
	 * @param searchStr the CharSequence to find, may be null
	 * @return true if the CharSequence contains the search CharSequence irrespective of
	 * case or false if not or {@code null} string input
	 * @since 3.0 Changed signature from containsIgnoreCase(String, String) to containsIgnoreCase(CharSequence, CharSequence)
	 */
	public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
		if (str == null || searchStr == null) {
			return false;
		}

		final int len = searchStr.length();
		final int max = str.length() - len;

		for (int i = 0; i <= max; i++) {
			if (regionMatches(str, true, i, searchStr, 0, len)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Green implementation of regionMatches.
	 *
	 * @param cs         the {@code CharSequence} to be processed
	 * @param ignoreCase whether or not to be case insensitive
	 * @param thisStart  the index to start on the {@code cs} CharSequence
	 * @param substring  the {@code CharSequence} to be looked for
	 * @param start      the index to start on the {@code substring} CharSequence
	 * @param length     character length of the region
	 * @return whether the region matched
	 */
	static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
	                             final CharSequence substring, final int start, final int length) {

		if (cs instanceof String && substring instanceof String) {
			return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
		}

		int index1 = thisStart;
		int index2 = start;
		int tmpLen = length;

		// Extract these first so we detect NPEs the same as the java.lang.String version
		final int srcLen   = cs.length() - thisStart;
		final int otherLen = substring.length() - start;

		// Check for invalid parameters
		if (thisStart < 0 || start < 0 || length < 0) {
			return false;
		}

		// Check that the regions are long enough
		if (srcLen < length || otherLen < length) {
			return false;
		}

		while (tmpLen-- > 0) {
			final char c1 = cs.charAt(index1++);
			final char c2 = substring.charAt(index2++);

			if (c1 == c2) {
				continue;
			}

			if (!ignoreCase) {
				return false;
			}

			// The same check as in String.regionMatches():
			if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
					&& Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
				return false;
			}
		}

		return true;
	}

	public static String getRandomString(int length) {
		return randomString(length, null);
	}

	public static String getRandomString(int length, String keyword) {
		return randomString(length, keyword);
	}

	public static String randomWord(int length) {
		return randomString(length, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	/**
	 * 生成随机字符串
	 *
	 * @param length   生成的字符串的长度
	 * @param keyword  生成的字符范围
	 * @param excludes 不需要包含的字符
	 * @return 随机字符串
	 */
	public static String randomString(int length, String keyword, String... excludes) {
		if (length < 1) {
			return null;
		}

		if (isEmpty(keyword)) {
			keyword = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		}

		for (String word : excludes) {
			keyword = keyword.replace(word, "");
		}

		SecureRandom random = new SecureRandom();
		char[]       strs   = keyword.toCharArray();
		char[]       chars  = new char[length];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = strs[random.nextInt(strs.length)];
		}

		return new String(chars);
	}

	/**
	 * <p>Joins the elements of the provided {@code Iterator} into
	 * a single String containing the provided elements.</p>
	 *
	 * <p>No delimiter is added before or after the list. Null objects or empty
	 * strings within the iteration are represented by empty strings.</p>
	 *
	 * <p>See the examples here: {@link #join(Object[], char)}. </p>
	 *
	 * @param iterator  the {@code Iterator} of values to join together, may be null
	 * @param separator the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.0
	 */
	public static String join(final Iterator<?> iterator, final char separator) {
		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}

		if (!iterator.hasNext()) {
			return EMPTY;
		}

		final Object first = iterator.next();

		if (!iterator.hasNext()) {
			return (first != null) ? first.toString() : EMPTY;
		}

		// two or more elements
		// Java default is 16, probably too small
		final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE);

		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			buf.append(separator);
			final Object obj = iterator.next();

			if (obj != null) {
				buf.append(obj);
			}
		}

		return buf.toString();
	}

	public static String join(final Object[] array, final char separator) {
		if (array == null) {
			return null;
		}

		StringBuilder buf = new StringBuilder();

		for (int i = 1; i < array.length; i++) {
			buf.append(separator);

			if (array[i] != null) {
				buf.append(array[i]);
			}
		}

		return buf.toString();
	}

	public static String[] split(final String str, final String splitStr) {
		return split(str, splitStr, -1);
	}

	public static String[] split(final String str, final String splitStr, final int max) {
		return splitWorker(str, splitStr, max, false);
	}

	public static String[] split(final String str, final String splitStr, final int max, boolean preserveAllTokens) {
		return splitWorker(str, splitStr, max, preserveAllTokens);
	}

	/**
	 * Performs the logic for the {@code split} and
	 * {@code splitPreserveAllTokens} methods that return a maximum array
	 * length.
	 *
	 * @param str               the String to parse, may be {@code null}
	 * @param separatorChars    the separate character
	 * @param max               the maximum number of elements to include in the
	 *                          array. A zero or negative value implies no limit.
	 * @param preserveAllTokens if {@code true}, adjacent separators are
	 *                          treated as empty token separators; if {@code false}, adjacent
	 *                          separators are treated as one separator.
	 * @return an array of parsed Strings, {@code null} if null String input
	 */
	private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
		// Performance tuned for 2.0 (JDK1.4)
		// Direct code is quicker than StringTokenizer.
		// Also, StringTokenizer uses isSpace() not isWhitespace()
		if (str == null) {
			return null;
		}

		final int len = str.length();

		if (len == 0) {
			return EMPTY_STRING_ARRAY;
		}

		final List<String> list      = new ArrayList<String>();
		int                sizePlus1 = 1;
		int                i         = 0;
		int                start     = 0;
		boolean            match     = false;
		boolean            lastMatch = false;

		if (separatorChars == null) {
			// Null separator means use whitespace
			while (i < len) {
				if (Character.isWhitespace(str.charAt(i))) {
					if (match || preserveAllTokens) {
						lastMatch = true;

						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i));
						match = false;
					}

					start = ++i;
					continue;
				}

				lastMatch = false;
				match = true;
				i++;
			}
		} else if (separatorChars.length() == 1) {
			// Optimise 1 character case
			final char sep = separatorChars.charAt(0);
			while (i < len) {
				if (str.charAt(i) == sep) {
					if (match || preserveAllTokens) {
						lastMatch = true;

						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i));
						match = false;
					}

					start = ++i;
					continue;
				}

				lastMatch = false;
				match = true;
				i++;
			}
		} else {
			// standard case
			while (i < len) {
				if (separatorChars.indexOf(str.charAt(i)) >= 0) {
					if (match || preserveAllTokens) {
						lastMatch = true;

						if (sizePlus1++ == max) {
							i = len;
							lastMatch = false;
						}

						list.add(str.substring(start, i));
						match = false;
					}

					start = ++i;
					continue;
				}

				lastMatch = false;
				match = true;
				i++;
			}
		}

		if (match || preserveAllTokens && lastMatch) {
			list.add(str.substring(start, i));
		}

		return list.toArray(EMPTY_STRING_ARRAY);
	}

	/**
	 * 检测字符串中是否包含大写字母
	 *
	 * @param str 字符串
	 * @return 检测结果
	 */
	public static boolean containsUpperCase(final String str) {
		if (isEmpty(str)) {
			return false;
		}

		for (int i = 0; i < str.length(); i++) {
			int ascii = str.charAt(i);

			if (ascii >= 'A' && ascii <= 'Z') {
				return true;
			}
		}

		return false;
	}

	/**
	 * 比较两个字符串是否相等，忽略大小写和空白符
	 *
	 * @param str    第一个字符串
	 * @param search 第二个字符串
	 * @return 两个字符串是否相等
	 */
	public static boolean equalIgnoreCaseAndTrim(String str, String search) {
		return eq(str, search, false, true);
	}

	/**
	 * 字符串trim、忽略大小写比较，计算字符串1是否以prefix开始
	 *
	 * @param str    字符串
	 * @param prefix 前缀
	 * @return 字符串1是否startWith prefix
	 */
	public static boolean startWithIgnoreCaseAndTrim(String str, String prefix) {
		return eq(str, prefix, true, true);
	}

	public static boolean eq(String str, String find) {
		return eq(str, find, false, false);
	}

	/**
	 * 获取String#trim()的开始和结束的位置
	 *
	 * @param str 字符串
	 * @param len 字符串长度
	 * @return offset/len
	 */
	public static int[] getTrimIndex(String str, int len) {
		int start = 0;
		int end   = len;

		while ((start < end) && (str.charAt(start) <= ' ')) {
			start++;
		}

		while ((start < end) && (str.charAt(end - 1) <= ' ')) {
			end--;
		}

		return (start > 0 || end < str.length()) ? new int[]{start, end} : new int[]{0, len};
	}

	/**
	 * 字符串trim、忽略大小写比较
	 *
	 * @param str       字符串
	 * @param prefix    前缀
	 * @param startWith 是否使用startWith方式匹配
	 * @return 两个字符串是否相等
	 */
	public static boolean eq(String str, String prefix, boolean startWith, boolean trim) {
		// 比较null或this
		if (str == null || prefix == null) {
			return str == null && prefix == null;
		}

		int len1 = str.length();
		int len2 = prefix.length();

		int[] trim1 = trim ? getTrimIndex(str, len1) : new int[]{0, len1};
		int[] trim2 = trim ? getTrimIndex(prefix, len2) : new int[]{0, len2};

		// 比较长度是否一样
		int lenDiff = (trim1[1] - trim1[0]) - (trim2[1] - trim2[0]);

		if (startWith) {
			if (lenDiff < 0) return false;
		} else {
			if (lenDiff != 0) return false;
		}

		// 比较内容是否一样
		for (int i = trim1[0], j = trim2[0]; i < trim1[1] && j < trim2[1]; i++, j++) {
			char chr  = str.charAt(i);
			char chr2 = prefix.charAt(j);

			// 忽略大小写
			if (chr != chr2 && toLowerCase(chr) != toLowerCase(chr2)) {
				return false;
			}
		}

		return true;
	}

	public static boolean cq(String str, String prefix) {
		if (str == null || prefix == null) {
			return str == null && prefix == null;
		}

		int len  = str.length();
		int plen = prefix.length();

		// 长度检测
		if (len < plen) return false;

		// 字符比较
		for (int i = 0; i < str.length(); i++) {
			char chr1 = str.charAt(i);
			char chr2 = prefix.charAt(i);

			if (chr1 == chr2 || toLowerCase(chr1) == toLowerCase(chr2)) {
				continue;
			}

			if (i == plen - 1) return true;
		}

		return false;
	}

	public static String genUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 统计字符串包含某个char字符的个数
	 *
	 * @param str 字符串
	 * @param chr char
	 * @return char出现次数
	 */
	public static int countOf(String str, char chr) {
		if (str == null) return -1;

		int count = 0;

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == chr) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 替换字符串的第一个字符，如：ABc替换成aBc
	 *
	 * @param str 字符串
	 * @param chr 替换后的首字母
	 * @return 替换后的新字符串
	 */
	public static String replaceFirstChar(String str, char chr) {
		if (str == null) return null;

		return chr + str.substring(1);
	}

	public static String checkMaxLength(String value, int maxLength) {
		if (value != null && value.length() > maxLength) {
			return value.substring(0, maxLength);
		}

		return value;
	}

	public static String[] checkMaxLength(String[] values, int maxLength) {
		int len = 0;

		if (values == null) return null;

		for (String value : values) {
			if (value == null) {
				continue;
			}

			len += value.length();

			if (len > maxLength) return new String[]{value.substring(0, maxLength)};
		}

		return values;
	}

	/**
	 * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte
	 * array.
	 *
	 * @param string the String to encode, may be <code>null</code>
	 * @return encoded bytes, or <code>null</code> if the input string was <code>null</code>
	 * @throws IllegalStateException Thrown when the charset is missing, which should be never according the the Java specification.
	 * @see <a href="http://download.oracle.com/javase/1.5.0/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
	 * @see #getBytesUnchecked(String, String)
	 */
	public static byte[] getBytesUtf8(String string) {
		return getBytesUnchecked(string, UTF_8);
	}

	public static byte[] getBytesUnchecked(String string, String charsetName) {
		if (string == null) {
			return null;
		}

		try {
			return string.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw newIllegalStateException(charsetName, e);
		}
	}

	private static IllegalStateException newIllegalStateException(String charsetName, UnsupportedEncodingException e) {
		return new IllegalStateException(charsetName + ": " + e);
	}

}