package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.HttpSessionProxy;
import org.javaweb.rasp.commons.servlet.ServletContextProxy;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.STRING_CLASS_ARG;
import static org.javaweb.rasp.commons.utils.ReflectionUtils.invokeProxyMethod;

public class RASPHttpSession implements HttpSessionProxy {

	private final Object session;

	private final Class<?> sessionClass;

	public RASPHttpSession(Object session) {
		this.session = session;
		this.sessionClass = this.session.getClass();
	}

	@Override
	public Object __getSession() {
		return session;
	}

	@Override
	public Class<?> __getSessionClass() {
		return sessionClass;
	}

	@Override
	public ServletContextProxy getServletContext() {
		Object obj = invokeProxyMethod(session, "getServletContext");

		if (obj != null) {
			return new RASPServletContext(obj);
		}

		return null;
	}

	@Override
	public Object getAttribute(String name) {
		return invokeProxyMethod(session, "getAttribute", STRING_CLASS_ARG, name);
	}

}
