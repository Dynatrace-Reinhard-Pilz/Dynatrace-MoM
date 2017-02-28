package com.dynatrace.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import com.dynatrace.Coverage;

/**
 * Tests for class {@link Iterables}
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public class IterablesTest extends Coverage<Iterables> {
	
	@Test
	public void testIsNullOrEmptyCollection() {
		Assert.assertTrue(Iterables.isNullOrEmpty((Collection<?>) null));
		Assert.assertTrue(Iterables.isNullOrEmpty((Iterable<?>) null));
		Assert.assertTrue(Iterables.isNullOrEmpty(wrap(null)));
		Assert.assertTrue(Iterables.isNullOrEmpty((Iterable<?>) Collections.emptyList()));
		Assert.assertTrue(Iterables.isNullOrEmpty(Collections.emptyList()));
		Assert.assertTrue(Iterables.isNullOrEmpty(wrap(Collections.emptyList())));
		Collection<UUID> c = new ArrayList<UUID>();
		c.add(UUID.randomUUID());
		Assert.assertFalse(Iterables.isNullOrEmpty(c));
		Assert.assertFalse(Iterables.isNullOrEmpty(wrap(c)));
		c.add(UUID.randomUUID());
		Assert.assertFalse(Iterables.isNullOrEmpty(c));
		Assert.assertFalse(Iterables.isNullOrEmpty(wrap(c)));
	}

	@Test
	public void testIsNullOrEmptyIterable() {
		Assert.assertTrue(Iterables.isNullOrEmpty((Iterable<?>) null));
		Assert.assertTrue(Iterables.isNullOrEmpty(wrap(null)));
		Assert.assertTrue(Iterables.isNullOrEmpty((Iterable<?>) Collections.emptyList()));
	}
	
	@Test
	public void testIsNullOrEmptyMap() {
		Assert.assertTrue(Iterables.isNullOrEmpty((Map<?, ?>) null));
		Map<UUID, UUID> map = new HashMap<UUID, UUID>();
		Assert.assertTrue(Iterables.isNullOrEmpty(map));
		map.put(UUID.randomUUID(), UUID.randomUUID());
		Assert.assertFalse(Iterables.isNullOrEmpty(map));
		map.put(UUID.randomUUID(), UUID.randomUUID());
		Assert.assertFalse(Iterables.isNullOrEmpty(map));
	}
	
	@Test
	public void testAsMap() {
		Map<Object, Unique<Object>> asMap = Iterables.asMap(null);
		Assert.assertNotNull(asMap);
		Assert.assertTrue(asMap.isEmpty());
		
		Iterator<UUIDUnique> it = Collections.<UUIDUnique>emptyList().iterator();
		Map<UUID, UUIDUnique> asMap2 = Iterables.asMap(new GenericIterable<UUIDUnique>(it));
		Assert.assertNotNull(asMap);
		Assert.assertTrue(asMap.isEmpty());
		
		asMap2 = Iterables.asMap(new GenericIterable<UUIDUnique>(null));
		Assert.assertNotNull(asMap);
		Assert.assertTrue(asMap.isEmpty());		
		
		Collection<UUIDUnique> list = new ArrayList<UUIDUnique>();
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertTrue(asMap2.isEmpty());
		
		UUIDUnique a = new UUIDUnique();
		list.add(a);
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertEquals(1, asMap2.size());
		Assert.assertTrue(a == asMap2.get(a.getId()));
		
		list.add(a);
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertEquals(1, asMap2.size());
		Assert.assertTrue(a == asMap2.get(a.getId()));
		
		list.add(null);
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertEquals(1, asMap2.size());
		Assert.assertTrue(a == asMap2.get(a.getId()));
		
		UUIDUnique b = new UUIDUnique();
		list.add(b);
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertEquals(2, asMap2.size());
		Assert.assertTrue(a == asMap2.get(a.getId()));
		Assert.assertTrue(b == asMap2.get(b.getId()));
		
		list.add(b);
		list.add(new UUIDUnique() {
			@Override
			public UUID getId() {
				return null;
			}
		});
		asMap2 = Iterables.asMap(list);
		Assert.assertNotNull(asMap2);
		Assert.assertEquals(2, asMap2.size());
		Assert.assertTrue(a == asMap2.get(a.getId()));
		Assert.assertTrue(b == asMap2.get(b.getId()));
	}
	
	@Test
	public void testareEqual() {
		Assert.assertTrue(Iterables.areEqual(null, null));
		Assert.assertFalse(Iterables.areEqual(null, UUID.randomUUID()));
		Assert.assertFalse(Iterables.areEqual(UUID.randomUUID(), null));
		Assert.assertFalse(Iterables.areEqual(
			UUID.randomUUID(),
			UUID.randomUUID()
		));
		UUID uuid = UUID.randomUUID();
		Assert.assertTrue(Iterables.areEqual(uuid, uuid));
	}
	
	@Test
	public void testContains() {
		Assert.assertFalse(Iterables.contains(null, null));
		Assert.assertFalse(Iterables.contains(null, UUID.randomUUID()));
		Assert.assertFalse(
			Iterables.contains(new GenericIterable<UUID>(null), null)
		);
		Assert.assertFalse(Iterables.contains(
			new GenericIterable<UUID>(null),
			UUID.randomUUID())
		);
		Assert.assertFalse(Iterables.contains(
			Collections.<UUID>emptyList(),
			UUID.randomUUID())
		);
		Collection<UUID> list = new ArrayList<UUID>();
		Assert.assertFalse(Iterables.contains(list, null));
		Assert.assertFalse(Iterables.contains(list, UUID.randomUUID()));

		list.add(UUID.randomUUID());
		Assert.assertFalse(Iterables.contains(list, null));
		Assert.assertFalse(Iterables.contains(list, UUID.randomUUID()));
		
		UUID uuid = UUID.randomUUID();
		list.add(uuid);
		Assert.assertFalse(Iterables.contains(list, null));
		Assert.assertFalse(Iterables.contains(list, UUID.randomUUID()));
		Assert.assertTrue(Iterables.contains(list, uuid));
	}
	
	@Test
	public void testGetSize() {
		Assert.assertEquals(0, Iterables.getSize(null));
		Collection<UUID> list = new ArrayList<UUID>();
		Assert.assertEquals(list.size(), Iterables.getSize(list));
		list.add(UUID.randomUUID());
		Assert.assertEquals(list.size(), Iterables.getSize(list));
		list.add(UUID.randomUUID());
		Assert.assertEquals(list.size(), Iterables.getSize(list));
		list.add(UUID.randomUUID());
		Assert.assertEquals(list.size(), Iterables.getSize(list));
	}
	
	@Test
	public void testMerge() {
		Map<UUID, UUID> stored = new HashMap<UUID, UUID>();
		Map<UUID, UUID> updates = new HashMap<UUID, UUID>();
		stored.put(UUID.randomUUID(), UUID.randomUUID());
		Iterables.merge(stored, null);
		Assert.assertTrue(stored.isEmpty());
		
		stored.put(UUID.randomUUID(), UUID.randomUUID());
		Iterables.merge(stored, updates);
		Assert.assertTrue(stored.isEmpty());
		
		stored.put(UUID.randomUUID(), UUID.randomUUID());
		UUID uuid = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		stored.put(uuid2, uuid2);
		updates.put(uuid, uuid);
		updates.put(uuid2, uuid2);
		Iterables.merge(stored, updates);
		Assert.assertEquals(2, stored.size());
		Assert.assertEquals(uuid, stored.get(uuid));
		Assert.assertEquals(uuid2, stored.get(uuid2));
	}
	
	@Test
	public void testAsSet() {
		Assert.assertNotNull(Iterables.asSet(null));
		Assert.assertTrue(Iterables.asSet(null).isEmpty());
		UUID s1 = UUID.randomUUID();
		UUID s2 = UUID.randomUUID();
		UUID s3 = new UUID(
			s1.getMostSignificantBits(),
			s1.getLeastSignificantBits()
		);
		Set<UUID> set = Iterables.asSet(new UUID[] { s1, s2, s3 });
		Assert.assertEquals(2, set.size());
		Assert.assertTrue(set.contains(s1));
		Assert.assertTrue(set.contains(s3));
		Assert.assertTrue(set.contains(s2));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAsSetIllegal() {
		Iterables.asSet(new String[] { UUID.randomUUID().toString(), null });
	}
	
	@Test
	public void testAsList() {
		Assert.assertNotNull(Iterables.asList((Object[])null));
		Assert.assertNotNull(Iterables.asList((Object[])null, false));
		Assert.assertNotNull(Iterables.asList((Object[])null, true));
		Assert.assertNotNull(Iterables.asList(new UUID[0]));
		Assert.assertNotNull(Iterables.asList(new UUID[0], false));
		Assert.assertNotNull(Iterables.asList(new UUID[0], true));
		
		UUID id1 = UUID.randomUUID();
		Collection<UUID> list = Iterables.asList(new UUID[] { id1 });
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(list.contains(id1));
		list = Iterables.asList(new UUID[] { id1 }, true);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(list.contains(id1));		
		list = Iterables.asList(new UUID[] { id1 }, false);
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(list.contains(id1));
		
		UUID id2 = UUID.randomUUID();
		list = Iterables.asList(new UUID[] { id1, id2 });
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		list = Iterables.asList(new UUID[] { id1, id2 }, true);
		Assert.assertNotNull(list);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		list = Iterables.asList(new UUID[] { id1, id2 }, false);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));

		UUID id3 = new UUID(
			id1.getMostSignificantBits(),
			id1.getLeastSignificantBits()
		);
		list = Iterables.asList(new UUID[] { id1, id2, id3 });
		Assert.assertNotNull(list);
		Assert.assertEquals(3, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		Assert.assertTrue(list.contains(id3));
		list = Iterables.asList(new UUID[] { id1, id2, id3 }, true);
		Assert.assertNotNull(list);
		Assert.assertEquals(3, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		Assert.assertTrue(list.contains(id3));

		list = Iterables.asList(new UUID[] { id1, id2, id3 }, false);
		Assert.assertEquals(3, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		Assert.assertTrue(list.contains(id3));
		
		list = Iterables.asList(new UUID[] { id1, id2, id3, null });
		Assert.assertNotNull(list);
		Assert.assertEquals(4, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		Assert.assertTrue(list.contains(id3));
		
		list = Iterables.asList(new UUID[] { id1, id2, id3, null }, true);
		Assert.assertNotNull(list);
		Assert.assertEquals(4, list.size());
		Assert.assertTrue(list.contains(id1));
		Assert.assertTrue(list.contains(id2));
		Assert.assertTrue(list.contains(id3));
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAsListIllegal() {
		Iterables.asList(new UUID[] {
			UUID.randomUUID(),
			null
		}, false);
	}
	
	@Test
	public void testIsNullOrEmpty() {
		Assert.assertTrue(Iterables.isNullOrEmpty(new Batch<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}
			
		}));
		Assert.assertTrue(Iterables.isNullOrEmpty((Iterable<Object>) new Batch<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}
			
		}));
		Assert.assertFalse(Iterables.isNullOrEmpty(new Batch<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return Collections.emptyIterator();
			}

			@Override
			public int size() {
				return 1;
			}
			
		}));
		Assert.assertFalse(Iterables.isNullOrEmpty((Iterable<Object>)new Batch<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return Collections.emptyIterator();
			}

			@Override
			public int size() {
				return 1;
			}
			
		}));
		Assert.assertTrue(
			Iterables.isNullOrEmpty((Batch<Object>) null)
		);
	}
	
	@Test
	public void testAddAllIterable() {
		Iterables.addAll(null, (Iterable<?>) null);
		Iterables.addAll(Collections.emptyList(), (Iterable<?>) null);
		Iterables.addAll(null, Collections.emptyList());
		Iterables.addAll(Collections.emptyList(), Collections.emptyList());
		ArrayList<Object> c = new ArrayList<>();
		ArrayList<Object> i = new ArrayList<>();
		Object o = new Object();
		i.add(o);
		Iterables.addAll(c, i);
		Assert.assertEquals(1, c.size());
		Assert.assertEquals(o, c.get(0));
	}
	
	@Test
	public void testAddAllEnumeration() {
		Iterables.addAll(null, (Enumeration<?>) null);
		Iterables.addAll(Collections.emptyList(), (Enumeration<?>) null);
		Iterables.addAll(null, Collections.emptyEnumeration());
		Iterables.addAll(Collections.emptyList(), Collections.emptyEnumeration());
		ArrayList<Object> c = new ArrayList<>();
		Vector<Object> i = new Vector<>();
		Object o = new Object();
		i.add(o);
		Iterables.addAll(c, i.elements());
		Assert.assertEquals(1, c.size());
		Assert.assertEquals(o, c.get(0));
	}
	
	@Test
	public void testAddAllArray() {
		Iterables.addAll(null, (Object[]) null);
		Iterables.addAll(Collections.emptyList(), (Object[]) null);
		Iterables.addAll(null, new Object[0]);
		Iterables.addAll(Collections.emptyList(), new Object[0]);
		ArrayList<Object> c = new ArrayList<>();
		Object o = new Object();
		Object[] i = new Object[] { o };
		Iterables.addAll(c, i);
		Assert.assertEquals(1, c.size());
		Assert.assertEquals(o, c.get(0));
	}
	
	@Test
	public void testIsNullOrEmptyArray() {
		Assert.assertTrue(Iterables.isNullOrEmpty((Object[]) null));
		Assert.assertTrue(Iterables.isNullOrEmpty(new Object[0]));
		Assert.assertFalse(Iterables.isNullOrEmpty(new Object[] { this }));
	}
	
	@Override
	protected Class<Iterables> getCoverageClass() {
		return Iterables.class;
	}
	
	private static <T> Iterable<T> wrap(Iterable<T> it) {
		return new CollectionIterable<T>(it);
	}
	
	public static class GenericIterable<T> implements Iterable<T> {
		
		private final Iterator<T> it;
		
		public GenericIterable(Iterator<T> it) {
			this.it = it;
		}

		@Override
		public Iterator<T> iterator() {
			return it;
		}
		
	}
	
	public static class CollectionIterable<T> implements Iterable<T> {
		
		private final Iterable<T> it;
		
		public CollectionIterable(Iterable<T> it) {
			this.it = it;
		}

		@Override
		public Iterator<T> iterator() {
			if (it == null) {
				return null;
			}
			return it.iterator();
		}
	}
	
	public static class UUIDUnique implements Unique<UUID> {
		
		private static int ctr = 1;
		private final UUID uuid = UUID.randomUUID();
		private boolean isUntouched = true;
		private final int cnt = (++ctr);

		@Override
		public UUID getId() {
			return uuid;
		}
		
		public boolean isUntouched() {
			return isUntouched;
		}
		
		@Override
		public String toString() {
			return String.valueOf(cnt);
		}
	}

}
