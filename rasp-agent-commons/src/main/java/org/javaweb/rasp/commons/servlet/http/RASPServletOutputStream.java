package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.ServletOutputStreamProxy;

import java.io.IOException;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.*;

public class RASPServletOutputStream implements ServletOutputStreamProxy {

	private final Object servletOutputStream;

	private final Class<?> servletOutputStreamClass;

	public RASPServletOutputStream(Object servletOutputStream) {
		this.servletOutputStream = servletOutputStream;
		this.servletOutputStreamClass = servletOutputStream.getClass();
	}

	@Override
	public Object __getServletOutputStream() {
		return servletOutputStream;
	}

	@Override
	public Class<?> __getServletOutputStreamClass() {
		return servletOutputStreamClass;
	}

	@Override
	public void print(String s) throws IOException {
		invokeProxyMethod(servletOutputStream, "print", STRING_CLASS_ARG, s);
	}

	@Override
	public void println(String s) throws IOException {
		invokeProxyMethod(servletOutputStream, "println", STRING_CLASS_ARG, s);
	}

	@Override
	public void write(int b) throws IOException {
		invokeProxyMethod(servletOutputStream, "write", INT_CLASS_ARG, b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		invokeProxyMethod(servletOutputStream, "write", BYTE_ARRAY_CLASS_ARG, new Object[]{b});
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		invokeProxyMethod(servletOutputStream, "write", STREAM_CLASS_ARG, b, off, len);
	}

	@Override
	public void flush() throws IOException {
		invokeProxyMethod(servletOutputStream, "flush");
	}

	@Override
	public void close() throws IOException {
		invokeProxyMethod(servletOutputStream, "close");
	}

}
