package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.CookieProxy;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.STRING_CLASS_ARG;
import static org.javaweb.rasp.commons.utils.ReflectionUtils.invokeProxyMethod;

public class RASPCookie implements CookieProxy {

	private final Object cookie;

	private final Class<?> cookieClass;

	@Override
	public Class<?> __getCookieClass() {
		return cookieClass;
	}

	public RASPCookie(Object cookie) {
		this.cookie = cookie;
		this.cookieClass = this.cookie.getClass();
	}

	@Override
	public String getName() {
		return invokeProxyMethod(cookie, "getName");
	}

	@Override
	public String getValue() {
		return invokeProxyMethod(cookie, "getValue");
	}

	@Override
	public void setValue(String newValue) {
		invokeProxyMethod(cookie, "setValue", STRING_CLASS_ARG, newValue);
	}

}
