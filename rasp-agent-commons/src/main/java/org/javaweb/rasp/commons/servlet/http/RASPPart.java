package org.javaweb.rasp.commons.servlet.http;

import org.javaweb.rasp.commons.servlet.PartProxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static org.javaweb.rasp.commons.utils.ReflectionUtils.STRING_CLASS_ARG;
import static org.javaweb.rasp.commons.utils.ReflectionUtils.invokeProxyMethod;

public class RASPPart implements PartProxy {

	private final Object part;

	private final Class<?> partClass;

	public RASPPart(Object part) {
		this.part = part;
		this.partClass = part.getClass();
	}

	@Override
	public Object __getPart() {
		return part;
	}

	@Override
	public Class<?> __getPartClass() {
		return partClass;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return invokeProxyMethod(part, "getInputStream");
	}

	@Override
	public String getContentType() {
		return invokeProxyMethod(part, "getContentType");
	}

	@Override
	public String getName() {
		return invokeProxyMethod(part, "getName");
	}

	@Override
	public String getSubmittedFileName() {
		return invokeProxyMethod(part, "getSubmittedFileName");
	}

	@Override
	public long getSize() {
		return invokeProxyMethod(part, "getSize");
	}

	@Override
	public void write(String fileName) throws IOException {
		invokeProxyMethod(part, "write", STRING_CLASS_ARG, fileName);
	}

	@Override
	public void delete() throws IOException {
		invokeProxyMethod(part, "delete");
	}

	@Override
	public String getHeader(String name) {
		return invokeProxyMethod(part, "getHeader", STRING_CLASS_ARG, name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return invokeProxyMethod(part, "getHeaders", STRING_CLASS_ARG, name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return invokeProxyMethod(part, "getHeaderNames");
	}

}
