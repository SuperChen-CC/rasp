package org.javaweb.rasp.commons.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.javaweb.rasp.commons.constants.RASPConstants.DEFAULT_ENCODING;
import static org.javaweb.rasp.commons.utils.Base64.*;

/**
 * 各种常见算法加密解密类
 *
 * @author yz
 */
public class EncryptUtils {

	/**
	 * MD5加密
	 *
	 * @param context Hash内容
	 * @return md5
	 */
	public static String md5(String context) {
		return DigestUtils.md5Hex(context != null ? context : "");
	}

	/**
	 * MD5加密
	 *
	 * @param context Hash内容
	 * @return md5
	 */
	public static String md5(String[] context) {
		return DigestUtils.md5Hex(context != null ? Arrays.toString(context) : "");
	}

	/**
	 * MD5加密
	 *
	 * @param bytes Hash字节
	 * @return md5
	 */
	public static String md5(byte[] bytes) {
		return DigestUtils.md5Hex(bytes);
	}

	/**
	 * MD5加密
	 *
	 * @param in Hash输入流
	 * @return md5
	 * @throws IOException IO异常
	 */
	public static String md5(InputStream in) throws IOException {
		try {
			return DigestUtils.md5Hex(in);
		} finally {
			if (in instanceof FileInputStream) IOUtils.closeQuietly(in);
		}
	}

	/**
	 * Base64编码
	 *
	 * @param str 字符串
	 * @return Base64字符串
	 */
	public static String base64Encode(String str) throws UnsupportedEncodingException {
		return new String(base64Encode(str.getBytes("UTF-8")));
	}

	/**
	 * Base64编码
	 *
	 * @param bytes byte数组
	 * @return Base64编码后的byte数组
	 */
	public static byte[] base64Encode(byte[] bytes) {
		return getEncoder().encode(bytes);
	}

	/**
	 * Base64解码
	 *
	 * @param str 字符串
	 * @return Base64解码后的字符串
	 */
	public static String base64Decode(String str) {
		try {
			return new String(base64Decode(str.getBytes()), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * Base64解码
	 *
	 * @param bytes byte数组
	 * @return Base64解码后的byte数组
	 */
	public static byte[] base64Decode(byte[] bytes) {
		return getMimeDecoder().decode(bytes);
	}

	/**
	 * 加密
	 *
	 * @param data   待加密的字符串
	 * @param rc4Key 加密key
	 * @return 加密后的字符串
	 */
	public static String enContent(String data, String rc4Key) {
		try {
			return new String(getEncoder().encode(RC4Utils.encryptionRC4Byte(data, rc4Key)), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static String enContent(Object obj, String rc4Key) {
		try {
			String json = JsonUtils.toJson(obj);
			return new String(getEncoder().encode(RC4Utils.encryptionRC4Byte(json, rc4Key)), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 解密
	 *
	 * @param data 加密后的字符串
	 * @param key  加密Key
	 * @return 解密后的内容
	 */
	public static String deContent(String data, String key) {
		try {
			return RC4Utils.decryptionRC4(getMimeDecoder().decode(data), key);
		} catch (Exception e) {
			return null;
		}
	}

}
