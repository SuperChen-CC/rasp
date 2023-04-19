package org.javaweb.rasp.commons.codec;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.nativeOrder;

public class StringUTF16 {

	static final int HI_BYTE_SHIFT;

	static final int LO_BYTE_SHIFT;

	static {
		if (nativeOrder() == BIG_ENDIAN) {
			HI_BYTE_SHIFT = 8;
			LO_BYTE_SHIFT = 0;
		} else {
			HI_BYTE_SHIFT = 0;
			LO_BYTE_SHIFT = 8;
		}
	}

	// intrinsic performs no bounds checks
	static char getChar(byte[] val, int index) {
		assert index >= 0 && index < length(val) : "Trusted caller missed bounds check";
		index <<= 1;
		return (char) (((val[index++] & 0xff) << HI_BYTE_SHIFT) |
				((val[index] & 0xff) << LO_BYTE_SHIFT));
	}

	public static int length(byte[] value) {
		return value.length >> 1;
	}

}
