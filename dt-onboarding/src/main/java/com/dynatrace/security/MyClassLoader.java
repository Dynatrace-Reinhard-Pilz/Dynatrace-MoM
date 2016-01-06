package com.dynatrace.security;

public class MyClassLoader extends ClassLoader {
	
	private static final String CLASS_NAME = "com.dynatrace.Decrypter";
	
	public MyClassLoader() {
		super(MyClassLoader.class.getClassLoader());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected java.lang.Class<?> findClass(String name) throws ClassNotFoundException {
		if (!CLASS_NAME.equals(name)) {
	        throw new ClassNotFoundException(name);
		}
		byte[] bytes = new byte[0];
		return defineClass(name, bytes, 0, bytes.length);
	}

}
