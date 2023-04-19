package org.javaweb.rasp.commons.utils;

public class RASPRequestUtils {

	/**
	 * 实现htmlSpecialChars函数把一些预定义的字符转换为HTML实体编码
	 *
	 * @param content 输入的字符串内容
	 * @return HTML实体化转义后的字符串
	 */
	public static String htmlSpecialChars(String content) {
		if (content == null) {
			return null;
		}

		char[]        charArray = content.toCharArray();
		StringBuilder sb        = new StringBuilder();

		for (char c : charArray) {
			switch (c) {
				case '&':
					sb.append("&amp;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\'':
					sb.append("&#039;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				default:
					sb.append(c);
					break;
			}
		}

		return sb.toString();
	}

}
