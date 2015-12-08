package com.dynatrace.mom.users;


public final class Permission extends java.security.Permission {

	private static final long serialVersionUID = 1L;

	public Permission(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean implies(final java.security.Permission permission) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Permission other = (Permission) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getActions() {
		return null;
	}

}
