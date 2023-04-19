package org.javaweb.rasp.commons.utils;

import java.text.DecimalFormat;

import static org.javaweb.rasp.commons.utils.StringUtils.isEmpty;

public class NumberUtils {

	/**
	 * 浮点类数字格式化，去小数点
	 *
	 * @param val    值
	 * @param format 格式
	 * @return 格式化后的数字
	 */
	public static String format(double val, String format) {
		DecimalFormat numFormat = new DecimalFormat(format);
		return numFormat.format(val);
	}

	public static boolean isNum(String str) {
		return isNumeric(str);
	}

	/**
	 * <p>Checks if the CharSequence contains only Unicode digits.
	 * A decimal point is not a Unicode digit and returns false.</p>
	 *
	 * <p>{@code null} will return {@code false}.
	 * An empty CharSequence (length()=0) will return {@code false}.</p>
	 *
	 * <p>Note that the method does not allow for a leading sign, either positive or negative.
	 * Also, if a String passes the numeric test, it may still generate a NumberFormatException
	 * when parsed by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range
	 * for int or long respectively.</p>
	 *
	 * <pre>
	 * StringUtils.isNumeric(null)   = false
	 * StringUtils.isNumeric("")     = false
	 * StringUtils.isNumeric("  ")   = false
	 * StringUtils.isNumeric("123")  = true
	 * StringUtils.isNumeric("\u0967\u0968\u0969")  = true
	 * StringUtils.isNumeric("12 3") = false
	 * StringUtils.isNumeric("ab2c") = false
	 * StringUtils.isNumeric("12-3") = false
	 * StringUtils.isNumeric("12.3") = false
	 * StringUtils.isNumeric("-123") = false
	 * StringUtils.isNumeric("+123") = false
	 * </pre>
	 *
	 * @param cs  the CharSequence to check, may be null
	 * @return {@code true} if only contains digits, and is non-null
	 * @since 3.0 Changed signature from isNumeric(String) to isNumeric(CharSequence)
	 * @since 3.0 Changed "" to return false and not true
	 */
	public static boolean isNumeric(final CharSequence cs) {
		if (isEmpty(cs)) {
			return false;
		}

		final int sz = cs.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 格式化纳秒时间差
	 *
	 * @param diff 纳秒时间差
	 * @return 纳秒时间格式化
	 */
	public static String nanoTimeDiffFormat(double diff) {
		return format(diff / 1000 / 1000, "0.00");
	}

	/**
	 * 格式化纳秒时间差
	 *
	 * @param start 开始时间
	 * @param end   结束时间
	 * @return 格式化纳秒时间差
	 */
	public static String nanoTimeDiffFormat(double start, double end) {
		return nanoTimeDiffFormat(end - start);
	}

}
