package com.dynatrace.mom.web.breadcrumbs;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author reinhard.pilz@dynatrace.com
 *
 */
public final class BreadCrumbs implements Iterable<BreadCrumbs> {

	private String label = null;
	private String link = null;
	private BreadCrumbs next = null;
	private BreadCrumbs prev = null;
	private boolean useParent = true;
	
	public BreadCrumbs(String label, String link) {
		this(label, link, true);
	}
	
	public BreadCrumbs(String label, String link, boolean useParent) {
		this.label = label;
		this.link = link;
		this.useParent = useParent;
	}
	
	public BreadCrumbs(String link) {
		this(link, true);
	}
	
	public BreadCrumbs(String link, boolean useParent) {
		this(null, link, useParent);
	}
	
	/**
	 * 
	 * @param contextPath
	 * @return
	 */
	private static String getRoot() {
		return "";
	}
	
	/**
	 * 
	 * @param contextPath
	 * @return
	 */
	public static final BreadCrumbs live() {
		return new BreadCrumbs("Live", getRoot());
	}

	public static final BreadCrumbs config() {
		return live().add("Configuration", "config", false);
	}
	
	/**
	 * 
	 * @param contextPath
	 * @return
	 */
	public static final BreadCrumbs servers() {
		return live().add("Servers", "servers");
	}

	public static final BreadCrumbs storage() {
		return config().add("Storage", "storage");
	}
	
	/**
	 * 
	 * @return
	 */
	public final boolean hasNext() {
		return this.next != null;
	}
	
	/**
	 * 
	 * @return
	 */
	public final String getLabel() {
		if (label == null) {
			return link;
		}
		return label;
	}
	
	public String getLink() {
		String link = getLink0();
		if (link == null) {
			return null;
		}
		while (link.endsWith("/")) {
			link = link.substring(0, link.length() - 1);
		}
		while (link.startsWith("/")) {
			link = link.substring(1);
		}
		return null;
	}
	
	private String getLink0() {
		if (prev == null) {
			return link;
		}
		if (!useParent) {
			return link;
		}
		String prevLink = prev.getLink();
		if (prevLink == null) {
			return null;
		}
		if (link == null) {
			return prevLink;
		}
		return prevLink + "/" + link;
	}
	
	/**
	 * 
	 * @param link
	 * @return
	 */
	public final BreadCrumbs add(String link) {
		return add(null, link);
	}
	
	public final BreadCrumbs add(String label, String link) {
		return add(label, link, true);
	}
	
	public final BreadCrumbs add(String label, String link, boolean useParent) {
		if (next == null) {
			next = new BreadCrumbs(label, link, useParent);
			next.prev = this;
			return next;
		}
		return next.add(label, link);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<BreadCrumbs> iterator() {
		BreadCrumbs current = this;
		while (current.prev != null) {
			current = current.prev;
		}
		return new BreadCrumbsIterator(current);
	}

	/**
	 * 
	 * @author reinhard.pilz@dynatrace.com
	 *
	 */
	private static class BreadCrumbsIterator implements Iterator<BreadCrumbs> {
		
		private BreadCrumbs current = null;
		
		public BreadCrumbsIterator(final BreadCrumbs current) {
			this.current = current;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final boolean hasNext() {
			return current != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final BreadCrumbs next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final BreadCrumbs result = current;
			current = current.next;
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
