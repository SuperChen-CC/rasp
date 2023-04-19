package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;

import static org.javaweb.rasp.commons.config.RASPWhitelist.isWhitelistRequest;

public abstract class RASPRequestContext extends RASPContext {

	/**
	 * 请求的URL地址
	 */
	private final String requestPath;

	/**
	 * 是否是白名单
	 */
	private final boolean whitelist;

	public RASPRequestContext(MethodHookEvent event, String contextPath, String requestPath) {
		super(event, contextPath);
		this.requestPath = requestPath;
		this.whitelist = isWhitelistRequest(this);
	}

	/**
	 * 获取请求的URL地址
	 *
	 * @return requestPath
	 */
	@Override
	public String getRequestPath() {
		return requestPath;
	}

	/**
	 * 请求的URL是否是白名单
	 *
	 * @return 是否是白名单
	 */
	@Override
	public boolean isWhitelist() {
		return whitelist;
	}

}
