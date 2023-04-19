/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Operations to simplify common {@link MessageDigest} tasks.
 * This class is immutable and thread-safe.
 * However the MessageDigest instances it creates generally won't be.
 * <p>
 * digest algorithms that can be used with the {@link #getDigest(String)} method
 * and other methods that require the Digest algorithm name.
 * <p>
 * Note: the class has shorthand methods for all the algorithms present as standard in Java 6.
 * This approach requires lots of methods for each algorithm, and quickly becomes unwieldy.
 * The following code works with all algorithms:
 * <pre>
 * import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_224;
 * ...
 * byte [] digest = new DigestUtils(SHA_224).digest(dataToDigest);
 * String hdigest = new DigestUtils(SHA_224).digestAsHex(new File("pom.xml"));
 * </pre>
 */
public class DigestUtils {

	/**
	 * The MD5 message digest algorithm defined in RFC 1321.
	 */
	public static final String MD5 = "MD5";

	private static final int STREAM_BUFFER_LENGTH = 1024;

	/**
	 * Reads through an InputStream and returns the digest for the data
	 *
	 * @param messageDigest The MessageDigest to use (e.g. MD5)
	 * @param data          Data to digest
	 * @return the digest
	 * @throws IOException On error reading from the stream
	 * @since 1.11 (was private)
	 */
	public static byte[] digest(final MessageDigest messageDigest, final InputStream data) throws IOException {
		return updateDigest(messageDigest, data).digest();
	}

	/**
	 * Returns a {@code MessageDigest} for the given {@code algorithm}.
	 *
	 * @param algorithm the name of the algorithm requested. See <a
	 *                  href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA"
	 *                  >Appendix A in the Java Cryptography Architecture Reference Guide</a> for information about standard
	 *                  algorithm names.
	 * @return A digest instance.
	 * @throws IllegalArgumentException when a {@link NoSuchAlgorithmException} is caught.
	 * @see MessageDigest#getInstance(String)
	 */
	public static MessageDigest getDigest(final String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns an MD5 MessageDigest.
	 *
	 * @return An MD5 digest instance.
	 * @throws IllegalArgumentException when a {@link NoSuchAlgorithmException} is caught, which should never happen because MD5 is a
	 *                                  built-in algorithm
	 */
	public static MessageDigest getMd5Digest() {
		return getDigest(MD5);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element {@code byte[]}.
	 *
	 * @param data Data to digest
	 * @return MD5 digest
	 */
	public static byte[] md5(final byte[] data) {
		return getMd5Digest().digest(data);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element {@code byte[]}.
	 *
	 * @param data Data to digest
	 * @return MD5 digest
	 * @throws IOException On error reading from the stream
	 * @since 1.4
	 */
	public static byte[] md5(final InputStream data) throws IOException {
		return digest(getMd5Digest(), data);
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 16 element {@code byte[]}.
	 *
	 * @param data Data to digest; converted to bytes using {@link StringUtils#getBytesUtf8(String)}
	 * @return MD5 digest
	 */
	public static byte[] md5(final String data) {
		return md5(StringUtils.getBytesUtf8(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(final byte[] data) {
		return Hex.encodeHexString(md5(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 * @throws IOException On error reading from the stream
	 * @since 1.4
	 */
	public static String md5Hex(final InputStream data) throws IOException {
		return Hex.encodeHexString(md5(data));
	}

	/**
	 * Calculates the MD5 digest and returns the value as a 32 character hex string.
	 *
	 * @param data Data to digest
	 * @return MD5 digest as a hex string
	 */
	public static String md5Hex(final String data) {
		return Hex.encodeHexString(md5(data));
	}

	/**
	 * Reads through an InputStream and updates the digest for the data
	 *
	 * @param digest      The MessageDigest to use (e.g. MD5)
	 * @param inputStream Data to digest
	 * @return the digest
	 * @throws IOException On error reading from the stream
	 * @since 1.8
	 */
	public static MessageDigest updateDigest(final MessageDigest digest, final InputStream inputStream)
			throws IOException {
		final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
		int          read   = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);

		while (read > -1) {
			digest.update(buffer, 0, read);
			read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);
		}

		return digest;
	}

}
