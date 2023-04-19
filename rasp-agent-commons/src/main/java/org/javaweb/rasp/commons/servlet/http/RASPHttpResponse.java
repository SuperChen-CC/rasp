package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.HttpServletResponseProxy;
import org.javaweb.rasp.commons.servlet.ServletOutputStreamProxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.*;

public class RASPHttpResponse implements HttpServletResponseProxy {

	private final Object response;

	private final Class<?> responseClass;

	public RASPHttpResponse(Object response) {
		this.response = response;
		this.responseClass = response.getClass();
	}

	@Override
	public Object __getResponse() {
		return response;
	}

	@Override
	public Class<?> __getResponseClass() {
		return responseClass;
	}

	@Override
	public void setHeader(String name, String value) {
		invokeProxyMethod(response, "setHeader", STRING_STRING_CLASS_ARG, name);
	}

	@Override
	public void setStatus(int sc, String sm) {
		invokeProxyMethod(response, "setStatus", INT_STRING_CLASS_ARG, sc, sm);
	}

	@Override
	public int getStatus() {
		Object obj = invokeProxyMethod(response, "getStatus");

		if (obj != null) {
			return (Integer) obj;
		}

		return -1;
	}

	@Override
	public void setStatus(int sc) {
		invokeProxyMethod(response, "setStatus", INT_CLASS_ARG, sc);
	}

	@Override
	public String getHeader(String name) {
		return invokeProxyMethod(response, "getHeader", STRING_CLASS_ARG, name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return invokeProxyMethod(response, "getHeaderNames");
	}

	@Override
	public String getCharacterEncoding() {
		return invokeProxyMethod(response, "getCharacterEncoding");
	}

	@Override
	public String getContentType() {
		return invokeProxyMethod(response, "getContentType");
	}

	@Override
	public void setContentType(String type) {
		invokeProxyMethod(response, "setContentType", STRING_CLASS_ARG, type);
	}

	@Override
	public ServletOutputStreamProxy getOutputStream() throws IOException {
		Object obj = invokeProxyMethod(response, "getOutputStream");

		if (obj != null) {
			return new RASPServletOutputStream(obj);
		}

		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return invokeProxyMethod(response, "getWriter");
	}

	@Override
	public boolean isCommitted() {
		return invokeProxyMethod(response, "isCommitted");
	}

	@Override
	public void reset() {
		invokeProxyMethod(response, "reset");
	}

}
