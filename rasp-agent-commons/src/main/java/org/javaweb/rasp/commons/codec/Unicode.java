package org.javaweb.rasp.commons.codec;

import java.nio.charset.Charset;

abstract class Unicode extends Charset {

	public Unicode(String name, String[] aliases) {
		super(name, aliases);
	}

	public boolean contains(Charset cs) {
		return (cs.name().equals("US-ASCII"))
				|| (cs.name().equals("ISO-8859-1"))
				|| (cs.name().equals("ISO-8859-15"))
				|| (cs.name().equals("ISO-8859-16"))
				|| (cs.name().equals("windows-1252"))
				|| (cs.name().equals("UTF-8"))
				|| (cs.name().equals("UTF-16"))
				|| (cs.name().equals("UTF-16BE"))
				|| (cs.name().equals("UTF-16LE"))
				|| (cs.name().equals("x-UTF-16LE-BOM"))
				|| (cs.name().equals("GBK"))
				|| (cs.name().equals("GB18030"))
				|| (cs.name().equals("ISO-8859-2"))
				|| (cs.name().equals("ISO-8859-3"))
				|| (cs.name().equals("ISO-8859-4"))
				|| (cs.name().equals("ISO-8859-5"))
				|| (cs.name().equals("ISO-8859-6"))
				|| (cs.name().equals("ISO-8859-7"))
				|| (cs.name().equals("ISO-8859-8"))
				|| (cs.name().equals("ISO-8859-9"))
				|| (cs.name().equals("ISO-8859-13"))
				|| (cs.name().equals("JIS_X0201"))
				|| (cs.name().equals("x-JIS0208"))
				|| (cs.name().equals("JIS_X0212-1990"))
				|| (cs.name().equals("GB2312"))
				|| (cs.name().equals("EUC-KR"))
				|| (cs.name().equals("x-EUC-TW"))
				|| (cs.name().equals("EUC-JP"))
				|| (cs.name().equals("x-euc-jp-linux"))
				|| (cs.name().equals("KOI8-R"))
				|| (cs.name().equals("TIS-620"))
				|| (cs.name().equals("x-ISCII91"))
				|| (cs.name().equals("windows-1251"))
				|| (cs.name().equals("windows-1253"))
				|| (cs.name().equals("windows-1254"))
				|| (cs.name().equals("windows-1255"))
				|| (cs.name().equals("windows-1256"))
				|| (cs.name().equals("windows-1257"))
				|| (cs.name().equals("windows-1258"))
				|| (cs.name().equals("windows-932"))
				|| (cs.name().equals("x-mswin-936"))
				|| (cs.name().equals("x-windows-949"))
				|| (cs.name().equals("x-windows-950"))
				|| (cs.name().equals("windows-31j"))
				|| (cs.name().equals("Big5"))
				|| (cs.name().equals("Big5-HKSCS"))
				|| (cs.name().equals("x-MS950-HKSCS"))
				|| (cs.name().equals("ISO-2022-JP"))
				|| (cs.name().equals("ISO-2022-KR"))
				|| (cs.name().equals("x-ISO-2022-CN-CNS"))
				|| (cs.name().equals("x-ISO-2022-CN-GB"))
				|| (cs.name().equals("x-Johab"))
				|| (cs.name().equals("Shift_JIS"));
	}

}
