package org.javaweb.rasp.commons.log;

import org.javaweb.rasp.commons.attack.RASPAttackInfo;
import org.javaweb.rasp.commons.context.RASPContext;
import org.javaweb.rasp.commons.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.TimeZone;

import static java.lang.System.currentTimeMillis;
import static org.javaweb.rasp.commons.config.RASPConfiguration.AGENT_PROPERTIES;
import static org.javaweb.rasp.commons.utils.StringUtils.checkMaxLength;

public class RASPAttackLog implements Serializable {

	private static final String TIME_ZONE = TimeZone.getDefault().getID();

	protected static int MAX_LENGTH = 5000;

	@SerializedName("silent")
	protected final boolean silent;

	@SerializedName("log_version")
	private final String logVersion;

	@SerializedName("app_id")
	private final String appId;

	@SerializedName("time")
	private final long time;

	@SerializedName("timezone")
	private final String timezone;

	@SerializedName("attack_block_request")
	protected boolean attackBlockRequest;

	@SerializedName("attack_parameter")
	protected String attackParameter;

	@SerializedName("attack_values")
	protected String[] attackValues;

	@SerializedName("attack_position")
	protected String attackPosition;

	@SerializedName("attack_type")
	protected String attackType;

	@SerializedName("attack_hash")
	protected String attackHash;

	@SerializedName("hook_class_name")
	protected String hookClassName;

	@SerializedName("hook_method_name")
	protected String hookMethodName;

	@SerializedName("hook_method_args_desc")
	protected String hookMethodArgsDesc;

	@SerializedName("hook_trace_elements")
	protected String hookTraceElements;

	public RASPAttackLog(RASPContext context, RASPAttackInfo attack) {
		this.logVersion = AGENT_PROPERTIES.getLogVersion();
		this.appId = context.getAppProperties().getAppID();
		this.time = currentTimeMillis();
		this.timezone = TIME_ZONE;
		this.silent = context.isSilent();

		if (attack == null) {
			return;
		}

		boolean blockRequest = attack.isBlockRequest();
		this.attackBlockRequest = (!blockRequest || !silent) && blockRequest;
		this.attackParameter = checkMaxLength(attack.getParameter(), MAX_LENGTH);
		this.attackValues = checkMaxLength(attack.getValues(), MAX_LENGTH);
		this.attackPosition = attack.getPosition().name();
		this.attackType = attack.getType();
		this.attackHash = attack.getAttackHash();
		this.hookTraceElements = attack.getTraceElements();
	}

	public String getLogVersion() {
		return logVersion;
	}

	public String getAppId() {
		return appId;
	}

	public long getTime() {
		return time;
	}

	public String getTimezone() {
		return timezone;
	}

}
