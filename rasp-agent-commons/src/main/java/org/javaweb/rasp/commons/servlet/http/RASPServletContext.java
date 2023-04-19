package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.ServletContextProxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.*;

public class RASPServletContext implements ServletContextProxy {

	private final Object context;

	private final Class<?> contextClass;

	public RASPServletContext(Object servletContext) {
		this.context = servletContext;
		this.contextClass = this.context.getClass();
	}

	public Object __getContext() {
		return context;
	}

	public Class<?> __getContextClass() {
		return contextClass;
	}

	@Override
	public ServletContextProxy getContext(String uriPath) {
		Object obj = invokeProxyMethod(context, "getContext", STRING_CLASS_ARG, uriPath);

		if (obj != null) {
			return new RASPServletContext(obj);
		}

		return null;
	}

	@Override
	public String getRealPath(String path) {
		return invokeProxyMethod(context, "getRealPath", STRING_CLASS_ARG, path);
	}

	@Override
	public String getServerInfo() {
		return invokeProxyMethod(context, "getServerInfo");
	}

	@Override
	public Object getAttribute(String name) {
		return invokeProxyMethod(context, "getAttribute", STRING_CLASS_ARG, name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return invokeProxyMethod(context, "getAttributeNames");
	}

	@Override
	public void setAttribute(String name, Object object) {
		invokeProxyMethod(context, "setAttribute", STRING_OBJECT_CLASS_ARG, name, object);
	}

	@Override
	public void removeAttribute(String name) {
		invokeProxyMethod(context, "removeAttribute", STRING_CLASS_ARG, name);
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		return invokeProxyMethod(context, "getResource", STRING_CLASS_ARG, path);
	}

}
