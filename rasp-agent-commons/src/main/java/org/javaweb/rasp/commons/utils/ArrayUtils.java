package org.javaweb.rasp.commons.utils;

import java.lang.reflect.Array;
import java.util.Collection;

public class ArrayUtils {

	/**
	 * The index value when an element is not found in a list or array: {@code -1}.
	 * This value is returned by methods in this class and can also be used in comparisons with values returned by
	 * various method from {@link java.util.List}.
	 */
	public static final int INDEX_NOT_FOUND = -1;

	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean arrayContains(final int[] array, final int search) {
		return array != null && array.length > 0 && contains(array, search);
	}

	public static boolean arrayContains(final String[] array, final String stringToFind) {
		return array != null && array.length > 0 && contains(array, stringToFind);
	}

	/**
	 * Checks if the object is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array        the array to search through
	 * @param objectToFind the object to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final Object[] array, final Object objectToFind) {
		return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final int[] array, final int valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array,
	 * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final int[] array, final int valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array,
	 * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final int[] array, final int valueToFind, int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}

		if (startIndex < 0) {
			startIndex = 0;
		}

		for (int i = startIndex; i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}

		return INDEX_NOT_FOUND;
	}

	public static void reverse(final byte[] array) {
		if (array != null) {
			reverse(array, 0, array.length);
		}
	}

	/**
	 * Reverses the order of the given array in the given range.
	 * <p>
	 * This method does nothing for a {@code null} input array.
	 * </p>
	 *
	 * @param array               the array to reverse, may be {@code null}
	 * @param startIndexInclusive the starting index. Undervalue (&lt;0) is promoted to 0, overvalue (&gt;array.length) results in no
	 *                            change.
	 * @param endIndexExclusive   elements up to endIndex-1 are reversed in the array. Undervalue (&lt; start index) results in no
	 *                            change. Overvalue (&gt;array.length) is demoted to array length.
	 * @since 3.2
	 */
	public static void reverse(final byte[] array, final int startIndexInclusive, final int endIndexExclusive) {
		if (array == null) {
			return;
		}

		int  i = Math.max(startIndexInclusive, 0);
		int  j = Math.min(array.length, endIndexExclusive) - 1;
		byte tmp;

		while (j > i) {
			tmp = array[j];
			array[j] = array[i];
			array[i] = tmp;
			j--;
			i++;
		}
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array,
	 * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null}
	 * array input
	 */
	public static int indexOf(final boolean[] array, final boolean valueToFind, int startIndex) {
		if (isEmpty(array)) {
			return INDEX_NOT_FOUND;
		}

		if (startIndex < 0) {
			startIndex = 0;
		}

		for (int i = startIndex; i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}

		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given object in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array        the array to search through for the object, may be {@code null}
	 * @param objectToFind the object to find, may be {@code null}
	 * @param startIndex   the index to start searching at
	 * @return the index of the object within the array starting at the index,
	 * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}

		if (startIndex < 0) {
			startIndex = 0;
		}

		if (objectToFind == null) {
			for (int i = startIndex; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = startIndex; i < array.length; i++) {
				if (objectToFind.equals(array[i])) {
					return i;
				}
			}
		}

		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given object in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null} input array.
	 * </p>
	 *
	 * @param array        the array to search through for the object, may be {@code null}
	 * @param objectToFind the object to find, may be {@code null}
	 * @return the index of the object within the array,
	 * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final Object[] array, final Object objectToFind) {
		return indexOf(array, objectToFind, 0);
	}

	/**
	 * Checks if an array of primitive booleans is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final boolean[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive bytes is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final byte[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 */
	private static boolean isArrayEmpty(final Object array) {
		return getLength(array) == 0;
	}

	/**
	 * Returns the length of the specified array.
	 * This method can deal with {@link Object} arrays and with primitive arrays.
	 * <p>
	 * If the input array is {@code null}, {@code 0} is returned.
	 * </p>
	 * <pre>
	 * ArrayUtils.getLength(null)            = 0
	 * ArrayUtils.getLength([])              = 0
	 * ArrayUtils.getLength([null])          = 1
	 * ArrayUtils.getLength([true, false])   = 2
	 * ArrayUtils.getLength([1, 2, 3])       = 3
	 * ArrayUtils.getLength(["a", "b", "c"]) = 3
	 * </pre>
	 *
	 * @param array the array to retrieve the length from, may be null
	 * @return The length of the array, or {@code 0} if the array is {@code null}
	 * @throws IllegalArgumentException if the object argument is not an array.
	 * @since 2.1
	 */
	public static int getLength(final Object array) {
		return array != null ? Array.getLength(array) : 0;
	}

}
