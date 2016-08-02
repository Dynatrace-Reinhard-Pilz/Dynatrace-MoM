package com.dynatrace.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class for dealing with {@link Collection}s, {@link Iterable}s, and
 * Arrays.
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class Iterables {
	
	/**
	 * Checks if the given {@link Collection} is either {@code null} or does not
	 * contain any elements.
	 * 
	 * @param c the {@link Collection} to check if it is {@code null} or does
	 * 		does not contain any elements
	 * 
	 * @return {@code true} if the given {@link Collection} is either
	 * 		{@code null} or does not contain any values, {@code false}
	 * 		otherwise.
	 */
	public static boolean isNullOrEmpty(Collection<?> c) {
		return (c == null) || c.isEmpty();
	}
	
	/**
	 * Checks if the given {@link SizedIterable} is either {@code null} or does
	 * not contain any entries.
	 * 
	 * @param i the {@link SizedIterable} to check whether it actually contains
	 * 		any entries
	 * 
	 * @return {@code true} if the given {@link SizedIterable} is either
	 * 		{@code null} or does not have any entries, {@code false} otherwise
	 */
	public static boolean isNullOrEmpty(SizedIterable<?> i) {
		return (i == null) || (i.size() == 0);
	}
	
	/**
	 * Checks if the given {@link Map} is either {@code null} or does not
	 * contain any elements.
	 * 
	 * @param m the {@link Map} to check if it is {@code null} or does
	 * 		does not contain any elements
	 * 
	 * @return {@code true} if the given {@link Map} is either
	 * 		{@code null} or does not contain any values, {@code false}
	 * 		otherwise.
	 */
	public static boolean isNullOrEmpty(Map<?, ?> m) {
		return (m == null) || m.isEmpty();
	}

	/**
	 * Checks if the given {@link Iterable} is either {@code null} or does not
	 * contain any elements.<br />
	 * <br />
	 * This method should be used with caution. If the {@link Iterable} passed
	 * here is not actually a {@link Collection} an {@link Iterator} is getting
	 * instantiated. This alone might already create some overhead
	 * 
	 * @param c the {@link Iterable} to check if it is {@code null} or does
	 * 		does not contain any elements
	 * 
	 * @return {@code true} if the given {@link Iterable} is either
	 * 		{@code null} or does not contain any values, {@code false}
	 * 		otherwise.
	 */
	public static boolean isNullOrEmpty(Iterable<?> iterable) {
		if (iterable == null) {
			return true;
		}
		if (iterable instanceof Collection) {
			return isNullOrEmpty((Collection<?>) iterable);
		}
		if (iterable instanceof SizedIterable) {
			return isNullOrEmpty((SizedIterable<?>) iterable);
		}
		Iterator<?> it = iterable.iterator();
		if (it == null) {
			return true;
		}
		return !it.hasNext();
	}
	
	/**
	 * Creates a {@link Map} based on the values available within the given
	 * {@link Iterable}.<br />
	 * <br />
	 * Since these values are required to implement {@link Unique} the type of
	 * the unique identifier {@link Unique#getId()} defines is going to be used
	 * as the generic type for the keys.
	 * 
	 * @param values the values to put into a {@link Map}
	 * 
	 * @return a {@link Map} which contains all the values or an empty
	 * 		{@link Map} if the parameter {@code values} is {@code null}.
	 */
	public static <T, K extends Unique<T>> Map<T, K> asMap(Iterable<K> values) {
		if (values == null) {
			return Collections.emptyMap();
		}
		Iterator<?> it = values.iterator();
		if ((it == null) || (!it.hasNext())) {
			return Collections.emptyMap();
		}
		Map<T, K> map = new HashMap<T, K>();
		for (K value : values) {
			if (value == null) {
				continue;
			}
			T key = value.getId();
			if (key != null) {
				map.put(value.getId(), value);
			}
		}
		return map;
	}
	
	/**
	 * Compares the given {@link Objects} by invoking
	 * {@link Object#equals(Object)} after performing the required checks for
	 * either one of them being {@code null}.
	 * 
	 * @param a an {@link Object} or {@code null}
	 * @param b an {@link Object} or {@code null}
	 * 
	 * @return {@code true} if both {@link Object}s are equal according to their
	 * 		implementation of {@link Object#equals(Object)} or if both of them
	 * 		are {@code null}, {@code false} otherwise
	 */
	public static boolean areEqual(Object a, Object b) {
		if (a == null)
			return b == null;
		if (b == null) {
			return false;
		}
		return a.equals(b);
	}
	
	/**
	 * Checks if the given value is contained within the given values.<br />
	 * <br />
	 * In order to check if the given value is contained within them,
	 * {@link Object#equals(Object)} is being invoked.
	 * 
	 * @param values the values to check if the given value is contained
	 * 		among them
	 * @param value the value to check if it is contained within the given
	 * 		values
	 * 
	 * @return {@code true} if the given value is contained within the given
	 * 		values, {@code false} otherwise
	 */
	public static boolean contains(Iterable<?> values, Object value) {
		if (values == null) {
			return false;
		}
		Iterator<?> it = values.iterator();
		if (it == null) {
			return false;
		}
		if (!it.hasNext()) {
			return false;
		}
		for (Object storedValue : values) {
			if (areEqual(storedValue, value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds all entries contained within the second {@link Map} to the first
	 * {@link Map} unless the already exist.<br />
	 * <br />
	 * Removes entries from the first {@link Map} that are not contained within
	 * the second {@link Map}.
	 * 
	 * @param stored the {@link Map} to align with the second {@link Map}
	 * @param updates the {@link Map} to be used to align the first {@link Map}
	 */
	public static <T, K> void merge(Map<T, K> stored, Map<T, K> updates) {
		if (updates == null) {
			stored.clear();
			return;
		}
		K updateValue;
		for (T updateKey : updates.keySet()) {
			updateValue = updates.get(updateKey);
			K storedValue = stored.get(updateKey);
			if (storedValue == null) {
				stored.put(updateKey, updateValue);
			}
		}
		
		Iterable<T> storedKeys = new ArrayList<T>(stored.keySet());
		for (T storedKey : storedKeys) {
			if (!updates.containsKey(storedKey)) {
				stored.remove(storedKey);
			}
		}
	}
	
	/**
	 * Returns the size of the given {@link Collection} after performing a check
	 * for {@code null}.<br />
	 * <br />
	 * If the passed parameter is {@code null}, a size of {@code 0} is assumed.
	 * 
	 * @param c the {@link Collection} to check for its size
	 * 
	 * @return the size of the given {@link Collection} or {@code 0} if the
	 * 		passed parameter is {@code null}.
	 */
	public static final int getSize(Collection<?> c) {
		if (c == null) {
			return 0;
		}
		return c.size();
	}
	
	/**
	 * Creates a {@link Set} containing all the given elements. None of these
	 * elements is allowed to be {@code null}.
	 * 
	 * @param elements the elements to add to the resulting {@link Set}
	 * 
	 * @return a {@link Set} containing all the elements.
	 * 
	 * @throws IllegalArgumentException in case one of the elements is
	 * 		{@code null}
	 */
	public static <T> Set<T> asSet(T[] elements) {
		if (elements == null) {
			return Collections.emptySet();
		}
		HashSet<T> set = new HashSet<T>();
		for (T element : elements) {
			if (element == null) {
				throw new IllegalArgumentException(
					"One of the elements was null"
				);
			}
			set.add(element);
		}
		return set;
	}
	
	/**
	 * Creates a {@link Collection} containing all the given elements including
	 * duplicates and {@code null} values.
	 * 
	 * @param elements the elements to put into a {@link Collection}
	 * 
	 * @return a {@link Collection} containing all the given elements
	 */
	public static <T> Collection<T> asList(T[] elements) {
		return asList(elements, true);
	}
	
	/**
	 * Creates a {@link Collection} containing all the given elements including
	 * duplicates. Optionally it ensures that none of the elements is
	 * {@code null}.
	 * 
	 * @param elements the elements to put into a {@link Collection}
	 * @param allowNull {@code true} if none of the given elements is required
	 * 		to be {@code null}, {@code false} otherwise.
	 * 
	 * @return a {@link Collection} containing all the given elements
	 * 
	 * @throws IllegalArgumentException if one of the given elements is
	 * 		{@code null} when it was required to not be the case.
	 */
	public static <T> Collection<T> asList(T[] elements, boolean allowNull) {
		if (elements == null) {
			return Collections.emptyList();
		}
		ArrayList<T> list = new ArrayList<T>();
		for (T element : elements) {
			if ((element == null) && !allowNull) {
				throw new IllegalArgumentException(
					"One of the elements was null"
				);
			}
			list.add(element);
		}
		return list;
	}
	
	/**
	 * Adds all the entries available within the given {@link Iterable} to the
	 * given {@link Collection}.<br />
	 * <br />
	 * If either the {@link Collection} to add the entries to or the
	 * {@link Iterable} holding the entries are {@code null} this call will
	 * simply get ignored.
	 * 
	 * @param c the {@link Collection} to add the entries to
	 * @param a the {@link Iterable} holding the entries to add
	 */
	public static <T> Collection<T> addAll(Collection<T> c, Iterable<? extends T> a) {
		if (c == null) {
			return c;
		}
		if (a == null) {
			return c;
		}
		for (T t : a) {
			c.add(t);
		}
		return c;
	}
	
	/**
	 * Adds all the entries contained within the given array to the given
	 * {@link Collection}.<br />
	 * <br />
	 * If either the {@link Collection} to add the entries to or the array
	 * holding the entries are {@code null} this call is simply getting ignored.
	 * 
	 * @param c the {@link Collection} to add entries to
	 * @param t the array holding the entries to add
	 */
	public static <T, E extends T> void addAll(Collection<T> c, E[] t) {
		if ((c == null) || (t == null)) {
			return;
		}
		if (t.length == 0) {
			return;
		}
		for (T element : t) {
			c.add(element);
		}
	}
	
	/**
	 * Checks if the given array is either {@code null} or does not contain
	 * any entries.
	 * 
	 * @param t the array to check whether it actually holds entries
	 * 
	 * @return {@code true} if the given array is either {@code null} or does
	 * 		not have any entries, {@code false} otherwise
	 */
	public static <T> boolean isNullOrEmpty(T[] t) {
		if (t == null) {
			return true;
		}
		if (t.length == 0) {
			return true;
		}
		return false;
	}
	
	public static <T, E extends T> void addAll(Collection<T> c, Enumeration<E> e) {
		if (c == null) {
			return;
		}
		if (e == null) {
			return;
		}
		while (e.hasMoreElements()) {
			c.add(e.nextElement());
		}
	}
	
	public static <T> Collection<T> asCollection(SizedIterable<T> iterable) {
		if (iterable == null) {
			return null;
		}
		if (iterable instanceof Collection) {
			return Unchecked.cast(iterable);
		}
		return addAll(new ArrayList<T>(iterable.size()), iterable);
	}
}
