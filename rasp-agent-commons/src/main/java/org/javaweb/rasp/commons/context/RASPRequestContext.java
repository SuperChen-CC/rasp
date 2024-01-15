package org.javaweb.rasp.commons.context;

import org.javaweb.rasp.commons.MethodHookEvent;
import org.javaweb.rasp.commons.config.RASPWhitelist;
import org.javaweb.rasp.commons.utils.StringUtils;

import static org.javaweb.rasp.commons.config.RASPWhitelist.getWhitelistIndexOfRequest;

public abstract class RASPRequestContext extends RASPContext {

	/**
	 * 请求的URL地址
	 */
	private final String requestPath;

	/**
	 * 是否是白名单
	 */
	private final boolean whitelist;

	/**
	 * 白名单攻击类型
	 */
	private final String attackTypeWhitelist;

	public RASPRequestContext(MethodHookEvent event, String contextPath, String requestPath) {
		super(event, contextPath);
		this.requestPath = requestPath;
		int index = getWhitelistIndexOfRequest(this);
		this.whitelist = index >= 0;
		this.attackTypeWhitelist = RASPWhitelist.getAttackTypeWhitelist(this, index);
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


	/**
	 * 获取白名单 攻击类型
	 */
	public String[] getAttackTypeWhitelist() {
		if (StringUtils.isEmpty(attackTypeWhitelist)) {
			return new String[0];
		}
		return attackTypeWhitelist.split(",");
	}
}
