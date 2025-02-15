/*
 * Copyright 2010 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.javaweb.rasp.commons.lru;

import java.io.Serializable;
import java.util.*;

import static org.javaweb.rasp.commons.lru.ConcurrentLinkedHashMap.checkNotNull;

/**
 * A common set of {@link Weigher} and {@link EntryWeigher} implementations.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 * @see <a href="http://code.google.com/p/concurrentlinkedhashmap/">
 * http://code.google.com/p/concurrentlinkedhashmap/</a>
 */
public final class Weighers {

	private Weighers() {
		throw new AssertionError();
	}

	/**
	 * A entry weigher backed by the specified weigher. The weight of the value
	 * determines the weight of the entry.
	 *
	 * @param weigher the weigher to be "wrapped" in a entry weigher.
	 * @return A entry weigher view of the specified weigher.
	 */
	public static <K, V> EntryWeigher<K, V> asEntryWeigher(
			final Weigher<? super V> weigher) {
		return (weigher == singleton())
				? Weighers.<K, V>entrySingleton()
				: new EntryWeigherView<K, V>(weigher);
	}

	/**
	 * A weigher where an entry has a weight of <tt>1</tt>. A map bounded with
	 * this weigher will evict when the number of key-value pairs exceeds the
	 * capacity.
	 *
	 * @return A weigher where a value takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <K, V> EntryWeigher<K, V> entrySingleton() {
		return (EntryWeigher<K, V>) SingletonEntryWeigher.INSTANCE;
	}

	/**
	 * A weigher where a value has a weight of <tt>1</tt>. A map bounded with
	 * this weigher will evict when the number of key-value pairs exceeds the
	 * capacity.
	 *
	 * @return A weigher where a value takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <V> Weigher<V> singleton() {
		return (Weigher<V>) SingletonWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a byte array and its weight is the number of
	 * bytes. A map bounded with this weigher will evict when the number of bytes
	 * exceeds the capacity rather than the number of key-value pairs in the map.
	 * This allows for restricting the capacity based on the memory-consumption
	 * and is primarily for usage by dedicated caching servers that hold the
	 * serialized data.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each byte takes one unit of capacity.
	 */
	public static Weigher<byte[]> byteArray() {
		return ByteArrayWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a {@link Iterable} and its weight is the
	 * number of elements. This weigher only should be used when the alternative
	 * {@link #collection()} weigher cannot be, as evaluation takes O(n) time. A
	 * map bounded with this weigher will evict when the total number of elements
	 * exceeds the capacity rather than the number of key-value pairs in the map.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each element takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <E> Weigher<? super Iterable<E>> iterable() {
		return (Weigher<Iterable<E>>) (Weigher<?>) IterableWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a {@link Collection} and its weight is the
	 * number of elements. A map bounded with this weigher will evict when the
	 * total number of elements exceeds the capacity rather than the number of
	 * key-value pairs in the map.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each element takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <E> Weigher<? super Collection<E>> collection() {
		return (Weigher<Collection<E>>) (Weigher<?>) CollectionWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a {@link List} and its weight is the number
	 * of elements. A map bounded with this weigher will evict when the total
	 * number of elements exceeds the capacity rather than the number of
	 * key-value pairs in the map.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each element takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <E> Weigher<? super List<E>> list() {
		return (Weigher<List<E>>) (Weigher<?>) ListWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a {@link Set} and its weight is the number
	 * of elements. A map bounded with this weigher will evict when the total
	 * number of elements exceeds the capacity rather than the number of
	 * key-value pairs in the map.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each element takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <E> Weigher<? super Set<E>> set() {
		return (Weigher<Set<E>>) (Weigher<?>) SetWeigher.INSTANCE;
	}

	/**
	 * A weigher where the value is a {@link Map} and its weight is the number of
	 * entries. A map bounded with this weigher will evict when the total number of
	 * entries across all values exceeds the capacity rather than the number of
	 * key-value pairs in the map.
	 * <p>
	 * A value with a weight of <tt>0</tt> will be rejected by the map. If a value
	 * with this weight can occur then the caller should eagerly evaluate the
	 * value and treat it as a removal operation. Alternatively, a custom weigher
	 * may be specified on the map to assign an empty value a positive weight.
	 *
	 * @return A weigher where each entry takes one unit of capacity.
	 */
	@SuppressWarnings({"cast", "unchecked"})
	public static <A, B> Weigher<? super Map<A, B>> map() {
		return (Weigher<Map<A, B>>) (Weigher<?>) MapWeigher.INSTANCE;
	}

	static final class EntryWeigherView<K, V> implements EntryWeigher<K, V>, Serializable {

		static final long serialVersionUID = 1;

		final Weigher<? super V> weigher;

		EntryWeigherView(Weigher<? super V> weigher) {
			checkNotNull(weigher);
			this.weigher = weigher;
		}

		@Override
		public int weightOf(K key, V value) {
			return weigher.weightOf(value);
		}
	}

	enum SingletonEntryWeigher implements EntryWeigher<Object, Object> {
		INSTANCE;

		@Override
		public int weightOf(Object key, Object value) {
			return 1;
		}
	}

	enum SingletonWeigher implements Weigher<Object> {
		INSTANCE;

		@Override
		public int weightOf(Object value) {
			return 1;
		}
	}

	enum ByteArrayWeigher implements Weigher<byte[]> {
		INSTANCE;

		@Override
		public int weightOf(byte[] value) {
			return value.length;
		}
	}

	enum IterableWeigher implements Weigher<Iterable<?>> {
		INSTANCE;

		@Override
		public int weightOf(Iterable<?> values) {
			if (values instanceof Collection<?>) {
				return ((Collection<?>) values).size();
			}
			int size = 0;
			for (Iterator<?> i = values.iterator(); i.hasNext(); ) {
				i.next();
				size++;
			}
			return size;
		}
	}

	enum CollectionWeigher implements Weigher<Collection<?>> {
		INSTANCE;

		@Override
		public int weightOf(Collection<?> values) {
			return values.size();
		}
	}

	enum ListWeigher implements Weigher<List<?>> {
		INSTANCE;

		@Override
		public int weightOf(List<?> values) {
			return values.size();
		}
	}

	enum SetWeigher implements Weigher<Set<?>> {
		INSTANCE;

		@Override
		public int weightOf(Set<?> values) {
			return values.size();
		}
	}

	enum MapWeigher implements Weigher<Map<?, ?>> {
		INSTANCE;

		@Override
		public int weightOf(Map<?, ?> values) {
			return values.size();
		}
	}
}
