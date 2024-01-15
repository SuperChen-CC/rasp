package org.javaweb.rasp.commons.attack;

import java.rasp.proxy.loader.RASPModuleType;
import java.util.Arrays;

import static org.javaweb.rasp.commons.attack.RASPAlertType.ATTACK;
import static org.javaweb.rasp.commons.attack.RASPAlertType.RULES;
import static org.javaweb.rasp.commons.utils.StringUtils.*;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_NAME;
import static org.javaweb.rasp.loader.AgentConstants.AGENT_PROXY_PACKAGE_PREFIX;

/**
 * Web攻击详情信息
 * Created by yz on 2017/2/1.
 *
 * @author yz
 */
public class RASPAttackInfo {

	/**
	 * 攻击类型,如: SQL注入文件上传
	 */
	private String type;

	/**
	 * 日志类型：0=测试，1=警告，2=漏洞，3=攻击, 4=规则
	 */
	private int alertType;

	/**
	 * 攻击参数
	 */
	private String parameter;

	/**
	 * 攻击的具体参数值
	 */
	private String[] values;

	/**
	 * 风险代码
	 */
	private String code;

	/**
	 * 发现攻击的具体位置,如: HEADER
	 */
	private RASPPosition position;

	/**
	 * 是否阻断请求
	 */
	private boolean blockRequest;

	/**
	 * Hook调用链
	 */
	private String traceElements;

	private String desc;

	private transient RASPModuleType moduleType;

	private transient RASPAlertType raspAlertType;

	/**
	 * 攻击Hash值
	 */
	private final String attackHash = genUUID();

	private static final String HOOK_PROXY_CLASS_NAME = AGENT_PROXY_PACKAGE_PREFIX + "loader.HookProxy";

	private void initTraceElements() {
		StringBuilder       sb       = new StringBuilder();
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();

		boolean traceBegin = false;

		// 移除无用信息，从RASPHookProxy类之后的调用链开始输出
		for (int i = 1; i < elements.length; i++) {
			StackTraceElement traceElement   = elements[i];
			String            traceClassName = traceElement.getClassName();

			if (traceBegin) {
				sb.append(traceClassName)
						.append("#").append(traceElement.getMethodName())
						.append(" (")
						.append(traceElement.getFileName())
						.append(":")
						.append(traceElement.getLineNumber()).append(")")
						.append("\r\n");
			}

			if (traceClassName.equals(HOOK_PROXY_CLASS_NAME)) {
				traceBegin = true;
			}
		}

		this.traceElements = sb.toString();
	}

	/**
	 * 获取攻击类型
	 *
	 * @return 攻击类型
	 */
	public RASPModuleType getModuleType() {
		return moduleType;
	}

	public String getType() {
		return type;
	}

	public int getAlertType() {
		return alertType;
	}

	/**
	 * 获取攻击参数
	 *
	 * @return 攻击参数
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * 获取攻击参数值
	 *
	 * @return 攻击参数值
	 */
	public String[] getValues() {
		return values;
	}

	/**
	 * 获取攻击发生的位置
	 *
	 * @return 攻击发生位置
	 */
	public RASPPosition getPosition() {
		return position;
	}

	public boolean isBlockRequest() {
		return blockRequest;
	}

	public String getTraceElements() {
		return traceElements;
	}

	public String getAttackHash() {
		return attackHash;
	}

	public String getDesc() {
		return desc;
	}

	public String getCode() {
		return code;
	}

	public static class Builder {

		private final transient RASPAttackInfo attack;

		public Builder() {
			this.attack = new RASPAttackInfo();
		}

		public Builder bindParameter(String parameter, String value, boolean block) {
			return bindParameter(parameter, value, null, block);
		}

		public Builder bindParameters(String parameter, String[] values, boolean block) {
			return bindParameters(parameter, values, null, block);
		}

		public Builder bindParameter(String parameter, String value, String code, boolean block) {
			return bindParameters(parameter, value != null ? new String[]{value} : null, code, block);
		}

		public Builder bindType(RASPModuleType moduleType, RASPAlertType alertType) {
			return bindType(RASPPosition.UNDEFINED, moduleType, alertType);
		}

		public Builder bindDesc(String desc, boolean blockRequest) {
			return bindDesc(null, desc, blockRequest);
		}

		public Builder bindParameters(String parameter, String[] values, String code, boolean block) {
			attack.parameter = parameter;
			attack.values = values;
			attack.code = code;
			attack.blockRequest = block;

			return this;
		}

		public Builder bindType(RASPPosition position, RASPModuleType moduleType, RASPAlertType alertType) {
			attack.position = position;
			attack.moduleType = moduleType;
			attack.raspAlertType = alertType;

			return this;
		}

		public Builder bindDesc(String code, String desc, boolean blockRequest) {
			attack.code = code;
			attack.desc = desc;
			attack.blockRequest = blockRequest;

			return this;
		}

		public Builder bindCode(String code, boolean blockRequest) {
			attack.code = code;
			attack.blockRequest = blockRequest;

			return this;
		}

		public Builder code(String code) {
			attack.code = code;

			return this;
		}

		public Builder desc(String desc) {
			attack.desc = desc;

			return this;
		}

		public Builder blockRequest(boolean block) {
			attack.blockRequest = block;

			return this;
		}

		// 构建对象的方法
		public RASPAttackInfo build() {
			// 检测values空值
			if (attack.values == null) attack.values = new String[0];

			attack.type = attack.moduleType.getModuleName();
			attack.alertType = attack.raspAlertType.getValue();

			// 根据告警类型判断是否是攻击
			boolean isAttack = attack.raspAlertType == ATTACK || attack.raspAlertType == RULES;

			// 漏洞/攻击描述信息
			if (isEmpty(attack.desc)) {
				StringBuilder sb = new StringBuilder(AGENT_NAME);

				sb.append("检测到[")
						.append(attack.moduleType.getModuleDesc())
						.append("]")
						.append(isAttack ? "攻击" : "漏洞");

				if (isNotEmpty(attack.parameter)) {
					sb.append("，参数名称：[").append(attack.parameter).append("]");
				}

				if (attack.values.length > 0) {
					sb.append("，参数值：[")
							.append(attack.values.length == 1 ? attack.values[0] : Arrays.toString(attack.values))
							.append("]");
				}

				if (isNotEmpty(attack.code)) {
					sb.append("，风险代码：[").append(attack.code).append("]");
				}

				this.attack.desc = sb.toString();
			}

			// 检测当前防御模块是否需要打印调用链
			if (attack.moduleType.isPrintTrace()) {
				attack.initTraceElements();
			}

			return attack;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RASPAttackInfo that = (RASPAttackInfo) o;

		if (alertType != that.alertType) return false;
		if (blockRequest != that.blockRequest) return false;
		if (!type.equals(that.type)) return false;
		if (parameter != null ? !parameter.equals(that.parameter) : that.parameter != null) return false;

		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(values, that.values);
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + alertType;
		result = 31 * result + (parameter != null ? parameter.hashCode() : 0);
		result = 31 * result + Arrays.hashCode(values);
		result = 31 * result + (blockRequest ? 1 : 0);

		return result;
	}

}
