package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.ServletInputStreamProxy;
import org.javaweb.rasp.commons.utils.ReflectionUtils;

import java.io.IOException;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.invokeProxyMethod;

public class RASPServletInputStream implements ServletInputStreamProxy {

	private final Object servletInputStream;

	private final Class<?> servletInputStreamClass;

	public RASPServletInputStream(Object servletInputStream) {
		this.servletInputStream = servletInputStream;
		this.servletInputStreamClass = servletInputStream.getClass();
	}

	@Override
	public Object __getServletInputStream() {
		return servletInputStream;
	}

	@Override
	public Class<?> __getServletInputStreamClass() {
		return servletInputStreamClass;
	}

	@Override
	public int read() throws IOException {
		return invokeProxyMethod(servletInputStream, "read");
	}

	@Override
	public int read(byte[] b) throws IOException {
		return invokeProxyMethod(servletInputStream, "read", ReflectionUtils.BYTE_ARRAY_CLASS_ARG, new Object[]{b});
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return invokeProxyMethod(servletInputStream, "read", ReflectionUtils.STREAM_CLASS_ARG, b, off, len);
	}

	@Override
	public int readLine(byte[] b, int off, int len) throws IOException {
		return invokeProxyMethod(servletInputStream, "readLine", ReflectionUtils.STREAM_CLASS_ARG, b, off, len);
	}

	@Override
	public boolean isFinished() {
		return invokeProxyMethod(servletInputStream, "isFinished");
	}

	@Override
	public boolean isReady() {
		return invokeProxyMethod(servletInputStream, "isReady");
	}

}
