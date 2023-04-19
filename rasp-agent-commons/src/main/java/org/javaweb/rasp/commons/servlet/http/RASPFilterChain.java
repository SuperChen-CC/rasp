package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.FilterChainProxy;

import java.io.IOException;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.invokeProxyMethod;

public class RASPFilterChain implements FilterChainProxy {

	private final Object chain;

	private final Class<?> chainClass;

	private static Class<?>[] argsTypeClass = null;

	public RASPFilterChain(Object chain) {
		this.chain = chain;
		this.chainClass = this.chain.getClass();
	}

	@Override
	public Object __getChain() {
		return chain;
	}

	@Override
	public Class<?> __getChainClass() {
		return chainClass;
	}

	@Override
	public void doFilter(Object request, Object response) throws IOException {
		if (argsTypeClass == null) {
			argsTypeClass = new Class[]{request.getClass(), response.getClass()};
		}

		invokeProxyMethod(chain, "doFilter", argsTypeClass, request, response);
	}

}
