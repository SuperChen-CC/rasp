package org.javaweb.rasp.commons.log;

import org.javaweb.rasp.commons.context.RASPContext;
import org.javaweb.rasp.commons.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;

public class RASPAccessLog implements Serializable {

	private static final String TIME_ZONE = TimeZone.getDefault().getID();

	/**
	 * 请求URL
	 */
	@SerializedName("request_url")
	private final String requestUrl;

	@SerializedName("remote_ip")
	private final String remoteIp;

	@SerializedName("request_time")
	private final long requestTime;

	@SerializedName("timezone")
	private final String timezone = TIME_ZONE;

	public RASPAccessLog(RASPContext context) {
		this.requestUrl = context.getRequestPath();
		this.remoteIp = context.getRequestIP();
		this.requestTime = currentTimeMillis();
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public String getTimezone() {
		return timezone;
	}

}
