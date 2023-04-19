/*
 * Copyright yz 2016-01-14  Email:admin@javaweb.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.rasp.commons.utils;

public class HexUtils {

	public static String bytes2HexString(byte[] bytes) {
		return bytes2HexString(bytes, 0, bytes.length);
	}

	public static String bytes2HexString(byte[] bytes, int srcPos, int endPos) {
		StringBuilder sb = new StringBuilder();

		for (int i = srcPos; i < endPos; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			sb.append(hex.length() == 1 ? '0' + hex : hex.toUpperCase());
		}

		return sb.toString();
	}

	public static byte[] hex2Bytes(String s) {
		byte[] bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			String hex = s.substring(2 * i, 2 * i + 2);

			bytes[i] = (byte) Integer.parseInt(hex, 16);
		}

		return bytes;
	}

}
