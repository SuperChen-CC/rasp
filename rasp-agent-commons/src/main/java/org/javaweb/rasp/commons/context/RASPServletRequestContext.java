package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.logback.classic.Logger;
import org.javaweb.rasp.commons.servlet.HttpServletRequestProxy;
import org.javaweb.rasp.commons.servlet.HttpServletResponseProxy;

import java.io.File;

public abstract class RASPServletRequestContext extends RASPRequestContext {

	public RASPServletRequestContext(MethodHookEvent event, String contextPath, String requestPath) {
		super(event, contextPath, requestPath);
	}

	public abstract String getUserAgent();

	public abstract boolean isJspFile();

	public abstract String getRequestURI();

	public abstract String getServletPath();

	public abstract ClassLoader getAdapterClassLoader();

	public abstract boolean isWebApiRequest();

	public abstract int getContentLength();

	public abstract int getMaxStreamCacheSize();

	public abstract boolean isJsonRequest();

	public abstract boolean isXmlRequest();

	public abstract boolean isInternalAPIRequest();

	public abstract File getJspFilePath();

	public abstract File getRequestFile();

	public abstract void preJSPRequest();

	public abstract void setJspFilePath(File filePath);

	public abstract File getDocumentRoot();

	public abstract String getQueryString();

	public abstract Logger initTraceLogger();

	public abstract HttpServletRequestProxy getServletRequest();

	public abstract HttpServletResponseProxy getServletResponse();

}
