package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.CookieProxy;
import org.javaweb.rasp.commons.servlet.HttpServletRequestProxy;
import org.javaweb.rasp.commons.servlet.ServletContextProxy;
import org.javaweb.rasp.commons.servlet.ServletInputStreamProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.*;

public class RASPHttpRequest implements HttpServletRequestProxy {

	private final Object request;

	private final Class<?> requestClass;

	private final Object[] requestInfo;

	private static final int CONTEXT_PATH = 0;

	private static final int SERVLET_PATH = 1;

	private static final int REMOTE_ADDR = 2;

	private static final int CONTENT_TYPE = 3;

	private static final int METHOD = 4;

	private static final int X_FORWARDED_FOR = 5;

	private static final int USER_AGENT = 6;

	private static final int CONTENT_LENGTH = 7;

	private static final int REQUEST_URI = 8;

	private static final int LOCAL_ADDR = 9;

	public RASPHttpRequest(Object req, Object[] requestInfo) {
		this.request = req;
		this.requestClass = req.getClass();
		this.requestInfo = requestInfo;
	}

	@Override
	public Object __getRequest() {
		return request;
	}

	@Override
	public Class<?> __getRequestClass() {
		return requestClass;
	}

	@Override
	public RASPHttpSession getSession(boolean create) {
		Object obj = invokeProxyMethod(request, "getSession", BOOLEAN_CLASS_ARG, create);

		if (obj != null) {
			return new RASPHttpSession(obj);
		}

		return null;
	}

	@Override
	public RASPHttpSession getSession() {
		return getSession(false);
	}

	@Override
	public CookieProxy[] getCookies() {
		try {
			Object[] obj = invokeProxyMethod(request, "getCookies");

			if (obj != null) {
				RASPCookie[] cookies = new RASPCookie[obj.length];

				for (int i = 0; i < obj.length; i++) {
					cookies[i] = new RASPCookie(obj[i]);
				}

				return cookies;
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	@Override
	public String getHeader(String name) {
		if (requestInfo != null) {
//			if ("x-forwarded-for".equals(name)) {
//				return (String) requestInfo[X_FORWARDED_FOR];
//			} else
			if ("User-Agent".equals(name)) {
				return (String) requestInfo[USER_AGENT];
			}
		}

		return invokeProxyMethod(request, "getHeader", STRING_CLASS_ARG, name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return invokeProxyMethod(request, "getHeaderNames");
	}

	@Override
	public String getMethod() {
		if (requestInfo != null) {
			return (String) requestInfo[METHOD];
		}

		return invokeProxyMethod(request, "getMethod");
	}

	@Override
	public String getContextPath() {
		if (requestInfo != null) {
			return (String) requestInfo[CONTEXT_PATH];
		}

		return invokeProxyMethod(request, "getContextPath");
	}

	@Override
	public String getQueryString() {
		return invokeProxyMethod(request, "getQueryString");
	}

	@Override
	public String getRequestURI() {
		if (requestInfo != null) {
			return (String) requestInfo[REQUEST_URI];
		}

		return invokeProxyMethod(request, "getRequestURI");
	}

	@Override
	public StringBuffer getRequestURL() {
		return invokeProxyMethod(request, "getRequestURL");
	}

	@Override
	public String getServletPath() {
		if (requestInfo != null) {
			return (String) requestInfo[SERVLET_PATH];
		}

		return invokeProxyMethod(request, "getServletPath");
	}

	@Override
	public Object getAttribute(String name) {
		return invokeProxyMethod(request, "getAttribute", STRING_CLASS_ARG, name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		invokeProxyMethod(request, "setAttribute", STRING_OBJECT_CLASS_ARG, name, o);
	}

	@Override
	public ServletContextProxy getServletContext() {
		Object obj = invokeProxyMethod(request, "getServletContext");

		if (obj != null) {
			return new RASPServletContext(obj);
		}

		return null;
	}

	@Override
	public String getContentType() {
		if (requestInfo != null) {
			return (String) requestInfo[CONTENT_TYPE];
		}

		return invokeProxyMethod(request, "getContentType");
	}

	@Override
	public ServletInputStreamProxy getInputStream() throws IOException {
		Object inputStream = invokeProxyMethod(request, "getInputStream");

		if (inputStream != null) {
			return new RASPServletInputStream(inputStream);
		}

		return null;
	}

	@Override
	public String getParameter(String name) {
		return invokeProxyMethod(request, "getParameter", STRING_CLASS_ARG, name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return invokeProxyMethod(request, "getParameterValues", STRING_CLASS_ARG, name);
	}

	@Override
	public String getScheme() {
		return invokeProxyMethod(request, "getScheme");
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return invokeProxyMethod(request, "getParameterMap");
	}

	@Override
	public String getServerName() {
		return invokeProxyMethod(request, "getServerName");
	}

	@Override
	public int getServerPort() {
		return invokeProxyMethod(request, "getServerPort");
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return invokeProxyMethod(request, "getReader");
	}

	@Override
	public String getRemoteAddr() {
		return invokeProxyMethod(request, "getRemoteAddr");
	}

	@Override
	public String getRealPath(String path) {
		return invokeProxyMethod(request, "getRealPath", STRING_CLASS_ARG, path);
	}

	@Override
	public String getLocalAddr() {
		if (requestInfo != null) {
			return (String) requestInfo[LOCAL_ADDR];
		}

		return invokeProxyMethod(request, "getLocalAddr");
	}

	@Override
	public int getContentLength() {
		if (requestInfo != null) {
			return (Integer) requestInfo[CONTENT_LENGTH];
		}

		return invokeProxyMethod(request, "getContentLength");
	}

}