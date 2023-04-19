package org.javaweb.rasp.commons;

import java.net.URL;
import java.net.URLClassLoader;

public class RASPAdapterClassLoader extends URLClassLoader {

	public RASPAdapterClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

	public RASPAdapterClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public void addURL(URL url) {
		super.addURL(url);
	}

	public Class<?> defineClass(byte[] bytes) {
		return super.defineClass(bytes, 0, bytes.length);
	}

	public Class<?> defineClass(String name, byte[] bytes) {
		return super.defineClass(name, bytes, 0, bytes.length);
	}

}
